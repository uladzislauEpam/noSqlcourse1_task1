package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.epam.mishchenko.ticketbooking.dao.UserDAO;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDAOImpl implements UserDAO {

    private static final String NAMESPACE = "user:";

    private static final Logger LOGGER = LogManager.getLogger(UserDAOImpl.class);

    private Storage storage;

    @Override
    public User getById(long id) {
        LOGGER.log(Level.DEBUG, "Finding a user by id: {}", id);

        String stringUser = storage.getInMemoryStorage().get(NAMESPACE + id);
        if (stringUser == null) {
            LOGGER.log(Level.WARN, "Can not to find a user by id: {}", id);
            throw new DbException("Can not to find a user by id: " + id);
        }

        User user = parseFromStringToUser(stringUser);

        LOGGER.log(Level.DEBUG, "The user with id {} successfully found ", id);
        return user;
    }

    private User parseFromStringToUser(String stringUser) {
        final String delimiterBetweenFields = ",";
        stringUser = removeBrackets(stringUser);
        String[] stringFields = stringUser.split(delimiterBetweenFields);
        return createUserFromStringFields(stringFields);
    }

    private String removeBrackets(String text) {
        text = text.replace("{", "");
        return text.replace("}", "");
    }

    private User createUserFromStringFields(String[] stringFields) {
        int index = 0;
        User user = new UserImpl();
        user.setId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        user.setName(getFieldValueFromFields(stringFields, index++));
        user.setEmail(getFieldValueFromFields(stringFields, index));
        return user;
    }

    private String getFieldValueFromFields(String[] stringFields, int index) {
        final String delimiterBetweenKeyAndValue = " : ";
        return removeSingleQuotesIfExist(stringFields[index].split(delimiterBetweenKeyAndValue)[1]);
    }

    private String removeSingleQuotesIfExist(String text) {
        return text.replaceAll("'", "");
    }

    public User getByEmail(String email) {
        LOGGER.log(Level.DEBUG, "Finding a user by email: {}", email);

        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            String stringUser = storage.getInMemoryStorage().get(id);
            if (emailEquals(stringUser, email)) {
                LOGGER.log(Level.DEBUG, "The user with email {} successfully found ", email);
                return parseFromStringToUser(stringUser);
            }
        }

        LOGGER.log(Level.WARN, "Can not to find a user by email: {}", email);
        throw new DbException("Can not to find a user by email: " + email);
    }

    private List<String> getIdsOfUsers() {
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isUserEntity)
                .collect(Collectors.toList());
    }

    private boolean isUserEntity(String entity) {
        return entity.contains(NAMESPACE);
    }

    private boolean emailEquals(String entity, String email) {
        return entity.contains("'email' : '" + email + "'");
    }

    public List<User> getByName(String name, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG, "Finding all users by name '{}' in the database using pagination", name);

        if (pageSize <= 0 || pageNum <= 0) {
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfUsersByName = getListOfStringUsersByName(name);
        if (stringListOfUsersByName.isEmpty()) {
            throw new DbException("List of all users by name '" + name + "' is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfUsersByName.size()) {
            throw new DbException("The size of users list (size=" + stringListOfUsersByName.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfUsersByNameInRange = stringListOfUsersByName.subList(start, end);
        List<User> listOfUsersByNameInRange = parseFromStringListToUserList(stringListOfUsersByNameInRange);

        LOGGER.log(Level.DEBUG,
                "All users successfully found by name '{}' in the database using pagination",
                name);
        return listOfUsersByNameInRange;
    }

    private List<String> getListOfStringUsersByName(String name) {
        List<String> stringListOfUsersByName = new ArrayList<>();
        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            String stringUser = storage.getInMemoryStorage().get(id);
            if (nameEquals(stringUser, name)) {
                stringListOfUsersByName.add(stringUser);
            }
        }
        return stringListOfUsersByName;
    }

    private List<User> parseFromStringListToUserList(List<String> stringListOfUsers) {
        List<User> users = new ArrayList<>();
        for (String stringUser : stringListOfUsers) {
            users.add(parseFromStringToUser(stringUser));
        }
        return users;
    }

    private boolean nameEquals(String entity, String name) {
        return entity.contains("'name' : '" + name + "'");
    }

    private int getStartIndex(int pageSize, int pageNum) {
        return pageSize * (pageNum - 1);
    }

    private int getEndIndex(int start, int pageSize) {
        return start + pageSize;
    }

    @Override
    public List<User> getAll() {
        LOGGER.log(Level.DEBUG, "Finding all users in the database");

        List<User> listOfAllUsers = new ArrayList<>();
        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            listOfAllUsers.add(parseFromStringToUser(storage.getInMemoryStorage().get(id)));
        }
        if (listOfAllUsers.isEmpty()) {
            throw new DbException("List of users is empty");
        }

        LOGGER.log(Level.DEBUG, "All users successfully found");
        return listOfAllUsers;
    }

    @Override
    public User insert(User user) {
        LOGGER.log(Level.DEBUG, "Start inserting of the user: {}", user);

        if (user == null) {
            throw new DbException("The user can not equal a null");
        }
        if (existsUserByEmail(user)) {
            throw new DbException("This email already exists");
        }
        setIdForUser(user);
        storage.getInMemoryStorage().put(NAMESPACE + user.getId(), user.toString());

        LOGGER.log(Level.DEBUG, "Successfully insertion of the user: {}", user);

        return user;
    }

    private boolean existsUserByEmail(User user) {
        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            String stringUser = storage.getInMemoryStorage().get(id);
            if (emailEquals(stringUser, user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private void setIdForUser(User user) {
        List<String> idsOfUsers = getIdsOfUsers();
        if (idsOfUsers.isEmpty()) {
            user.setId(1L);
            return;
        }
        Collections.sort(idsOfUsers);
        String stringLastUserId = idsOfUsers.get(idsOfUsers.size() - 1);
        long longLastUserId = Long.parseLong(stringLastUserId.split(":")[1]);
        long newId = longLastUserId + 1;
        user.setId(newId);
    }

    @Override
    public User update(User user) {
        LOGGER.log(Level.DEBUG, "Start updating of the user: {}", user);

        if (user == null) {
            throw new DbException("The user can not equal a null");
        }
        if (!isUserExists(user.getId())) {
            throw new DbException("The user with id " + user.getId() + " does not exist");
        }

        storage.getInMemoryStorage().replace(NAMESPACE + user.getId(), user.toString());

        LOGGER.log(Level.DEBUG, "Successfully update of the user: {}", user);

        return user;
    }

    private boolean isUserExists(long id) {
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    @Override
    public boolean delete(long userId) {
        LOGGER.log(Level.DEBUG, "Start deleting of the user with id: {}", userId);

        if (!isUserExists(userId)) {
            throw new DbException("The user with id " + userId + " does not exist");
        }

        String removedUser = storage.getInMemoryStorage().remove(NAMESPACE + userId);

        if (removedUser == null) {
            throw new DbException("The user with id" + userId + " not deleted");
        }

        LOGGER.log(Level.DEBUG, "Successfully deletion of the user with id: {}", userId);

        return true;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}

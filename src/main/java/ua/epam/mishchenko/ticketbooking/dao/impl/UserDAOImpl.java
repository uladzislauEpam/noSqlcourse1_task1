package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.dao.UserDAO;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserDAOImpl implements UserDAO {

    private static final String NAMESPACE = "user:";

    private static final Logger log = LoggerFactory.getLogger(UserDAOImpl.class);

    private Storage storage;

    @Override
    public User getById(long id) {
        log.info("Finding a user by id: {}", id);

        String stringUser = storage.getInMemoryStorage().get(NAMESPACE + id);
        if (stringUser == null) {
            log.warn("Can not to find a user by id: {}", id);
            throw new DbException("Can not to find a user by id: " + id);
        }

        User user = parseFromStringToUser(stringUser);

        log.info("The user with id {} successfully found ", id);
        return user;
    }

    private User parseFromStringToUser(String stringUser) {
        log.debug("Parsing from string ticket to ticket object: {}", stringUser);
        final String delimiterBetweenFields = ",";
        stringUser = removeBrackets(stringUser);
        String[] stringFields = stringUser.split(delimiterBetweenFields);
        return createUserFromStringFields(stringFields);
    }

    private String removeBrackets(String text) {
        log.debug("Removing brackets from string ticket: {}", text);
        text = text.replace("{", "");
        return text.replace("}", "");
    }

    private User createUserFromStringFields(String[] stringFields) {
        log.debug("Creating ticket from string fields: {}", Arrays.toString(stringFields));
        int index = 0;
        User user = new UserImpl();
        user.setId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        user.setName(getFieldValueFromFields(stringFields, index++));
        user.setEmail(getFieldValueFromFields(stringFields, index));
        return user;
    }

    private String getFieldValueFromFields(String[] stringFields, int index) {
        log.debug("Getting field value by index {} from the array: {}", index, Arrays.toString(stringFields));
        final String delimiterBetweenKeyAndValue = " : ";
        return removeSingleQuotesIfExist(stringFields[index].split(delimiterBetweenKeyAndValue)[1]);
    }

    private String removeSingleQuotesIfExist(String text) {
        log.debug("Removing single quotes from string values if exists: {}", text);
        return text.replaceAll("'", "");
    }

    public User getByEmail(String email) {
        log.info("Finding a user by email: {}", email);

        User userByEmail = getUserByEmailFromStorage(email);
        if (userByEmail == null) {
            log.warn("Can not to find a user by email: {}", email);
            throw new DbException("Can not to find a user by email: " + email);
        }
        return userByEmail;
    }

    private User getUserByEmailFromStorage(String email) {
        log.debug("Getting user by email {} from storage", email);
        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            String stringUser = storage.getInMemoryStorage().get(id);
            if (emailEquals(stringUser, email)) {
                log.info("The user with email {} successfully found ", email);
                return parseFromStringToUser(stringUser);
            }
        }
        return null;
    }

    private List<String> getIdsOfUsers() {
        log.debug("Getting all user entities by \"user\" namespace");
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isUserEntity)
                .collect(Collectors.toList());
    }

    private boolean isUserEntity(String entity) {
        log.debug("Checking if entity is a user entity: {}", entity);
        return entity.contains(NAMESPACE);
    }

    private boolean emailEquals(String entity, String email) {
        log.debug("Checking if user entity email {} equals to {}", entity, email);
        return entity.contains("'email' : '" + email + "'");
    }

    public List<User> getByName(String name, int pageSize, int pageNum) {
        log.info("Finding all users by name '{}' in the database using pagination", name);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfUsersByName = getListOfStringUsersByName(name);
        if (stringListOfUsersByName.isEmpty()) {
            log.warn("List of all users by name '{}' is empty", name);
            throw new DbException("List of all users by name '" + name + "' is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfUsersByName.size()) {
            log.warn("The size of events list (size={}) is less than end page (last page={})",
                    stringListOfUsersByName.size(), end);
            throw new DbException("The size of users list (size=" + stringListOfUsersByName.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfUsersByNameInRange = stringListOfUsersByName.subList(start, end);
        List<User> listOfUsersByNameInRange = parseFromStringListToUserList(stringListOfUsersByNameInRange);

        log.info("All users successfully found by name '{}' in the database using pagination", name);
        return listOfUsersByNameInRange;
    }

    private List<String> getListOfStringUsersByName(String name) {
        log.debug("Getting users by name {} from storage", name);
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
        log.debug("Parsing from string list of users to object list of users: {}", stringListOfUsers);
        List<User> users = new ArrayList<>();
        for (String stringUser : stringListOfUsers) {
            users.add(parseFromStringToUser(stringUser));
        }
        return users;
    }

    private boolean nameEquals(String entity, String name) {
        log.debug("Checking if user entity name {} equals to {}", entity, name);
        return entity.contains("'name' : '" + name + "'");
    }

    private int getStartIndex(int pageSize, int pageNum) {
        log.debug("Getting start index by page size = {} and page num = {}", pageSize, pageNum);
        return pageSize * (pageNum - 1);
    }

    private int getEndIndex(int start, int pageSize) {
        log.debug("Getting end index by start index {} and page size {}", start, pageSize);
        return start + pageSize;
    }

    @Override
    public List<User> getAll() {
        log.info("Finding all users in the database");

        List<User> listOfAllUsers = getAllUsersFromStorageByIds();
        if (listOfAllUsers.isEmpty()) {
            log.warn("List of all users is empty");
            throw new DbException("List of users is empty");
        }

        log.info("All users successfully found");
        return listOfAllUsers;
    }

    private List<User> getAllUsersFromStorageByIds() {
        log.debug("Getting all users from storage by ids with \"user\" namespace");
        List<User> listOfAllUsers = new ArrayList<>();
        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            listOfAllUsers.add(parseFromStringToUser(storage.getInMemoryStorage().get(id)));
        }
        return listOfAllUsers;
    }

    @Override
    public User insert(User user) {
        log.info("Start inserting of the user: {}", user);

        if (existsUserByEmail(user)) {
            log.debug("This email already exists");
            throw new DbException("This email already exists");
        }

        setIdForUser(user);
        storage.getInMemoryStorage().put(NAMESPACE + user.getId(), user.toString());

        log.info("Successfully insertion of the user: {}", user);

        return user;
    }

    private boolean existsUserByEmail(User user) {
        log.debug("Checking if user with email {} already exists", user.getEmail());
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
        log.debug("Setting id for user: {}", user);
        List<String> idsOfUsers = getIdsOfUsers();
        if (idsOfUsers.isEmpty()) {
            log.debug("This user is a first object. The new id is 1");
            user.setId(1L);
            return;
        }
        Collections.sort(idsOfUsers);
        String stringLastUserId = idsOfUsers.get(idsOfUsers.size() - 1);
        long longLastUserId = Long.parseLong(stringLastUserId.split(":")[1]);
        long newId = longLastUserId + 1;
        log.debug("The last id of user entity is {}. The new id is {}", stringLastUserId, newId);
        user.setId(newId);
    }

    @Override
    public User update(User user) {
        log.info("Start updating of the user: {}", user);

        if (!isUserExists(user.getId())) {
            log.warn("The user with id {} does not exist", user.getId());
            throw new DbException("The user with id " + user.getId() + " does not exist");
        }
        if (checkIfUserWithExistsEmailEqualsToUpdatingUser(user)) {
            log.debug("This email already exists");
            throw new DbException("This email already exists");
        }

        storage.getInMemoryStorage().replace(NAMESPACE + user.getId(), user.toString());

        log.info("Successfully update of the user: {}", user);

        return user;
    }

    private boolean checkIfUserWithExistsEmailEqualsToUpdatingUser(User user) {
        log.debug("Check if user with exists email equals to updating user");
        try {
            long idUserByEmail = getByEmail(user.getEmail()).getId();
            return idUserByEmail != user.getId();
        } catch (DbException e) {
            return false;
        }
    }

    private boolean isUserExists(long id) {
        log.debug("Checking if id {} exists", id);
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    @Override
    public boolean delete(long userId) {
        log.info("Start deleting of the user with id: {}", userId);

        if (!isUserExists(userId)) {
            log.warn("The user with id {} does not exist", userId);
            throw new DbException("The user with id " + userId + " does not exist");
        }

        String removedUser = storage.getInMemoryStorage().remove(NAMESPACE + userId);

        if (removedUser == null) {
            log.warn("The user with id {} not deleted", userId);
            throw new DbException("The user with id" + userId + " not deleted");
        }

        log.info("Successfully deletion of the user with id: {}", userId);

        return true;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}

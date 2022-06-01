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

/**
 * The type User dao.
 */
public class UserDAOImpl implements UserDAO {

    /**
     * The constant NAMESPACE.
     */
    private static final String NAMESPACE = "user:";

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(UserDAOImpl.class);

    /**
     * The Storage.
     */
    private Storage storage;

    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
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

    /**
     * Parse from string to user user.
     *
     * @param stringUser the string user
     * @return the user
     */
    private User parseFromStringToUser(String stringUser) {
        log.debug("Parsing from string ticket to ticket object: {}", stringUser);
        final String delimiterBetweenFields = ",";
        stringUser = removeBrackets(stringUser);
        String[] stringFields = stringUser.split(delimiterBetweenFields);
        return createUserFromStringFields(stringFields);
    }

    /**
     * Remove brackets string.
     *
     * @param text the text
     * @return the string
     */
    private String removeBrackets(String text) {
        log.debug("Removing brackets from string ticket: {}", text);
        text = text.replace("{", "");
        return text.replace("}", "");
    }

    /**
     * Create user from string fields user.
     *
     * @param stringFields the string fields
     * @return the user
     */
    private User createUserFromStringFields(String[] stringFields) {
        log.debug("Creating ticket from string fields: {}", Arrays.toString(stringFields));
        int index = 0;
        User user = new UserImpl();
        user.setId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        user.setName(getFieldValueFromFields(stringFields, index++));
        user.setEmail(getFieldValueFromFields(stringFields, index));
        return user;
    }

    /**
     * Gets field value from fields.
     *
     * @param stringFields the string fields
     * @param index        the index
     * @return the field value from fields
     */
    private String getFieldValueFromFields(String[] stringFields, int index) {
        log.debug("Getting field value by index {} from the array: {}", index, Arrays.toString(stringFields));
        final String delimiterBetweenKeyAndValue = " : ";
        return removeSingleQuotesIfExist(stringFields[index].split(delimiterBetweenKeyAndValue)[1]);
    }

    /**
     * Remove single quotes if exist string.
     *
     * @param text the text
     * @return the string
     */
    private String removeSingleQuotesIfExist(String text) {
        log.debug("Removing single quotes from string values if exists: {}", text);
        return text.replaceAll("'", "");
    }

    /**
     * Gets by email.
     *
     * @param email the email
     * @return the by email
     */
    public User getByEmail(String email) {
        log.info("Finding a user by email: {}", email);

        User userByEmail = getUserByEmailFromStorage(email);
        if (userByEmail == null) {
            log.warn("Can not to find a user by email: {}", email);
            throw new DbException("Can not to find a user by email: " + email);
        }
        return userByEmail;
    }

    /**
     * Gets user by email from storage.
     *
     * @param email the email
     * @return the user by email from storage
     */
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

    /**
     * Gets ids of users.
     *
     * @return the ids of users
     */
    private List<String> getIdsOfUsers() {
        log.debug("Getting all user entities by \"user\" namespace");
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isUserEntity)
                .collect(Collectors.toList());
    }

    /**
     * Is user entity boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    private boolean isUserEntity(String entity) {
        log.debug("Checking if entity is a user entity: {}", entity);
        return entity.contains(NAMESPACE);
    }

    /**
     * Email equals boolean.
     *
     * @param entity the entity
     * @param email  the email
     * @return the boolean
     */
    private boolean emailEquals(String entity, String email) {
        log.debug("Checking if user entity email {} equals to {}", entity, email);
        return entity.contains("'email' : '" + email + "'");
    }

    /**
     * Gets by name.
     *
     * @param name     the name
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the by name
     */
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

    /**
     * Gets list of string users by name.
     *
     * @param name the name
     * @return the list of string users by name
     */
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

    /**
     * Parse from string list to user list list.
     *
     * @param stringListOfUsers the string list of users
     * @return the list
     */
    private List<User> parseFromStringListToUserList(List<String> stringListOfUsers) {
        log.debug("Parsing from string list of users to object list of users: {}", stringListOfUsers);
        List<User> users = new ArrayList<>();
        for (String stringUser : stringListOfUsers) {
            users.add(parseFromStringToUser(stringUser));
        }
        return users;
    }

    /**
     * Name equals boolean.
     *
     * @param entity the entity
     * @param name   the name
     * @return the boolean
     */
    private boolean nameEquals(String entity, String name) {
        log.debug("Checking if user entity name {} equals to {}", entity, name);
        return entity.contains("'name' : '" + name + "'");
    }

    /**
     * Gets start index.
     *
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the start index
     */
    private int getStartIndex(int pageSize, int pageNum) {
        log.debug("Getting start index by page size = {} and page num = {}", pageSize, pageNum);
        return pageSize * (pageNum - 1);
    }

    /**
     * Gets end index.
     *
     * @param start    the start
     * @param pageSize the page size
     * @return the end index
     */
    private int getEndIndex(int start, int pageSize) {
        log.debug("Getting end index by start index {} and page size {}", start, pageSize);
        return start + pageSize;
    }

    /**
     * Gets all.
     *
     * @return the all
     */
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

    /**
     * Gets all users from storage by ids.
     *
     * @return the all users from storage by ids
     */
    private List<User> getAllUsersFromStorageByIds() {
        log.debug("Getting all users from storage by ids with \"user\" namespace");
        List<User> listOfAllUsers = new ArrayList<>();
        List<String> idsOfUsers = getIdsOfUsers();
        for (String id : idsOfUsers) {
            listOfAllUsers.add(parseFromStringToUser(storage.getInMemoryStorage().get(id)));
        }
        return listOfAllUsers;
    }

    /**
     * Insert user.
     *
     * @param user the user
     * @return the user
     */
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

    /**
     * Exists user by email boolean.
     *
     * @param user the user
     * @return the boolean
     */
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

    /**
     * Sets id for user.
     *
     * @param user the user
     */
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

    /**
     * Update user.
     *
     * @param user the user
     * @return the user
     */
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

    /**
     * Check if user with exists email equals to updating user boolean.
     *
     * @param user the user
     * @return the boolean
     */
    private boolean checkIfUserWithExistsEmailEqualsToUpdatingUser(User user) {
        log.debug("Check if user with exists email equals to updating user");
        try {
            long idUserByEmail = getByEmail(user.getEmail()).getId();
            return idUserByEmail != user.getId();
        } catch (DbException e) {
            return false;
        }
    }

    /**
     * Is user exists boolean.
     *
     * @param id the id
     * @return the boolean
     */
    private boolean isUserExists(long id) {
        log.debug("Checking if id {} exists", id);
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    /**
     * Delete boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
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

    /**
     * Sets storage.
     *
     * @param storage the storage
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}

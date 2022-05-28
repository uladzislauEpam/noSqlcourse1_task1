package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.dao.impl.UserDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDAOImpl userDAO;

    @Override
    public User getUserById(long userId) {
        log.info("Finding a user by id: {}", userId);

        try {
            User user = userDAO.getById(userId);

            log.info("The user with id {} successfully found ", userId);

            return user;
        } catch (DbException e) {
            log.warn("Can not to get an user by id: " + userId);
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("Finding a user by email: {}", email);

        try {
            if (email.isEmpty()) {
                log.warn("The email can not be null");
                return null;
            }

            User user = userDAO.getByEmail(email);

            log.info("The user with email {} successfully found ", email);

            return user;
        } catch (DbException e) {
            log.warn("Can not to get an user by email: " + email);
            return null;
        }
    }

    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        log.info("Finding all users by name {} with page size {} and number of page {}", name, pageSize, pageNum);

        try {
            if (name.isEmpty()) {
                log.warn("The name can not be null");
                return new ArrayList<>();
            }

            List<User> usersByName = userDAO.getByName(name, pageSize, pageNum);

            log.info("All users successfully found by name {} with page size {} and number of page {}",
                    name, pageSize, pageNum);

            return usersByName;
        } catch (DbException e) {
            log.warn("Can not to find a list of users by name '{}'", name, e);
            return new ArrayList<>();
        }
    }

    @Override
    public User createUser(User user) {
        log.info("Start creating an user: {}", user);

        try {
            if (isUserNull(user)) {
                log.warn("The user can not be a null");
                return null;
            }

            user = userDAO.insert(user);

            log.info("Successfully creation of the user: {}", user);

            return user;
        } catch (DbException e) {
            log.warn("Can not to create an user: {}", user, e);
            return null;
        }
    }

    private boolean isUserNull(User user) {
        return user == null;
    }

    @Override
    public User updateUser(User user) {
        log.info("Start updating an user: {}", user);

        try {
            if (isUserNull(user)) {
                log.warn("The user can not be a null");
                return null;
            }

            user = userDAO.update(user);

            log.info("Successfully updating of the user: {}", user);

            return user;
        } catch (DbException e) {
            log.warn("Can not to update an user: {}", user, e);
            return null;
        }
    }

    @Override
    public boolean deleteUser(long userId) {
        log.info("Start deleting an user with id: {}", userId);

        try {
            boolean isRemoved = userDAO.delete(userId);

            log.info("Successfully deletion of the user with id: {}", userId);

            return isRemoved;
        } catch (DbException e) {
            log.warn("Can not to delete an user with id: {}", userId, e);
            return false;
        }
    }

    public void setUserDAO(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }
}

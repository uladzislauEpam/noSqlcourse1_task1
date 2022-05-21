package ua.epam.mishchenko.ticketbooking.service.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.epam.mishchenko.ticketbooking.dao.impl.UserDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    private UserDAOImpl userDAO;

    @Override
    public User getUserById(long userId) {
        LOGGER.log(Level.DEBUG, "Finding a user by id: {}", userId);

        try {
            User user = userDAO.getById(userId);

            LOGGER.log(Level.DEBUG, "The user with id {} successfully found ", userId);

            return user;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to get an user by id: " + userId);
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        LOGGER.log(Level.DEBUG, "Finding a user by email: {}", email);

        try {
            User user = userDAO.getByEmail(email);

            LOGGER.log(Level.DEBUG, "The user with email {} successfully found ", email);

            return user;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to get an user by email: " + email);
            return null;
        }
    }

    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG,
                "Finding all users by name {} with page size {} and number of page {}",
                name, pageSize, pageNum);

        try {
            List<User> usersByName = userDAO.getByName(name, pageSize, pageNum);

            LOGGER.log(Level.DEBUG,
                    "All users successfully found by name {} with page size {} and number of page {}",
                    name, pageSize, pageNum);

            return usersByName;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to find a list of users by name '{}'", name, e);
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        LOGGER.log(Level.DEBUG, "Start creating an user: {}", user);

        try {
            user = userDAO.insert(user);

            LOGGER.log(Level.DEBUG, "Successfully creation of the user: {}", user);

            return user;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to create an user: {}", user, e);
            return null;
        }
    }

    @Override
    public User updateUser(User user) {
        LOGGER.log(Level.DEBUG, "Start updating an user: {}", user);

        try {
            user = userDAO.update(user);

            LOGGER.log(Level.DEBUG, "Successfully updating of the user: {}", user);

            return user;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to update an user: {}", user, e);
            return null;
        }
    }

    @Override
    public boolean deleteUser(long userId) {
        LOGGER.log(Level.DEBUG, "Start deleting an user with id: {}", userId);

        try {
            boolean isRemoved = userDAO.delete(userId);

            LOGGER.log(Level.DEBUG, "Successfully deletion of the user with id: {}", userId);

            return isRemoved;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to delete an user with id: {}", userId, e);
            return false;
        }
    }

    public void setUserDAO(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }
}

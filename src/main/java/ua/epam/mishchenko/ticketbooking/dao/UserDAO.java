package ua.epam.mishchenko.ticketbooking.dao;

import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.List;

/**
 * The interface User dao.
 */
public interface UserDAO {

    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    User getById(long id);

    /**
     * Gets all.
     *
     * @return the all
     */
    List<User> getAll();

    /**
     * Insert user.
     *
     * @param user the user
     * @return the user
     */
    User insert(User user);

    /**
     * Update user.
     *
     * @param user the user
     * @return the user
     */
    User update(User user);

    /**
     * Delete boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    boolean delete(long userId);
}

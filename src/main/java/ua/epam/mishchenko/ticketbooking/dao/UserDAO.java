package ua.epam.mishchenko.ticketbooking.dao;

import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.List;

public interface UserDAO {

    User getById(long id);

    List<User> getAll();

    User insert(User user);

    User update(User user);

    boolean delete(long userId);
}

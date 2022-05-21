package ua.epam.mishchenko.ticketbooking.dao;

import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;

import java.util.List;

public interface EventDAO {

    Event getById(long id);

    List<Event> getAll();

    Event insert(Event event);

    Event update(Event event);

    boolean delete(long eventId);
}

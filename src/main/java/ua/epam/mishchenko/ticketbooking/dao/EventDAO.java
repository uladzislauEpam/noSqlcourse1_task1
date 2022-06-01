package ua.epam.mishchenko.ticketbooking.dao;

import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;

import java.util.List;

/**
 * The interface Event dao.
 */
public interface EventDAO {

    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    Event getById(long id);

    /**
     * Gets all.
     *
     * @return the all
     */
    List<Event> getAll();

    /**
     * Insert event.
     *
     * @param event the event
     * @return the event
     */
    Event insert(Event event);

    /**
     * Update event.
     *
     * @param event the event
     * @return the event
     */
    Event update(Event event);

    /**
     * Delete boolean.
     *
     * @param eventId the event id
     * @return the boolean
     */
    boolean delete(long eventId);
}

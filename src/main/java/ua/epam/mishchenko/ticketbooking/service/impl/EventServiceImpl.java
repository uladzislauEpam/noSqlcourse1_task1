package ua.epam.mishchenko.ticketbooking.service.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.epam.mishchenko.ticketbooking.dao.impl.EventDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.service.EventService;

import java.util.Date;
import java.util.List;

public class EventServiceImpl implements EventService {

    private static final Logger LOGGER = LogManager.getLogger(EventServiceImpl.class);

    private EventDAOImpl eventDAO;

    @Override
    public Event getEventById(long eventId) {
        LOGGER.log(Level.DEBUG, "Finding an event by id: {}", eventId);

        try {
            Event event = eventDAO.getById(eventId);

            LOGGER.log(Level.DEBUG, "Event with id {} successfully found ", eventId);

            return event;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to find an event by id: " + eventId);
            return null;
        }
    }

    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG,
                "Finding all events by title {} with page size {} and number of page {}",
                title, pageSize, pageNum);

        try {
            List<Event> eventsByTitle = eventDAO.getEventsByTitle(title, pageSize, pageNum);

            LOGGER.log(Level.DEBUG,
                    "All events successfully found by title {} with page size {} and number of page {}",
                    title, pageSize, pageNum);

            return eventsByTitle;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to find a list of events by title '{}'", title, e);
            return null;
        }
    }

    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG,
                "Finding all events for day {} with page size {} and number of page {}",
                day, pageSize, pageNum);

        try {
            List<Event> eventsByTitle = eventDAO.getEventsForDay(day, pageSize, pageNum);

            LOGGER.log(Level.DEBUG,
                    "All events successfully found for day {} with page size {} and number of page {}",
                    day, pageSize, pageNum);

            return eventsByTitle;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to find a list of events for day {}", day, e);
            return null;
        }
    }

    @Override
    public Event createEvent(Event event) {
        LOGGER.log(Level.DEBUG, "Start creating an event: {}", event);

        try {
            event = eventDAO.insert(event);

            LOGGER.log(Level.DEBUG, "Successfully creation of the event: {}", event);

            return event;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to create an event: {}", event, e);
            return null;
        }
    }

    @Override
    public Event updateEvent(Event event) {
        LOGGER.log(Level.DEBUG, "Start updating an event: {}", event);

        try {
            event = eventDAO.update(event);

            LOGGER.log(Level.DEBUG, "Successfully updating of the event: {}", event);

            return event;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to update an event: {}", event, e);
            return null;
        }
    }

    @Override
    public boolean deleteEvent(long eventId) {
        LOGGER.log(Level.DEBUG, "Start deleting an event with id: {}", eventId);

        try {
            boolean isRemoved = eventDAO.delete(eventId);

            LOGGER.log(Level.DEBUG, "Successfully deletion of the event with id: {}", eventId);

            return isRemoved;
        } catch (DbException e) {
            LOGGER.log(Level.WARN, "Can not to delete an event with id: {}", eventId, e);
            return false;
        }
    }

    public EventDAOImpl getEventDAO() {
        return eventDAO;
    }

    public void setEventDAO(EventDAOImpl eventDAO) {
        this.eventDAO = eventDAO;
    }
}

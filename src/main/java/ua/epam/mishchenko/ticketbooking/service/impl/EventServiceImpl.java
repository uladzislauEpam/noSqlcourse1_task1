package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.dao.impl.EventDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.service.EventService;

import java.util.Date;
import java.util.List;

public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private EventDAOImpl eventDAO;

    @Override
    public Event getEventById(long eventId) {
        log.info("Finding an event by id: {}", eventId);

        try {
            Event event = eventDAO.getById(eventId);

            log.info("Event with id {} successfully found ", eventId);

            return event;
        } catch (DbException e) {
            log.warn("Can not to find an event by id: " + eventId);
            return null;
        }
    }

    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        log.warn("Finding all events by title {} with page size {} and number of page {}",
                title, pageSize, pageNum);

        try {
            if (title.isEmpty()) {
                log.warn("The title can not be empty");
                return null;
            }

            List<Event> eventsByTitle = eventDAO.getEventsByTitle(title, pageSize, pageNum);

            log.info("All events successfully found by title {} with page size {} and number of page {}",
                    title, pageSize, pageNum);

            return eventsByTitle;
        } catch (DbException e) {
            log.warn("Can not to find a list of events by title '{}'", title, e);
            return null;
        }
    }

    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        log.info("Finding all events for day {} with page size {} and number of page {}",
                day, pageSize, pageNum);

        try {
            if (day == null) {
                log.warn("The day can not be null");
                return null;
            }

            List<Event> eventsByTitle = eventDAO.getEventsForDay(day, pageSize, pageNum);

            log.info("All events successfully found for day {} with page size {} and number of page {}",
                    day, pageSize, pageNum);

            return eventsByTitle;
        } catch (DbException e) {
            log.warn("Can not to find a list of events for day {}", day, e);
            return null;
        }
    }

    @Override
    public Event createEvent(Event event) {
        log.info("Start creating an event: {}", event);

        try {
            if (isEventNull(event)) {
                log.warn("The event can not be null");
                return null;
            }

            event = eventDAO.insert(event);

            log.info("Successfully creation of the event: {}", event);

            return event;
        } catch (DbException e) {
            log.warn("Can not to create an event: {}", event, e);
            return null;
        }
    }

    private boolean isEventNull(Event event) {
        return event == null;
    }

    @Override
    public Event updateEvent(Event event) {
        log.info("Start updating an event: {}", event);

        try {
            if (isEventNull(event)) {
                log.warn("The event can not be null");
                return null;
            }

            event = eventDAO.update(event);

            log.info("Successfully updating of the event: {}", event);

            return event;
        } catch (DbException e) {
            log.warn("Can not to update an event: {}", event, e);
            return null;
        }
    }

    @Override
    public boolean deleteEvent(long eventId) {
        log.info("Start deleting an event with id: {}", eventId);

        try {
            boolean isRemoved = eventDAO.delete(eventId);

            log.info("Successfully deletion of the event with id: {}", eventId);

            return isRemoved;
        } catch (DbException e) {
            log.warn("Can not to delete an event with id: {}", eventId, e);
            return false;
        }
    }

    public void setEventDAO(EventDAOImpl eventDAO) {
        this.eventDAO = eventDAO;
    }
}

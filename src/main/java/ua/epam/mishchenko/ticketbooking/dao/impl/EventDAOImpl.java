package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.epam.mishchenko.ticketbooking.dao.EventDAO;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

public class EventDAOImpl implements EventDAO {

    private static final String NAMESPACE = "event:";

    private static final Logger LOGGER = LogManager.getLogger(EventDAOImpl.class);

    private Storage storage;

    @Override
    public Event getById(long id) {
        LOGGER.log(Level.DEBUG, "Finding an event by id: {}", id);


        String stringEvent = storage.getInMemoryStorage().get(NAMESPACE + id);
        if (stringEvent == null) {
            LOGGER.log(Level.WARN, "Can not to find an event by id: {}", id);
            throw new DbException("Can not to find an event by id: " + id);
        }

        Event event = parseFromStringToEvent(stringEvent);

        LOGGER.log(Level.DEBUG, "Event with id {} successfully found ", id);
        return event;
    }

    private Event parseFromStringToEvent(String stringEvent) {
        final String delimiterBetweenFields = ",";
        String[] stringFields = stringEvent.split(delimiterBetweenFields);
        return createEventFromStringFields(stringFields);
    }

    private Event createEventFromStringFields(String[] stringFields) {
        int index = 0;
        Event event = new EventImpl();
        event.setId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        event.setTitle(getFieldValueFromFields(stringFields, index++));
        event.setDate(createDateFromString(getFieldValueFromFields(stringFields, index)));
        return event;
    }

    private Date createDateFromString(String fieldValueFromFields) {
        try {
            return DATE_FORMATTER.parse(fieldValueFromFields);
        } catch (ParseException e) {
            throw new RuntimeException("Can not to parse string to date", e);
        }
    }

    private String getFieldValueFromFields(String[] stringFields, int index) {
        final String delimiterBetweenKeyAndValue = " : ";
        return removeSingleQuotesIfExist(stringFields[index].split(delimiterBetweenKeyAndValue)[1]);
    }

    private String removeSingleQuotesIfExist(String text) {
        return text.replaceAll("'", "");
    }

    @Override
    public List<Event> getAll() {
        LOGGER.log(Level.DEBUG, "Finding all events in the database");

        List<Event> listOfAllEvents = getAllTicketsFromStorageByIds();
        if (listOfAllEvents.isEmpty()) {
            throw new DbException("List of events is empty");
        }

        LOGGER.log(Level.DEBUG, "All events successfully found");
        return listOfAllEvents;
    }

    private List<Event> getAllTicketsFromStorageByIds() {
        List<Event> listOfAllEvents = new ArrayList<>();
        List<String> idsOfEvents = getIdsOfEvents();
        for (String id : idsOfEvents) {
            listOfAllEvents.add(parseFromStringToEvent(storage.getInMemoryStorage().get(id)));
        }
        return listOfAllEvents;
    }

    private List<String> getIdsOfEvents() {
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isEventEntity)
                .collect(Collectors.toList());
    }

    private boolean isEventEntity(String entity) {
        return entity.contains(NAMESPACE);
    }

    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG, "Finding all events by title '{}' in the database using pagination", title);

        if (pageSize <= 0 || pageNum <= 0) {
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfEventsByTitle = getListOfStringEventsByTitle(title);
        if (stringListOfEventsByTitle.isEmpty()) {
            throw new DbException("List of all events by title '" + title + "' is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfEventsByTitle.size()) {
            throw new DbException("The size of events list (size=" + stringListOfEventsByTitle.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfEventsByTitleInRange = stringListOfEventsByTitle.subList(start, end);
        List<Event> listOfEventsByTitleInRange = parseFromStringListToEventList(stringListOfEventsByTitleInRange);

        LOGGER.log(Level.DEBUG,
                "All events successfully found by title '{}' in the database using pagination",
                title);

        return listOfEventsByTitleInRange;
    }

    private List<String> getListOfStringEventsByTitle(String title) {
        List<String> stringListOfEventsByTitle = new ArrayList<>();
        List<String> idsOfEvents = getIdsOfEvents();
        for (String id : idsOfEvents) {
            String stringEvent = storage.getInMemoryStorage().get(id);
            if (titleEquals(stringEvent, title)) {
                stringListOfEventsByTitle.add(stringEvent);
            }
        }
        return stringListOfEventsByTitle;
    }

    private boolean titleEquals(String entity, String title) {
        return entity.contains("'title' : '" + title + "'");
    }

    private int getStartIndex(int pageSize, int pageNum) {
        return pageSize * (pageNum - 1);
    }

    private int getEndIndex(int start, int pageSize) {
        return start + pageSize;
    }

    private List<Event> parseFromStringListToEventList(List<String> stringListOfEvents) {
        List<Event> events = new ArrayList<>();
        for (String stringEvent : stringListOfEvents) {
            events.add(parseFromStringToEvent(stringEvent));
        }
        return events;
    }

    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG, "Finding all events for day {} in the database using pagination", day);

        if (pageSize <= 0 || pageNum <= 0) {
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfEventsForDay = getListOfStringEventsForDay(day);
        if (stringListOfEventsForDay.isEmpty()) {
            throw new DbException("List of all events for day '" + day + "' is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfEventsForDay.size()) {
            throw new DbException("The size of events list (size=" + stringListOfEventsForDay.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfEventsForDayInRange = stringListOfEventsForDay.subList(start, end);
        List<Event> listOfEventsForDayInRange = parseFromStringListToEventList(stringListOfEventsForDayInRange);

        LOGGER.log(Level.DEBUG,
                "All events successfully found for day '{}' in the database using pagination",
                day);

        return listOfEventsForDayInRange;
    }

    private List<String> getListOfStringEventsForDay(Date day) {
        List<String> stringListOfEventsForDay = new ArrayList<>();
        List<String> idsOfEvents = getIdsOfEvents();
        for (String id : idsOfEvents) {
            String stringEvent = storage.getInMemoryStorage().get(id);
            if (dayEquals(stringEvent, day)) {
                stringListOfEventsForDay.add(stringEvent);
            }
        }
        return stringListOfEventsForDay;
    }

    private boolean dayEquals(String entity, Date day) {
        return entity.contains("'date' : '" + DATE_FORMATTER.format(day) + "'");
    }

    @Override
    public Event insert(Event event) {
        LOGGER.log(Level.DEBUG, "Start inserting of the event: {}", event);

        if (event == null) {
            throw new DbException("The event can not equal a null");
        }
        if (existsByTitleAndDay(event)) {
            throw new DbException("This email already exists");
        }
        setIdForEvent(event);
        storage.getInMemoryStorage().put(NAMESPACE + event.getId(), event.toString());

        LOGGER.log(Level.DEBUG, "Successfully insertion of the event: {}", event);

        return event;
    }

    private boolean existsByTitleAndDay(Event event) {
        List<String> idsOfEvents = getIdsOfEvents();
        for (String id : idsOfEvents) {
            String stringEvent = storage.getInMemoryStorage().get(id);
            if (titleEquals(stringEvent, event.getTitle()) && dayEquals(stringEvent, event.getDate())) {
                return true;
            }
        }
        return false;
    }

    private void setIdForEvent(Event event) {
        List<String> idsOfEvents = getIdsOfEvents();
        if (idsOfEvents.isEmpty()) {
            event.setId(1L);
            return;
        }
        Collections.sort(idsOfEvents);
        String stringLastEventId = idsOfEvents.get(idsOfEvents.size() - 1);
        long longLastEventId = Long.parseLong(stringLastEventId.split(":")[1]);
        long newId = longLastEventId + 1;
        event.setId(newId);
    }

    @Override
    public Event update(Event event) {
        LOGGER.log(Level.DEBUG, "Start updating of the event: {}", event);

        if (event == null) {
            throw new DbException("The event can not equal a null");
        }
        if (!isEventExists(event.getId())) {
            throw new DbException("The event with id " + event.getId() + " does not exist");
        }

        storage.getInMemoryStorage().replace(NAMESPACE + event.getId(), event.toString());

        LOGGER.log(Level.DEBUG, "Successfully update of the event: {}", event);

        return event;
    }

    private boolean isEventExists(long id) {
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    @Override
    public boolean delete(long eventId) {
        LOGGER.log(Level.DEBUG, "Start deleting of the event with id: {}", eventId);

        if (!isEventExists(eventId)) {
            throw new DbException("The event with id " + eventId + " does not exist");
        }

        String removedEvent = storage.getInMemoryStorage().remove(NAMESPACE + eventId);

        if (removedEvent == null) {
            throw new DbException("The event with id" + eventId + " not deleted");
        }

        LOGGER.log(Level.DEBUG, "Successfully deletion of the event with id: {}", eventId);

        return true;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}

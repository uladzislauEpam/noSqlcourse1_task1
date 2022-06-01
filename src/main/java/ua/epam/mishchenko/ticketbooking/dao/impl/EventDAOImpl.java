package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.dao.EventDAO;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

/**
 * The type Event dao.
 */
public class EventDAOImpl implements EventDAO {

    /**
     * The constant NAMESPACE.
     */
    private static final String NAMESPACE = "event:";

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(EventDAOImpl.class);

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
    public Event getById(long id) {
        log.info("Finding an event by id: {}", id);


        String stringEvent = storage.getInMemoryStorage().get(NAMESPACE + id);
        if (stringEvent == null) {
            log.warn("Can not to find an event by id: {}", id);
            throw new DbException("Can not to find an event by id: " + id);
        }

        Event event = parseFromStringToEvent(stringEvent);

        log.info("Event with id {} successfully found ", id);
        return event;
    }

    /**
     * Parse from string to event event.
     *
     * @param stringEvent the string event
     * @return the event
     */
    private Event parseFromStringToEvent(String stringEvent) {
        log.debug("Parsing from string event to event object: {}", stringEvent);
        final String delimiterBetweenFields = ",";
        stringEvent = removeBrackets(stringEvent);
        String[] stringFields = stringEvent.split(delimiterBetweenFields);
        return createEventFromStringFields(stringFields);
    }

    /**
     * Remove brackets string.
     *
     * @param text the text
     * @return the string
     */
    private String removeBrackets(String text) {
        log.debug("Removing brackets from string event: {}", text);
        text = text.replace("{", "");
        return text.replace("}", "");
    }

    /**
     * Create event from string fields event.
     *
     * @param stringFields the string fields
     * @return the event
     */
    private Event createEventFromStringFields(String[] stringFields) {
        log.debug("Creating event from string fields: {}", Arrays.toString(stringFields));
        int index = 0;
        Event event = new EventImpl();
        event.setId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        event.setTitle(getFieldValueFromFields(stringFields, index++));
        event.setDate(createDateFromString(getFieldValueFromFields(stringFields, index)));
        return event;
    }

    /**
     * Create date from string date.
     *
     * @param fieldValueFromFields the field value from fields
     * @return the date
     */
    private Date createDateFromString(String fieldValueFromFields) {
        log.debug("Trying to parsing date from string to date object");
        try {
            return DATE_FORMATTER.parse(fieldValueFromFields);
        } catch (ParseException e) {
            throw new RuntimeException("Can not to parse string to date", e);
        }
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
     * Gets all.
     *
     * @return the all
     */
    @Override
    public List<Event> getAll() {
        log.info("Finding all events in the database");

        List<Event> listOfAllEvents = getAllEventsFromStorageByIds();
        if (listOfAllEvents.isEmpty()) {
            log.warn("List of all events is empty");
            throw new DbException("List of events is empty");
        }

        log.info("All events successfully found");
        return listOfAllEvents;
    }

    /**
     * Gets all events from storage by ids.
     *
     * @return the all events from storage by ids
     */
    private List<Event> getAllEventsFromStorageByIds() {
        log.debug("Getting all events from storage by ids with \"event\" namespace");
        List<Event> listOfAllEvents = new ArrayList<>();
        List<String> idsOfEvents = getIdsOfEvents();
        for (String id : idsOfEvents) {
            listOfAllEvents.add(parseFromStringToEvent(storage.getInMemoryStorage().get(id)));
        }
        return listOfAllEvents;
    }

    /**
     * Gets ids of events.
     *
     * @return the ids of events
     */
    private List<String> getIdsOfEvents() {
        log.debug("Getting all event entities by \"event\" namespace");
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isEventEntity)
                .collect(Collectors.toList());
    }

    /**
     * Is event entity boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    private boolean isEventEntity(String entity) {
        log.debug("Checking if entity is a event entity: {}", entity);
        return entity.contains(NAMESPACE);
    }

    /**
     * Gets events by title.
     *
     * @param title    the title
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events by title
     */
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        log.info("Finding all events by title '{}' in the database using pagination", title);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfEventsByTitle = getListOfStringEventsByTitle(title);
        if (stringListOfEventsByTitle.isEmpty()) {
            log.warn("List of all events by title '{}' is empty", title);
            throw new DbException("List of all events by title '" + title + "' is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfEventsByTitle.size()) {
            log.warn("The size of events list (size={}) is less than end page (last page={})",
                    stringListOfEventsByTitle.size(), end);
            throw new DbException("The size of events list (size=" + stringListOfEventsByTitle.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfEventsByTitleInRange = stringListOfEventsByTitle.subList(start, end);
        List<Event> listOfEventsByTitleInRange = parseFromStringListToEventList(stringListOfEventsByTitleInRange);

        log.info("All events successfully found by title '{}' in the database using pagination", title);

        return listOfEventsByTitleInRange;
    }

    /**
     * Gets list of string events by title.
     *
     * @param title the title
     * @return the list of string events by title
     */
    private List<String> getListOfStringEventsByTitle(String title) {
        log.debug("Getting list of string events by title: {}", title);
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

    /**
     * Title equals boolean.
     *
     * @param entity the entity
     * @param title  the title
     * @return the boolean
     */
    private boolean titleEquals(String entity, String title) {
        log.debug("Checking if entity title {} equals to {}", entity, title);
        return entity.contains("'title' : '" + title + "'");
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
     * Parse from string list to event list list.
     *
     * @param stringListOfEvents the string list of events
     * @return the list
     */
    private List<Event> parseFromStringListToEventList(List<String> stringListOfEvents) {
        log.debug("Parsing from string list of events to object list of events: {}", stringListOfEvents);
        List<Event> events = new ArrayList<>();
        for (String stringEvent : stringListOfEvents) {
            events.add(parseFromStringToEvent(stringEvent));
        }
        return events;
    }

    /**
     * Gets events for day.
     *
     * @param day      the day
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events for day
     */
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        log.info("Finding all events for day {} in the database using pagination", day);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfEventsForDay = getListOfStringEventsForDay(day);
        if (stringListOfEventsForDay.isEmpty()) {
            log.warn("List of all events for day '{}' is empty", day);
            throw new DbException("List of all events for day '" + day + "' is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfEventsForDay.size()) {
            log.warn("The size of events list (size={}) is less than end page (last page={})",
                    stringListOfEventsForDay.size(), end);
            throw new DbException("The size of events list (size=" + stringListOfEventsForDay.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfEventsForDayInRange = stringListOfEventsForDay.subList(start, end);
        List<Event> listOfEventsForDayInRange = parseFromStringListToEventList(stringListOfEventsForDayInRange);

        log.info("All events successfully found for day '{}' in the database using pagination", day);

        return listOfEventsForDayInRange;
    }

    /**
     * Gets list of string events for day.
     *
     * @param day the day
     * @return the list of string events for day
     */
    private List<String> getListOfStringEventsForDay(Date day) {
        log.debug("Getting list of string events for day: {}", day);
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

    /**
     * Day equals boolean.
     *
     * @param entity the entity
     * @param day    the day
     * @return the boolean
     */
    private boolean dayEquals(String entity, Date day) {
        log.debug("Checking if entity day {} equals to {}", entity, day);
        return entity.contains("'date' : '" + DATE_FORMATTER.format(day) + "'");
    }

    /**
     * Insert event.
     *
     * @param event the event
     * @return the event
     */
    @Override
    public Event insert(Event event) {
        log.info("Start inserting of the event: {}", event);

        if (existsByTitleAndDay(event)) {
            log.warn("These title and day are already exists for one event");
            throw new DbException("These title and day are already exists for one event");
        }

        setIdForEvent(event);
        storage.getInMemoryStorage().put(NAMESPACE + event.getId(), event.toString());

        log.info("Successfully insertion of the event: {}", event);

        return event;
    }

    /**
     * Exists by title and day boolean.
     *
     * @param event the event
     * @return the boolean
     */
    private boolean existsByTitleAndDay(Event event) {
        log.debug("Checking if this event exists");
        List<String> idsOfEvents = getIdsOfEvents();
        for (String id : idsOfEvents) {
            String stringEvent = storage.getInMemoryStorage().get(id);
            if (titleEquals(stringEvent, event.getTitle()) && dayEquals(stringEvent, event.getDate())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets id for event.
     *
     * @param event the event
     */
    private void setIdForEvent(Event event) {
        log.debug("Setting id for event: {}", event);
        List<String> idsOfEvents = getIdsOfEvents();
        if (idsOfEvents.isEmpty()) {
            log.debug("This event is a first object. The new id is 1");
            event.setId(1L);
            return;
        }
        Collections.sort(idsOfEvents);
        String stringLastEventId = idsOfEvents.get(idsOfEvents.size() - 1);
        long longLastEventId = Long.parseLong(stringLastEventId.split(":")[1]);
        long newId = longLastEventId + 1;
        log.debug("The last id of event entity is {}. The new id is {}", stringLastEventId, newId);
        event.setId(newId);
    }

    /**
     * Update event.
     *
     * @param event the event
     * @return the event
     */
    @Override
    public Event update(Event event) {
        log.info("Start updating of the event: {}", event);

        if (!isEventExists(event.getId())) {
            log.warn("The event with id {} does not exist", event.getId());
            throw new DbException("The event with id " + event.getId() + " does not exist");
        }
        if (existsByTitleAndDay(event)) {
            log.warn("These title and day are already exists for one event");
            throw new DbException("These title and day are already exists for one event");
        }

        storage.getInMemoryStorage().replace(NAMESPACE + event.getId(), event.toString());

        log.info("Successfully update of the event: {}", event);

        return event;
    }

    /**
     * Is event exists boolean.
     *
     * @param id the id
     * @return the boolean
     */
    private boolean isEventExists(long id) {
        log.debug("Checking if id {} exists", id);
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    /**
     * Delete boolean.
     *
     * @param eventId the event id
     * @return the boolean
     */
    @Override
    public boolean delete(long eventId) {
        log.info("Start deleting of the event with id: {}", eventId);

        if (!isEventExists(eventId)) {
            log.warn("The event with id {} does not exist", eventId);
            throw new DbException("The event with id " + eventId + " does not exist");
        }

        String removedEvent = storage.getInMemoryStorage().remove(NAMESPACE + eventId);

        if (removedEvent == null) {
            log.warn("The event with id {} not deleted", eventId);
            throw new DbException("The event with id" + eventId + " not deleted");
        }

        log.info("Successfully deletion of the event with id: {}", eventId);

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

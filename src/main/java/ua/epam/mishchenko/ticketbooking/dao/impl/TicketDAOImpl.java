package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.dao.TicketDAO;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.TicketImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Ticket dao.
 */
public class TicketDAOImpl implements TicketDAO {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(TicketDAOImpl.class);

    /**
     * The constant NAMESPACE.
     */
    private static final String NAMESPACE = "ticket:";

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
    public Ticket getById(long id) {
        log.info("Finding a ticket by id: {}", id);

        String stringTicket = storage.getInMemoryStorage().get(NAMESPACE + id);
        if (stringTicket == null) {
            log.warn("Can not to find a ticket by id: {}", id);
            throw new DbException("Can not to find a ticket by id: " + id);
        }

        Ticket ticket = parseFromStringToTicket(stringTicket);

        log.info("The ticket with id {} successfully found ", id);
        return ticket;
    }

    /**
     * Parse from string to ticket ticket.
     *
     * @param stringTicket the string ticket
     * @return the ticket
     */
    private Ticket parseFromStringToTicket(String stringTicket) {
        log.debug("Parsing from string ticket to ticket object: {}", stringTicket);
        final String delimiterBetweenFields = ", ";
        stringTicket = removeBrackets(stringTicket);
        String[] stringFields = stringTicket.split(delimiterBetweenFields);
        return createTicketFromStringFields(stringFields);
    }

    /**
     * Remove brackets string.
     *
     * @param text the text
     * @return the string
     */
    private String removeBrackets(String text) {
        log.debug("Removing brackets from string ticket: {}", text);
        text = text.replace("{", "");
        return text.replace("}", "");
    }

    /**
     * Create ticket from string fields ticket.
     *
     * @param stringFields the string fields
     * @return the ticket
     */
    private Ticket createTicketFromStringFields(String[] stringFields) {
        log.debug("Creating ticket from string fields: {}", Arrays.toString(stringFields));
        int index = 0;
        Ticket ticket = new TicketImpl();
        ticket.setId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        ticket.setUserId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        ticket.setEventId(Long.parseLong(getFieldValueFromFields(stringFields, index++)));
        ticket.setPlace(Integer.parseInt(getFieldValueFromFields(stringFields, index++)));
        ticket.setCategory(Ticket.Category.valueOf(getFieldValueFromFields(stringFields, index)));
        return ticket;
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
    public List<Ticket> getAll() {
        log.info("Finding all tickets in the database");

        List<Ticket> listOfAllTickets = getAllTicketsFromStorageByIds();
        if (listOfAllTickets.isEmpty()) {
            log.warn("List of all tickets is empty");
            throw new DbException("List of tickets is empty");
        }

        log.info("All tickets successfully found");
        return listOfAllTickets;
    }

    /**
     * Gets all tickets from storage by ids.
     *
     * @return the all tickets from storage by ids
     */
    private List<Ticket> getAllTicketsFromStorageByIds() {
        log.debug("Getting all tickets from storage by ids with \"ticket\" namespace");
        List<Ticket> listOfAllTickets = new ArrayList<>();
        List<String> idsOfTickets = getIdsOfTickets();
        for (String id : idsOfTickets) {
            listOfAllTickets.add(parseFromStringToTicket(storage.getInMemoryStorage().get(id)));
        }
        return listOfAllTickets;
    }

    /**
     * Gets ids of tickets.
     *
     * @return the ids of tickets
     */
    private List<String> getIdsOfTickets() {
        log.debug("Getting all ticket entities by \"ticket\" namespace");
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isTicketEntity)
                .collect(Collectors.toList());
    }

    /**
     * Is ticket entity boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    private boolean isTicketEntity(String entity) {
        log.debug("Checking if entity is a ticket entity: {}", entity);
        return entity.contains(NAMESPACE);
    }

    /**
     * Gets all by user.
     *
     * @param user     the user
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the all by user
     */
    public List<Ticket> getAllByUser(User user, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by user '{}' in the database using pagination", user);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfTicketsByUser = getListOfStringTicketsByUser(user);
        if (stringListOfTicketsByUser.isEmpty()) {
            log.warn("List of all tickets by user '{}' is empty", user);
            throw new DbException("List of all booked tickets by user " + user + " is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfTicketsByUser.size()) {
            log.warn("The size of events list (size={}) is less than end page (last page={})",
                    stringListOfTicketsByUser.size(), end);
            throw new DbException("The size of users (size=" + stringListOfTicketsByUser.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfTicketsByUserInRange = stringListOfTicketsByUser.subList(start, end);
        List<Ticket> listOfTicketsByUserInRange = parseFromStringListToUserList(stringListOfTicketsByUserInRange);

        log.info("All booked tickets successfully found by user {} in the database using pagination", user);

        return listOfTicketsByUserInRange;
    }

    /**
     * Parse from string list to user list list.
     *
     * @param stringListOfUsers the string list of users
     * @return the list
     */
    private List<Ticket> parseFromStringListToUserList(List<String> stringListOfUsers) {
        log.debug("Parsing from string list of tickets to object list of tickets: {}", stringListOfUsers);
        List<Ticket> tickets = new ArrayList<>();
        for (String stringUser : stringListOfUsers) {
            tickets.add(parseFromStringToTicket(stringUser));
        }
        return tickets;
    }

    /**
     * Gets list of string tickets by user.
     *
     * @param user the user
     * @return the list of string tickets by user
     */
    private List<String> getListOfStringTicketsByUser(User user) {
        log.debug("Getting list of string tickets by user: {}", user);
        List<String> idsOfTickets = getIdsOfTickets();
        List<String> stringListOfUsersByName = new ArrayList<>();
        for (String id : idsOfTickets) {
            String stringTicket = storage.getInMemoryStorage().get(id);
            if (userIdEquals(stringTicket, user.getId())) {
                stringListOfUsersByName.add(stringTicket);
            }
        }
        return stringListOfUsersByName;
    }

    /**
     * User id equals boolean.
     *
     * @param entity the entity
     * @param id     the id
     * @return the boolean
     */
    private boolean userIdEquals(String entity, long id) {
        log.debug("Checking if user entity id {} equals to {}", entity, id);
        return entity.contains("'userId' : " + id);
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
     * Gets all by event.
     *
     * @param event    the event
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the all by event
     */
    public List<Ticket> getAllByEvent(Event event, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by event '{}' in the database using pagination", event);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }

        List<String> stringListOfTicketsByEvent = getListOfStringTicketsByEvent(event);
        if (stringListOfTicketsByEvent.isEmpty()) {
            log.warn("List of all tickets by event '{}' is empty", event);
            throw new DbException("List of all booked tickets by event " + event + " is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfTicketsByEvent.size()) {
            log.warn("The size of events list (size={}) is less than end page (last page={})",
                    stringListOfTicketsByEvent.size(), end);
            throw new DbException("The size of users (size=" + stringListOfTicketsByEvent.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfTicketsByEventInRange = stringListOfTicketsByEvent.subList(start, end);
        List<Ticket> listOfTicketsByEventInRange = parseFromStringListToUserList(stringListOfTicketsByEventInRange);

        log.info("All booked tickets successfully found by event {} in the database using pagination", event);

        return listOfTicketsByEventInRange;
    }

    /**
     * Gets list of string tickets by event.
     *
     * @param event the event
     * @return the list of string tickets by event
     */
    private List<String> getListOfStringTicketsByEvent(Event event) {
        log.debug("Getting list of string tickets by event: {}", event);
        List<String> idsOfTickets = getIdsOfTickets();
        List<String> stringListOfUsersByName = new ArrayList<>();
        for (String id : idsOfTickets) {
            String stringTicket = storage.getInMemoryStorage().get(id);
            if (eventIdEquals(stringTicket, event.getId())) {
                stringListOfUsersByName.add(stringTicket);
            }
        }
        return stringListOfUsersByName;
    }

    /**
     * Event id equals boolean.
     *
     * @param entity the entity
     * @param id     the id
     * @return the boolean
     */
    private boolean eventIdEquals(String entity, long id) {
        log.debug("Checking if event entity id {} equals to {}", entity, id);
        return entity.contains("'eventId' : " + id);
    }

    /**
     * Insert ticket.
     *
     * @param ticket the ticket
     * @return the ticket
     */
    @Override
    public Ticket insert(Ticket ticket) {
        log.info("Start inserting of the ticket: {}", ticket);

        if (isTicketBooked(ticket)) {
            log.warn("This ticket already booked");
            throw new DbException("This ticket already booked");
        }

        setIdForTicket(ticket);
        storage.getInMemoryStorage().put(NAMESPACE + ticket.getId(), ticket.toString());

        log.info("Successfully insertion of the ticket: {}", ticket);

        return ticket;
    }

    /**
     * Is ticket booked boolean.
     *
     * @param ticket the ticket
     * @return the boolean
     */
    private boolean isTicketBooked(Ticket ticket) {
        log.debug("Checking if this ticket is booked");
        List<String> idsOfTickets = getIdsOfTickets();
        for (String id : idsOfTickets) {
            String stringTicket = storage.getInMemoryStorage().get(id);
            if (existsByEventIdAndPlace(stringTicket, ticket.getEventId(), ticket.getPlace())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Exists by event id and place boolean.
     *
     * @param entity  the entity
     * @param eventId the event id
     * @param place   the place
     * @return the boolean
     */
    private boolean existsByEventIdAndPlace(String entity, long eventId, int place) {
        log.debug("Checking if ticket exists by event id {} and place {}", eventId, place);
        return entity.contains("'eventId' : " + eventId) && entity.contains("'place' : " + place);
    }

    /**
     * Sets id for ticket.
     *
     * @param ticket the ticket
     */
    private void setIdForTicket(Ticket ticket) {
        log.debug("Setting id for ticket: {}", ticket);
        List<String> idsOfTickets = getIdsOfTickets();
        if (idsOfTickets.isEmpty()) {
            log.debug("This ticket is a first object. The new id is 1");
            ticket.setId(1L);
            return;
        }
        Collections.sort(idsOfTickets);
        String stringLastTicketId = idsOfTickets.get(idsOfTickets.size() - 1);
        long longLastTicketId = Long.parseLong(stringLastTicketId.split(":")[1]);
        long newId = longLastTicketId + 1;
        log.debug("The last id of ticket entity is {}. The new id is {}", stringLastTicketId, newId);
        ticket.setId(newId);
    }

    /**
     * Update ticket.
     *
     * @param ticket the ticket
     * @return the ticket
     */
    @Override
    public Ticket update(Ticket ticket) {
        log.info("Start updating of the ticket: {}", ticket);

        if (ticket == null) {
            log.warn("The ticket can not be a null");
            throw new DbException("The ticket can not equal a null");
        }
        if (!isTicketExists(ticket.getId())) {
            log.warn("The ticket with id {} does not exist", ticket.getId());
            throw new DbException("The ticket with id " + ticket.getId() + " does not exist");
        }

        storage.getInMemoryStorage().replace(NAMESPACE + ticket.getId(), ticket.toString());

        log.info("Successfully update of the ticket: {}", ticket);

        return ticket;
    }

    /**
     * Is ticket exists boolean.
     *
     * @param id the id
     * @return the boolean
     */
    private boolean isTicketExists(long id) {
        log.debug("Checking if id {} exists", id);
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    /**
     * Delete boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    @Override
    public boolean delete(long ticketId) {
        log.info("Start deleting of the ticket with id: {}", ticketId);

        if (!isTicketExists(ticketId)) {
            log.warn("The ticket with id {} does not exist", ticketId);
            throw new DbException("The ticket with id " + ticketId + " does not exist");
        }

        String removedTicket = storage.getInMemoryStorage().remove(NAMESPACE + ticketId);

        if (removedTicket == null) {
            log.warn("The ticket with id {} not deleted", ticketId);
            throw new DbException("The ticket with id" + ticketId + " not deleted");
        }

        log.info("Successfully deletion of the ticket with id: {}", ticketId);

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

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

public class TicketDAOImpl implements TicketDAO {

    private static final Logger log = LoggerFactory.getLogger(TicketDAOImpl.class);

    private static final String NAMESPACE = "ticket:";

    private Storage storage;

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

    private Ticket parseFromStringToTicket(String stringTicket) {
        log.debug("Parsing from string ticket to ticket object: {}", stringTicket);
        final String delimiterBetweenFields = ", ";
        stringTicket = removeBrackets(stringTicket);
        String[] stringFields = stringTicket.split(delimiterBetweenFields);
        return createTicketFromStringFields(stringFields);
    }

    private String removeBrackets(String text) {
        log.debug("Removing brackets from string ticket: {}", text);
        text = text.replace("{", "");
        return text.replace("}", "");
    }

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

    private String getFieldValueFromFields(String[] stringFields, int index) {
        log.debug("Getting field value by index {} from the array: {}", index, Arrays.toString(stringFields));
        final String delimiterBetweenKeyAndValue = " : ";
        return removeSingleQuotesIfExist(stringFields[index].split(delimiterBetweenKeyAndValue)[1]);
    }

    private String removeSingleQuotesIfExist(String text) {
        log.debug("Removing single quotes from string values if exists: {}", text);
        return text.replaceAll("'", "");
    }

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

    private List<Ticket> getAllTicketsFromStorageByIds() {
        log.debug("Getting all tickets from storage by ids with \"ticket\" namespace");
        List<Ticket> listOfAllTickets = new ArrayList<>();
        List<String> idsOfTickets = getIdsOfTickets();
        for (String id : idsOfTickets) {
            listOfAllTickets.add(parseFromStringToTicket(storage.getInMemoryStorage().get(id)));
        }
        return listOfAllTickets;
    }

    private List<String> getIdsOfTickets() {
        log.debug("Getting all ticket entities by \"ticket\" namespace");
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isTicketEntity)
                .collect(Collectors.toList());
    }

    private boolean isTicketEntity(String entity) {
        log.debug("Checking if entity is a ticket entity: {}", entity);
        return entity.contains(NAMESPACE);
    }

    public List<Ticket> getAllByUser(User user, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by user '{}' in the database using pagination", user);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }
        if (user == null) {
            log.warn("The user can not be a null");
            throw new DbException("The user can not be a null");
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

    private List<Ticket> parseFromStringListToUserList(List<String> stringListOfUsers) {
        log.debug("Parsing from string list of tickets to object list of tickets: {}", stringListOfUsers);
        List<Ticket> tickets = new ArrayList<>();
        for (String stringUser : stringListOfUsers) {
            tickets.add(parseFromStringToTicket(stringUser));
        }
        return tickets;
    }

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

    private boolean userIdEquals(String entity, long id) {
        log.debug("Checking if user entity id {} equals to {}", entity, id);
        return entity.contains("'userId' : " + id);
    }

    private int getStartIndex(int pageSize, int pageNum) {
        log.debug("Getting start index by page size = {} and page num = {}", pageSize, pageNum);
        return pageSize * (pageNum - 1);
    }

    private int getEndIndex(int start, int pageSize) {
        log.debug("Getting end index by start index {} and page size {}", start, pageSize);
        return start + pageSize;
    }

    public List<Ticket> getAllByEvent(Event event, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by event '{}' in the database using pagination", event);

        if (pageSize <= 0 || pageNum <= 0) {
            log.warn("The page size and page num must be greater than 0");
            throw new DbException("The page size and page num must be greater than 0");
        }
        if (event == null) {
            log.warn("The event can not be a null");
            throw new DbException("The event can not be a null");
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

    private boolean eventIdEquals(String entity, long id) {
        log.debug("Checking if event entity id {} equals to {}", entity, id);
        return entity.contains("'eventId' : " + id);
    }

    @Override
    public Ticket insert(Ticket ticket) {
        log.info("Start inserting of the ticket: {}", ticket);

        if (ticket == null) {
            log.warn("The ticket can not be a null");
            throw new DbException("The ticket can not equal a null");
        }
        if (isTicketBooked(ticket)) {
            log.warn("This ticket already booked");
            throw new DbException("This ticket already booked");
        }

        setIdForTicket(ticket);
        storage.getInMemoryStorage().put(NAMESPACE + ticket.getId(), ticket.toString());

        log.info("Successfully insertion of the ticket: {}", ticket);

        return ticket;
    }

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

    private boolean existsByEventIdAndPlace(String entity, long eventId, int place) {
        log.debug("Checking if ticket exists by event id {} and place {}", eventId, place);
        return entity.contains("'eventId' : " + eventId) && entity.contains("'place' : " + place);
    }

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

    private boolean isTicketExists(long id) {
        log.debug("Checking if id {} exists", id);
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

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

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}

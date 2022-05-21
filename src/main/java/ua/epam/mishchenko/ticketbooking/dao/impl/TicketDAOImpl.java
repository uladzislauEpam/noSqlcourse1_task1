package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.epam.mishchenko.ticketbooking.dao.TicketDAO;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.TicketImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TicketDAOImpl implements TicketDAO {

    private static final Logger LOGGER = LogManager.getLogger(TicketDAOImpl.class);

    private static final String NAMESPACE = "ticket:";

    private Storage storage;

    @Override
    public Ticket getById(long id) {
        LOGGER.log(Level.DEBUG, "Finding a ticket by id: {}", id);

        String stringTicket = storage.getInMemoryStorage().get(NAMESPACE + id);
        if (stringTicket == null) {
            LOGGER.log(Level.WARN, "Can not to find a ticket by id: {}", id);
            throw new DbException("Can not to find a ticket by id: " + id);
        }

        Ticket ticket = parseFromStringToTicket(stringTicket);

        LOGGER.log(Level.DEBUG, "The ticket with id {} successfully found ", id);
        return ticket;
    }

    private Ticket parseFromStringToTicket(String stringTicket) {
        final String delimiterBetweenFields = ", ";
        stringTicket = removeBrackets(stringTicket);
        String[] stringFields = stringTicket.split(delimiterBetweenFields);
        return createTicketFromStringFields(stringFields);
    }

    private String removeBrackets(String text) {
        text = text.replace("{", "");
        return text.replace("}", "");
    }

    private Ticket createTicketFromStringFields(String[] stringFields) {
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
        final String delimiterBetweenKeyAndValue = " : ";
        return removeSingleQuotesIfExist(stringFields[index].split(delimiterBetweenKeyAndValue)[1]);
    }

    private String removeSingleQuotesIfExist(String text) {
        return text.replaceAll("'", "");
    }

    @Override
    public List<Ticket> getAll() {
        LOGGER.log(Level.DEBUG, "Finding all tickets in the database");

        List<Ticket> listOfAllTickets = new ArrayList<>();
        List<String> idsOfTickets = getIdsOfTickets();
        for (String id : idsOfTickets) {
            listOfAllTickets.add(parseFromStringToTicket(storage.getInMemoryStorage().get(id)));
        }
        if (listOfAllTickets.isEmpty()) {
            throw new DbException("List of tickets is empty");
        }

        LOGGER.log(Level.DEBUG, "All tickets successfully found");
        return listOfAllTickets;
    }

    private List<String> getIdsOfTickets() {
        return storage.getInMemoryStorage().keySet().stream()
                .filter(this::isTicketEntity)
                .collect(Collectors.toList());
    }

    private boolean isTicketEntity(String entity) {
        return entity.contains(NAMESPACE);
    }

    public List<Ticket> getAllByUser(User user, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG, "Finding all booked tickets by user '{}' in the database using pagination", user);

        if (pageSize <= 0 || pageNum <= 0) {
            throw new DbException("The page size and page num must be greater than 0");
        }
        if (user == null) {
            throw new DbException("The user can not be a null");
        }

        List<String> stringListOfTicketsByUser = getListOfStringTicketsByUser(user);
        if (stringListOfTicketsByUser.isEmpty()) {
            throw new DbException("List of all booked tickets by user " + user + " is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfTicketsByUser.size()) {
            throw new DbException("The size of users (size=" + stringListOfTicketsByUser.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfTicketsByUserInRange = stringListOfTicketsByUser.subList(start, end);
        List<Ticket> listOfTicketsByUserInRange = parseFromStringListToUserList(stringListOfTicketsByUserInRange);

        LOGGER.log(Level.DEBUG,
                "All booked tickets successfully found by user {} in the database using pagination",
                user);

        return listOfTicketsByUserInRange;
    }

    private List<Ticket> parseFromStringListToUserList(List<String> stringListOfUsers) {
        List<Ticket> tickets = new ArrayList<>();
        for (String stringUser : stringListOfUsers) {
            tickets.add(parseFromStringToTicket(stringUser));
        }
        return tickets;
    }

    private List<String> getListOfStringTicketsByUser(User user) {
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
        return entity.contains("'userId' : " + id);
    }

    private int getStartIndex(int pageSize, int pageNum) {
        return pageSize * (pageNum - 1);
    }

    private int getEndIndex(int start, int pageSize) {
        return start + pageSize;
    }

    public List<Ticket> getAllByEvent(Event event, int pageSize, int pageNum) {
        LOGGER.log(Level.DEBUG, "Finding all booked tickets by event '{}' in the database using pagination", event);

        if (pageSize <= 0 || pageNum <= 0) {
            throw new DbException("The page size and page num must be greater than 0");
        }
        if (event == null) {
            throw new DbException("The event can not be a null");
        }

        List<String> stringListOfTicketsByEvent = getListOfStringTicketsByEvent(event);
        if (stringListOfTicketsByEvent.isEmpty()) {
            throw new DbException("List of all booked tickets by event " + event + " is empty");
        }

        int start = getStartIndex(pageSize, pageNum);
        int end = getEndIndex(start, pageSize);
        if (end > stringListOfTicketsByEvent.size()) {
            throw new DbException("The size of users (size=" + stringListOfTicketsByEvent.size() + ") " +
                    "is less than end page (last page=" + end + ")");
        }
        List<String> stringListOfTicketsByEventInRange = stringListOfTicketsByEvent.subList(start, end);
        List<Ticket> listOfTicketsByEventInRange = parseFromStringListToUserList(stringListOfTicketsByEventInRange);

        LOGGER.log(Level.DEBUG,
                "All booked tickets successfully found by event {} in the database using pagination",
                event);

        return listOfTicketsByEventInRange;
    }

    private List<String> getListOfStringTicketsByEvent(Event event) {
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
        return entity.contains("'eventId' : " + id);
    }

    @Override
    public Ticket insert(Ticket ticket) {
        LOGGER.log(Level.DEBUG, "Start inserting of the ticket: {}", ticket);

        if (ticket == null) {
            throw new DbException("The ticket can not equal a null");
        }
        if (isTicketBooked(ticket)) {
            throw new DbException("This ticket already booked");
        }

        setIdForTicket(ticket);
        storage.getInMemoryStorage().put(NAMESPACE + ticket.getId(), ticket.toString());

        LOGGER.log(Level.DEBUG, "Successfully insertion of the ticket: {}", ticket);

        return ticket;
    }

    private boolean isTicketBooked(Ticket ticket) {
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
        return entity.contains("'eventId' : " + eventId) && entity.contains("'place' : " + place);
    }

    private void setIdForTicket(Ticket ticket) {
        List<String> idsOfTickets = getIdsOfTickets();
        if (idsOfTickets.isEmpty()) {
            ticket.setId(1L);
            return;
        }
        Collections.sort(idsOfTickets);
        String stringLastTicketId = idsOfTickets.get(idsOfTickets.size() - 1);
        long longLastTicketId = Long.parseLong(stringLastTicketId.split(":")[1]);
        long newId = longLastTicketId + 1;
        ticket.setId(newId);
    }

    @Override
    public Ticket update(Ticket ticket) {
        LOGGER.log(Level.DEBUG, "Start updating of the ticket: {}", ticket);

        if (ticket == null) {
            throw new DbException("The ticket can not equal a null");
        }
        if (!isTicketExists(ticket.getId())) {
            throw new DbException("The ticket with id " + ticket.getId() + " does not exist");
        }

        storage.getInMemoryStorage().replace(NAMESPACE + ticket.getId(), ticket.toString());

        LOGGER.log(Level.DEBUG, "Successfully update of the ticket: {}", ticket);

        return ticket;
    }

    private boolean isTicketExists(long id) {
        return storage.getInMemoryStorage().containsKey(NAMESPACE + id);
    }

    @Override
    public boolean delete(long ticketId) {
        LOGGER.log(Level.DEBUG, "Start deleting of the ticket with id: {}", ticketId);

        if (!isTicketExists(ticketId)) {
            throw new DbException("The ticket with id " + ticketId + " does not exist");
        }

        String removedTicket = storage.getInMemoryStorage().remove(NAMESPACE + ticketId);

        if (removedTicket == null) {
            throw new DbException("The ticket with id" + ticketId + " not deleted");
        }

        LOGGER.log(Level.DEBUG, "Successfully deletion of the ticket with id: {}", ticketId);

        return true;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }
}

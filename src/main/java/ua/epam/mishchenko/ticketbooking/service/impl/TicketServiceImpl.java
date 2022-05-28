package ua.epam.mishchenko.ticketbooking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.dao.impl.TicketDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.TicketImpl;
import ua.epam.mishchenko.ticketbooking.service.TicketService;

import java.util.ArrayList;
import java.util.List;

public class TicketServiceImpl implements TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private TicketDAOImpl ticketDAO;

    @Override
    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        log.info("Start booking a ticket for user with id {}, event with id event {}, place {}, category {}",
                userId, eventId, place, category);

        try {
            Ticket ticket = ticketDAO.insert(createNewTicket(userId, eventId, place, category));

            log.info("Successfully booking of the ticket: {}", ticket);

            return ticket;
        } catch (DbException e) {
            log.warn("Can not to book a ticket for user with id {}, event with id {}, place {}, category {}",
                    userId, eventId, place, category, e);
            return null;
        }
    }

    private Ticket createNewTicket(long userId, long eventId, int place, Ticket.Category category) {
        return new TicketImpl(userId, eventId, place, category);
    }

    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by user {} with page size {} and number of page {}",
                user, pageSize, pageNum);

        try {
            if (isUserNull(user)) {
                log.warn("The user can not be a null");
                return new ArrayList<>();
            }

            List<Ticket> ticketsByUser = ticketDAO.getAllByUser(user, pageSize, pageNum);

            log.info("All booked tickets successfully found by user {} with page size {} and number of page {}",
                    user, pageSize, pageNum);

            return ticketsByUser;
        } catch (DbException e) {
            log.warn("Can not to find a list of booked tickets by user '{}'", user, e);
            return new ArrayList<>();
        }
    }

    private boolean isUserNull(User user) {
        return user == null;
    }

    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        log.info("Finding all booked tickets by event {} with page size {} and number of page {}",
                event, pageSize, pageNum);

        try {
            if (isEventNull(event)) {
                log.warn("The event can not be a null");
                return new ArrayList<>();
            }

            List<Ticket> ticketsByUser = ticketDAO.getAllByEvent(event, pageSize, pageNum);

            log.info("All booked tickets successfully found by event {} with page size {} and number of page {}",
                    event, pageSize, pageNum);

            return ticketsByUser;
        } catch (DbException e) {
            log.warn("Can not to find a list of booked tickets by event '{}'", event, e);
            return new ArrayList<>();
        }
    }

    private boolean isEventNull(Event event) {
        return event == null;
    }

    @Override
    public boolean cancelTicket(long ticketId) {
        log.info("Start canceling a ticket with id: {}", ticketId);

        try {
            boolean isRemoved = ticketDAO.delete(ticketId);

            log.info("Successfully canceling of the ticket with id: {}", ticketId);

            return isRemoved;
        } catch (DbException e) {
            log.warn("Can not to cancel a ticket with id: {}", ticketId, e);
            return false;
        }
    }

    public void setTicketDAO(TicketDAOImpl ticketDAO) {
        this.ticketDAO = ticketDAO;
    }
}

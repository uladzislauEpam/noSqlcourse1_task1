package ua.epam.mishchenko.ticketbooking.facade.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import ua.epam.mishchenko.ticketbooking.facade.BookingFacade;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.oxm.model.TicketDTO;
import ua.epam.mishchenko.ticketbooking.oxm.model.TicketsDTO;
import ua.epam.mishchenko.ticketbooking.service.EventService;
import ua.epam.mishchenko.ticketbooking.service.TicketService;
import ua.epam.mishchenko.ticketbooking.service.UserService;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingFacadeImpl implements BookingFacade {

    private static final Logger log = LoggerFactory.getLogger(BookingFacadeImpl.class);

    private final EventService eventService;

    private final TicketService ticketService;

    private final UserService userService;

    private final Unmarshaller unmarshaller;

    public BookingFacadeImpl(EventService eventService, TicketService ticketService, UserService userService,
                             Unmarshaller unmarshaller) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public Event getEventById(long eventId) {
        return eventService.getEventById(eventId);
    }

    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        return eventService.getEventsByTitle(title, pageSize, pageNum);
    }

    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        return eventService.getEventsForDay(day, pageSize, pageNum);
    }

    @Override
    public Event createEvent(Event event) {
        return eventService.createEvent(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return eventService.updateEvent(event);
    }

    @Override
    public boolean deleteEvent(long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @Override
    public User getUserById(long userId) {
        return userService.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        return userService.getUsersByName(name, pageSize, pageNum);
    }

    @Override
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    @Override
    public boolean deleteUser(long userId) {
        return userService.deleteUser(userId);
    }

    @Override
    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        return ticketService.bookTicket(userId, eventId, place, category);
    }

    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(user, pageSize, pageNum);
    }

    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(event, pageSize, pageNum);
    }

    @Override
    public boolean cancelTicket(long ticketId) {
        return ticketService.cancelTicket(ticketId);
    }

    public void preloadTickets() {
        List<Ticket> bookedTickets = new ArrayList<>();
        try {
            readTicketsFromFileAndSaveToDB(bookedTickets);
        } catch (RuntimeException e) {
            log.warn("Can not to save tickets in the data base from tickets.xml", e);
            rollbackTickets(bookedTickets);
            throw new RuntimeException("Can not to save the tickets in the data base from tickets.xml", e);
        } catch (FileNotFoundException e) {
            log.warn("Can not to find a file", e);
            throw new RuntimeException("Can not to find a file", e);
        } catch (IOException e) {
            log.warn("Can not to read a tickets.xml file", e);
            throw new RuntimeException("Can not to read a tickets.xml file", e);
        }
    }

    private void readTicketsFromFileAndSaveToDB(List<Ticket> bookedTickets) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("data/tickets.xml");
        FileInputStream is = new FileInputStream(classPathResource.getFile());
        TicketsDTO ticketsDTO = (TicketsDTO) unmarshaller.unmarshal(new StreamSource(is));
        saveToDB(bookedTickets, ticketsDTO);
    }

    private void saveToDB(List<Ticket> bookedTickets, TicketsDTO ticketsDTO) {
        for (TicketDTO ticketDTO : ticketsDTO.getTickets()) {
            Ticket ticket = bookTicket(ticketDTO.getUserId(), ticketDTO.getEventId(), ticketDTO.getPlace(), ticketDTO.getCategory());
            if (ticket == null) {
                throw new RuntimeException("Can not to save the ticket: " + ticketDTO);
            }
            bookedTickets.add(ticket);
        }
    }

    private void rollbackTickets(List<Ticket> bookedTickets) {
        log.warn("Rollback tickets from the data base");
        for (Ticket bookedTicket : bookedTickets) {
            cancelTicket(bookedTicket.getId());
        }
    }
}

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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Booking facade.
 */
public class BookingFacadeImpl implements BookingFacade {

    /**
     * The constant log.
     */
    private static final Logger log = LoggerFactory.getLogger(BookingFacadeImpl.class);

    /**
     * The Event service.
     */
    private final EventService eventService;

    /**
     * The Ticket service.
     */
    private final TicketService ticketService;

    /**
     * The User service.
     */
    private final UserService userService;

    /**
     * The Unmarshaller.
     */
    private final Unmarshaller unmarshaller;

    /**
     * Instantiates a new Booking facade.
     *
     * @param eventService  the event service
     * @param ticketService the ticket service
     * @param userService   the user service
     * @param unmarshaller  the unmarshaller
     */
    public BookingFacadeImpl(EventService eventService, TicketService ticketService, UserService userService,
                             Unmarshaller unmarshaller) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.unmarshaller = unmarshaller;
    }

    /**
     * Gets event by id.
     *
     * @param eventId the event id
     * @return the event by id
     */
    @Override
    public Event getEventById(long eventId) {
        return eventService.getEventById(eventId);
    }

    /**
     * Gets events by title.
     *
     * @param title    the title
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events by title
     */
    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        return eventService.getEventsByTitle(title, pageSize, pageNum);
    }

    /**
     * Gets events for day.
     *
     * @param day      the day
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the events for day
     */
    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        return eventService.getEventsForDay(day, pageSize, pageNum);
    }

    /**
     * Create event event.
     *
     * @param event the event
     * @return the event
     */
    @Override
    public Event createEvent(Event event) {
        return eventService.createEvent(event);
    }

    /**
     * Update event event.
     *
     * @param event the event
     * @return the event
     */
    @Override
    public Event updateEvent(Event event) {
        return eventService.updateEvent(event);
    }

    /**
     * Delete event boolean.
     *
     * @param eventId the event id
     * @return the boolean
     */
    @Override
    public boolean deleteEvent(long eventId) {
        return eventService.deleteEvent(eventId);
    }

    /**
     * Gets user by id.
     *
     * @param userId the user id
     * @return the user by id
     */
    @Override
    public User getUserById(long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Gets user by email.
     *
     * @param email the email
     * @return the user by email
     */
    @Override
    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Gets users by name.
     *
     * @param name     the name
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the users by name
     */
    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        return userService.getUsersByName(name, pageSize, pageNum);
    }

    /**
     * Create user user.
     *
     * @param user the user
     * @return the user
     */
    @Override
    public User createUser(User user) {
        return userService.createUser(user);
    }

    /**
     * Update user user.
     *
     * @param user the user
     * @return the user
     */
    @Override
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    /**
     * Delete user boolean.
     *
     * @param userId the user id
     * @return the boolean
     */
    @Override
    public boolean deleteUser(long userId) {
        return userService.deleteUser(userId);
    }

    /**
     * Book ticket ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     * @return the ticket
     */
    @Override
    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        return ticketService.bookTicket(userId, eventId, place, category);
    }

    /**
     * Gets booked tickets.
     *
     * @param user     the user
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(user, pageSize, pageNum);
    }

    /**
     * Gets booked tickets.
     *
     * @param event    the event
     * @param pageSize the page size
     * @param pageNum  the page num
     * @return the booked tickets
     */
    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        return ticketService.getBookedTickets(event, pageSize, pageNum);
    }

    /**
     * Cancel ticket boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    @Override
    public boolean cancelTicket(long ticketId) {
        return ticketService.cancelTicket(ticketId);
    }

    /**
     * Preload tickets.
     */
    public void preloadTickets(InputStream xmlFile) {
        List<Ticket> bookedTickets = new ArrayList<>();
        try {
            readTicketsFromFileAndSaveToDB(bookedTickets, xmlFile);
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

    /**
     * Read tickets from file and save to db.
     *
     * @param bookedTickets the booked tickets
     * @param xmlFile the xml file with tickets
     * @throws IOException the io exception
     */
    private void readTicketsFromFileAndSaveToDB(List<Ticket> bookedTickets, InputStream xmlFile) throws IOException {
        TicketsDTO ticketsDTO = (TicketsDTO) unmarshaller.unmarshal(new StreamSource(xmlFile));
        saveToDB(bookedTickets, ticketsDTO);
    }

    /**
     * Save to db.
     *
     * @param bookedTickets the booked tickets
     * @param ticketsDTO    the tickets dto
     */
    private void saveToDB(List<Ticket> bookedTickets, TicketsDTO ticketsDTO) {
        for (TicketDTO ticketDTO : ticketsDTO.getTickets()) {
            Ticket ticket = bookTicket(ticketDTO.getUserId(), ticketDTO.getEventId(), ticketDTO.getPlace(), ticketDTO.getCategory());
            if (ticket == null) {
                throw new RuntimeException("Can not to save the ticket: " + ticketDTO);
            }
            bookedTickets.add(ticket);
        }
    }

    /**
     * Rollback tickets.
     *
     * @param bookedTickets the booked tickets
     */
    private void rollbackTickets(List<Ticket> bookedTickets) {
        log.warn("Rollback tickets from the data base");
        for (Ticket bookedTicket : bookedTickets) {
            cancelTicket(bookedTicket.getId());
        }
    }
}

package ua.epam.mishchenko.ticketbooking.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tickets")
public class TicketsController {

    private static final Logger log = LoggerFactory.getLogger(TicketsController.class);

    private final BookingFacadeImpl bookingFacade;

    public TicketsController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @PostMapping
    public ModelAndView bookTicket(@RequestParam long userId,
                                   @RequestParam long eventId,
                                   @RequestParam int place,
                                   @RequestParam Ticket.Category category) {
        log.info("Booking a ticket: userId={}, eventId={}, place={}, category={}", userId, eventId, place, category);
        Map<String, Object> model = new HashMap<>();
        Ticket ticket = bookingFacade.bookTicket(userId, eventId, place, category);
        if (isNull(ticket)) {
            model.put("message", "Can not to book a ticket");
            log.info("Can not to book a ticket: userId={}, eventId={}, place={}, category={}",
                    userId, eventId, place, category);
        } else {
            model.put("ticket", ticket);
            log.info("The ticket successfully booked");
        }
        return new ModelAndView("ticket", model);
    }

    private boolean isNull(Object object) {
        return object == null;
    }

    @GetMapping("/user/{userId}")
    public ModelAndView showTicketsByUser(@PathVariable long userId,
                                          @RequestParam int pageSize,
                                          @RequestParam int pageNum) {
        log.info("Showing the tickets by user with id: {}", userId);
        Map<String, Object> model = new HashMap<>();
        User userById = bookingFacade.getUserById(userId);
        if (isNull(userById)) {
            model.put("message", "Can not to find a user by id: " + userId);
            log.info("Can not to find a user by id: {}", userId);
        } else {
            List<Ticket> bookedTickets = bookingFacade.getBookedTickets(userById, pageSize, pageNum);
            if (bookedTickets.isEmpty()) {
                model.put("message", "Can not to find the tickets by user with id: " + userId);
                log.info("Can not to find the tickets by user with id: {}", userId);
            } else {
                model.put("tickets", bookedTickets);
                log.info("The tickets successfully found");
            }
        }
        return new ModelAndView("tickets", model);
    }

    @GetMapping("/event/{eventId}")
    public ModelAndView showTicketsByEvent(@PathVariable long eventId,
                                           @RequestParam int pageSize,
                                           @RequestParam int pageNum) {
        log.info("Showing the tickets by event with id: {}", eventId);
        Map<String, Object> model = new HashMap<>();
        Event eventById = bookingFacade.getEventById(eventId);
        if (isNull(eventById)) {
            model.put("message", "Can not to find an event by id: " + eventId);
            log.info("Can not to find an event by id: {}", eventId);
        } else {
            List<Ticket> bookedTickets = bookingFacade.getBookedTickets(eventById, pageSize, pageNum);
            if (bookedTickets.isEmpty()) {
                model.put("message", "Can not to find the tickets by event with id: " + eventId);
                log.info("Can not to find the tickets by event with id: {}", eventId);
            } else {
                model.put("tickets", bookedTickets);
                log.info("The tickets successfully found");
            }
        }
        return new ModelAndView("tickets", model);
    }

    @DeleteMapping("/{id}")
    public ModelAndView cancelTicket(@PathVariable long id) {
        log.info("Canceling ticket with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        boolean isTicketCanceled = bookingFacade.cancelTicket(id);
        if (isTicketCanceled) {
            model.put("message", "The ticket with id: " + id + " successfully canceled");
            log.info("The ticket with id: {} successfully canceled", id);
        } else {
            model.put("message", "The ticket with id: " + id + " not canceled");
            log.info("The ticket with id: {} not canceled", id);
        }
        return new ModelAndView("ticket", model);
    }
}

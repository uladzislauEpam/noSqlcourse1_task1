package ua.epam.mishchenko.ticketbooking.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;

import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketsController {

    private static final Logger log = LoggerFactory.getLogger(TicketsController.class);

    private final BookingFacadeImpl bookingFacade;

    public TicketsController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @PostMapping
    public String bookTicket(@RequestParam long userId,
                             @RequestParam long eventId,
                             @RequestParam int place,
                             @RequestParam Ticket.Category category,
                             Model model) {
        log.info("Booking a ticket: userId={}, eventId={}, place={}, category={}", userId, eventId, place, category);
        Ticket ticket = bookingFacade.bookTicket(userId, eventId, place, category);
        if (isNull(ticket)) {
            model.addAttribute("message", "Can not to book a ticket");
            log.info("Can not to book a ticket: userId={}, eventId={}, place={}, category={}",
                    userId, eventId, place, category);
        } else {
            model.addAttribute("ticket", ticket);
            log.info("The ticket successfully booked");
        }
        return "ticket";
    }

    private boolean isNull(Object object) {
        return object == null;
    }

    @GetMapping("/event/{eventId}")
    public String showTicketsByEvent(@PathVariable long eventId,
                                     @RequestParam int pageSize,
                                     @RequestParam int pageNum,
                                     Model model) {
        log.info("Showing the tickets by event with id: {}", eventId);
        Event eventById = bookingFacade.getEventById(eventId);
        if (isNull(eventById)) {
            model.addAttribute("message", "Can not to find an event by id: " + eventId);
            log.info("Can not to find an event by id: {}", eventId);
        } else {
            List<Ticket> bookedTickets = bookingFacade.getBookedTickets(eventById, pageSize, pageNum);
            if (bookedTickets.isEmpty()) {
                model.addAttribute("message", "Can not to find the tickets by event with id: " + eventId);
                log.info("Can not to find the tickets by event with id: {}", eventId);
            } else {
                model.addAttribute("tickets", bookedTickets);
                log.info("The tickets successfully found");
            }
        }
        return "tickets";
    }

    @DeleteMapping("/{id}")
    public String cancelTicket(@PathVariable long id, Model model) {
        log.info("Canceling ticket with id: {}", id);
        boolean isTicketCanceled = bookingFacade.cancelTicket(id);
        if (isTicketCanceled) {
            model.addAttribute("message", "The ticket with id: " + id + " successfully canceled");
            log.info("The ticket with id: {} successfully canceled", id);
        } else {
            model.addAttribute("message", "The ticket with id: " + id + " not canceled");
            log.info("The ticket with id: {} not canceled", id);
        }
        return "ticket";
    }
}

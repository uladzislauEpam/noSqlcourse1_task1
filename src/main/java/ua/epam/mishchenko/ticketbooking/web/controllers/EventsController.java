package ua.epam.mishchenko.ticketbooking.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

@Controller
@RequestMapping("/events")
public class EventsController {

    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    private final BookingFacadeImpl bookingFacade;

    public EventsController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/{id}")
    public String showEventById(@PathVariable long id, Model model) {
        log.info("Showing event by id: {}", id);
        Event eventById = bookingFacade.getEventById(id);
        if (isNull(eventById)) {
            model.addAttribute("message", "Can not to get an event by id: " + id);
            log.info("Can not to get event by id: {}", id);
        } else {
            model.addAttribute("event", eventById);
            log.info("Event by id: {} successfully found", id);
        }
        return "event";
    }

    private boolean isNull(Event event) {
        return event == null;
    }

    @GetMapping("/title/{title}")
    public String showEventsByTitle(@PathVariable String title,
                                    @RequestParam int pageSize,
                                    @RequestParam int pageNum,
                                    Model model) {
        log.info("Showing events by title: {}", title);
        List<Event> eventsByTitle = bookingFacade.getEventsByTitle(title, pageSize, pageNum);
        if (eventsByTitle.isEmpty()) {
            model.addAttribute("message", "Can not to get events by title: " + title);
            log.info("Can not to get events by title: {}", title);
        } else {
            model.addAttribute("events", eventsByTitle);
            log.info("Events by title '{}' successfully found", title);
        }
        return "events";
    }

    @GetMapping("/day/{day}")
    public String showEventsForDay(@PathVariable String day,
                                   @RequestParam int pageSize,
                                   @RequestParam int pageNum,
                                   Model model) {
        log.info("Showing events for day: {}", day);
        try {
            Date date = parseFromStringToDate(day);
            List<Event> eventsForDay = bookingFacade.getEventsForDay(date, pageSize, pageNum);
            if (eventsForDay.isEmpty()) {
                model.addAttribute("message", "Can not to get events for day: " + day);
                log.info("Can not to get events for day: {}", day);
            } else {
                model.addAttribute("events", eventsForDay);
                log.info("Events for day: {} successfully found", day);
            }
        } catch (RuntimeException e) {
            log.warn("Can not to get events for day={}", day, e);
            model.addAttribute("message", "Can not to parse string " + day + " to date object");
        }
        return "events";
    }

    @PostMapping
    public String createEvent(@RequestParam String title,
                              @RequestParam String day,
                              Model model) {
        log.info("Creating an event with title={} and day={}", title, day);
        try {
            Event event = bookingFacade.createEvent(createEventEntityWithoutId(title, day));
            if (isNull(event)) {
                model.addAttribute("Can not to create an event");
                log.info("Can not to create an event");
            } else {
                model.addAttribute("event", event);
                log.info("The event successfully created");
            }
        } catch (RuntimeException e) {
            log.error("Can not to create entity with title={}, day={}", title, day, e);
            model.addAttribute("message", "Can not to parse string " + day + " to date object");
        }
        return "event";
    }

    private Event createEventEntityWithoutId(String title, String day) {
        Event event = new EventImpl();
        event.setTitle(title);
        event.setDate(parseFromStringToDate(day));
        return event;
    }

    private Date parseFromStringToDate(String date) {
        try {
            return DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            log.warn("Can not to parse string {} to date object", date);
            throw new RuntimeException("Can not to parse string " + date + " to date object", e);
        }
    }

    @PutMapping
    public String updateEvent(@RequestParam long id,
                              @RequestParam String title,
                              @RequestParam String day,
                              Model model) {
        log.info("Updating an event with id: {}", id);
        try {
            Event event = bookingFacade.updateEvent(createEventEntityWithId(id, title, day));
            if (isNull(event)) {
                model.addAttribute("Can not to update an event with id: " + id);
                log.info("Can not to update an event with id: {}", id);
            } else {
                model.addAttribute("event", event);
                log.info("The event with id: {} successfully updated", id);
            }
        } catch (RuntimeException e) {
            log.error("Can not to update entity with id={}, title={}, day={}", id, title, day, e);
            model.addAttribute("message", "Can not to parse string " + day + " to date object");
        }
        return "event";
    }

    private Event createEventEntityWithId(long id, String title, String day) {
        Event eventEntity = createEventEntityWithoutId(title, day);
        eventEntity.setId(id);
        return eventEntity;
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable long id, Model model) {
        log.info("Deleting an event with id: {}", id);
        boolean isEventDeleted = bookingFacade.deleteEvent(id);
        if (isEventDeleted) {
            model.addAttribute("message", "The event with id " + id + " successfully deleted");
            log.info("The event with id: {} successfully deleted", id);
        } else {
            model.addAttribute("message", "The event with id " + id + " not deleted");
            log.info("The event with id: {} not deleted", id);
        }
        return "event";
    }
}

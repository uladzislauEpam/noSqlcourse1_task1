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
    public String getEventById(@PathVariable long id, Model model) {
        Event eventById = bookingFacade.getEventById(id);
        if (eventById == null) {
            model.addAttribute("message", "Can not to get an event by id");
        } else {
            model.addAttribute("event", eventById);
        }
        return "event";
    }

    @GetMapping("/title/{title}")
    public String getEventsByTitle(@PathVariable String title,
                                   @RequestParam int pageSize,
                                   @RequestParam int pageNum,
                                   Model model) {
        List<Event> eventsByTitle = bookingFacade.getEventsByTitle(title, pageSize, pageNum);
        if (eventsByTitle == null) {
            model.addAttribute("message", "Can not to get events by title");
        } else {
            model.addAttribute("events", eventsByTitle);
        }
        return "events";
    }

    @GetMapping("/day/{day}")
    public String getEventsForDay(@PathVariable String day,
                                  @RequestParam int pageSize,
                                  @RequestParam int pageNum,
                                  Model model) {
        try {
            Date date = parseFromStringToDate(day);
            List<Event> eventsForDay = bookingFacade.getEventsForDay(date, pageSize, pageNum);
            if (eventsForDay == null) {
                model.addAttribute("message", "Can not to get events for day");
            } else {
                model.addAttribute("events", eventsForDay);
            }
        } catch (RuntimeException e) {
            log.warn("Can not to get events for day={}", day, e);
            model.addAttribute("message", "Can not to parse string " + day + " to date object");
        }
        return "events";
    }

    @PostMapping
    public String createEvent(@RequestParam String title, @RequestParam String day, Model model) {
        try {
            Event event = bookingFacade.createEvent(createEventEntityWithoutId(title, day));
            if (event == null) {
                model.addAttribute("Can not to create an event");
            }
            model.addAttribute("event", event);
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
        try {
            Event event = bookingFacade.updateEvent(createEventEntityWithId(id, title, day));
            if (event == null) {
                model.addAttribute("Can not to update an event with id: " + id);
            }
            model.addAttribute("event", event);
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
        boolean isEventDeleted = bookingFacade.deleteEvent(id);
        if (isEventDeleted) {
            model.addAttribute("message", "The event with id " + id + " successfully deleted");
        } else {
            model.addAttribute("message", "The event with id " + id + " not deleted");
        }
        return "event";
    }
}

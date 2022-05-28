package ua.epam.mishchenko.ticketbooking.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ModelAndView showEventById(@PathVariable long id) {
        log.info("Showing event by id: {}", id);
        Event eventById = bookingFacade.getEventById(id);
        Map<String, Object> model = new HashMap<>();
        if (isNull(eventById)) {
            model.put("message", "Can not to get an event by id: " + id);
            log.info("Can not to get event by id: {}", id);
        } else {
            model.put("event", eventById);
            log.info("Event by id: {} successfully found", id);
        }
        return new ModelAndView("event", model);
    }

    private boolean isNull(Event event) {
        return event == null;
    }

    @GetMapping("/title/{title}")
    public ModelAndView showEventsByTitle(@PathVariable String title,
                                          @RequestParam int pageSize,
                                          @RequestParam int pageNum) {
        log.info("Showing events by title: {}", title);
        Map<String, Object> model = new HashMap<>();
        List<Event> eventsByTitle = bookingFacade.getEventsByTitle(title, pageSize, pageNum);
        if (eventsByTitle.isEmpty()) {
            model.put("message", "Can not to get events by title: " + title);
            log.info("Can not to get events by title: {}", title);
        } else {
            model.put("events", eventsByTitle);
            log.info("Events by title '{}' successfully found", title);
        }
        return new ModelAndView("events", model);
    }

    @GetMapping("/day/{day}")
    public ModelAndView showEventsForDay(@PathVariable String day,
                                         @RequestParam int pageSize,
                                         @RequestParam int pageNum) {
        log.info("Showing events for day: {}", day);
        Map<String, Object> model = new HashMap<>();
        try {
            Date date = parseFromStringToDate(day);
            List<Event> eventsForDay = bookingFacade.getEventsForDay(date, pageSize, pageNum);
            if (eventsForDay.isEmpty()) {
                model.put("message", "Can not to get events for day: " + day);
                log.info("Can not to get events for day: {}", day);
            } else {
                model.put("events", eventsForDay);
                log.info("Events for day: {} successfully found", day);
            }
        } catch (RuntimeException e) {
            log.warn("Can not to get events for day={}", day, e);
            model.put("message", "Can not to parse string " + day + " to date object");
        }
        return new ModelAndView("events", model);
    }

    @PostMapping
    public ModelAndView createEvent(@RequestParam String title,
                                    @RequestParam String day) {
        log.info("Creating an event with title={} and day={}", title, day);
        Map<String, Object> model = new HashMap<>();
        try {
            Event event = bookingFacade.createEvent(createEventEntityWithoutId(title, day));
            if (isNull(event)) {
                model.put("message", "Can not to create an event");
                log.info("Can not to create an event");
            } else {
                model.put("event", event);
                log.info("The event successfully created");
            }
        } catch (RuntimeException e) {
            log.error("Can not to parse string {} to date object", day, e);
            model.put("message", "Can not to parse string " + day + " to date object");
        }
        return new ModelAndView("event", model);
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
    public ModelAndView updateEvent(@RequestParam long id,
                                    @RequestParam String title,
                                    @RequestParam String day) {
        log.info("Updating an event with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        try {
            Event event = bookingFacade.updateEvent(createEventEntityWithId(id, title, day));
            if (isNull(event)) {
                model.put("message", "Can not to update an event with id: " + id);
                log.info("Can not to update an event with id: {}", id);
            } else {
                model.put("event", event);
                log.info("The event with id: {} successfully updated", id);
            }
        } catch (RuntimeException e) {
            log.error("Can not to update entity with id={}, title={}, day={}", id, title, day, e);
            model.put("message", "Can not to parse string " + day + " to date object");
        }
        return new ModelAndView("event", model);
    }

    private Event createEventEntityWithId(long id, String title, String day) {
        Event eventEntity = createEventEntityWithoutId(title, day);
        eventEntity.setId(id);
        return eventEntity;
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteEvent(@PathVariable long id) {
        log.info("Deleting an event with id: {}", id);
        Map<String, Object> model = new HashMap<>();
        boolean isEventDeleted = bookingFacade.deleteEvent(id);
        if (isEventDeleted) {
            model.put("message", "The event with id " + id + " successfully deleted");
            log.info("The event with id: {} successfully deleted", id);
        } else {
            model.put("message", "The event with id " + id + " not deleted");
            log.info("The event with id: {} not deleted", id);
        }
        return new ModelAndView("event", model);
    }
}

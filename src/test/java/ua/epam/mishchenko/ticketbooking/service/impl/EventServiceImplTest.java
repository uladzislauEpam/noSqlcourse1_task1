package ua.epam.mishchenko.ticketbooking.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.epam.mishchenko.ticketbooking.dao.impl.EventDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    private EventServiceImpl eventService;

    @Mock
    private EventDAOImpl eventDAO;

    @Before
    public void setUp() {
        eventService = new EventServiceImpl();
        eventService.setEventDAO(eventDAO);
    }

    @Test
    public void getEventByIdWithExistsIdShouldBeOk() throws ParseException {
        long userId = 3L;
        Event expectedEvent = new EventImpl(userId, "Third event", DATE_FORMATTER.parse("16-05-2022 12:00"));

        when(eventDAO.getById(userId)).thenReturn(expectedEvent);

        Event actualEvent = eventService.getEventById(userId);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void getEventByIdWithExceptionShouldReturnNull() {
        when(eventDAO.getById(anyLong())).thenThrow(DbException.class);

        Event actualEvent = eventService.getEventById(10L);

        assertNull(actualEvent);
    }

    @Test
    public void getEventsByTitleWithExistsTitleShouldBeOk() throws ParseException {
        String title = "Third event";
        List<Event> expectedEvents = Arrays.asList(
                new EventImpl(3L, title, DATE_FORMATTER.parse("16-05-2022 12:00")),
                new EventImpl(5L, title, DATE_FORMATTER.parse("25-05-2022 9:10"))
        );

        when(eventDAO.getEventsByTitle(eq(title), anyInt(), anyInt())).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.getEventsByTitle(title, 2, 1);

        assertEquals(expectedEvents, actualEvents);
    }

    @Test
    public void getEventsByTitleWithExceptionShouldReturnNull() {
        when(eventDAO.getEventsByTitle(anyString(), anyInt(), anyInt())).thenThrow(DbException.class);

        List<Event> actualEventsByTitle = eventService.getEventsByTitle("not exists title", 1, 1);

        assertNull(actualEventsByTitle);
    }

    @Test
    public void getEventsByTitleWithEmptyTitleShouldReturnNull() {
        List<Event> actualEventsByTitle = eventService.getEventsByTitle("", 1, 2);

        assertNull(actualEventsByTitle);
    }

    @Test
    public void getEventsForDayWithExistsDayShouldBeOk() throws ParseException {
        Date day = DATE_FORMATTER.parse("15-05-2022 21:00");
        List<Event> expectedEvents = Arrays.asList(
                new EventImpl(2L, "Second event", DATE_FORMATTER.parse("15-05-2022 21:00")),
                new EventImpl(4L, "Fourth event", DATE_FORMATTER.parse("15-05-2022 21:00"))
        );

        when(eventDAO.getEventsForDay(eq(day), anyInt(), anyInt())).thenReturn(expectedEvents);

        List<Event> actualEvents = eventService.getEventsForDay(day, 2, 1);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    public void getEventsForDayWithExceptionShouldReturnNull() throws ParseException {
        Date day = DATE_FORMATTER.parse("15-05-2000 21:00");

        when(eventDAO.getEventsForDay(any(), anyInt(), anyInt())).thenThrow(DbException.class);

        List<Event> actualEventsForDay = eventService.getEventsForDay(day, 1, 1);

        assertNull(actualEventsForDay);
    }

    @Test
    public void getEventsForDayWithNullDayShouldReturnNull() {
        List<Event> actualEventsForDay = eventService.getEventsForDay(null, 1, 2);

        assertNull(actualEventsForDay);
    }

    @Test
    public void createEventWithExceptionShouldReturnNull() {
        when(eventDAO.insert(any())).thenThrow(DbException.class);

        Event actualEvent = eventService.createEvent(new EventImpl());

        assertNull(actualEvent);
    }

    @Test
    public void createEventWithExistsTitleAndEmailShouldReturnNull() throws ParseException {
        EventImpl expectedEvent = new EventImpl(1L, "Second event", DATE_FORMATTER.parse("15-05-2022 21:00"));

        when(eventDAO.insert(expectedEvent)).thenReturn(expectedEvent);

        Event actualEvent = eventService.createEvent(expectedEvent);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void createEventWithNullEventShouldReturnNull() {
        Event actualEvent = eventService.createEvent(null);

        assertNull(actualEvent);
    }

    @Test
    public void updateEventWithExistsEventShouldBeOk() throws ParseException {
        Event expectedEvent = new EventImpl(1L, "Second event", DATE_FORMATTER.parse("15-05-2022 21:00"));

        when(eventDAO.update(any())).thenReturn(expectedEvent);

        Event actualEvent = eventService.updateEvent(expectedEvent);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void updateEventWithExceptionShouldReturnNull() {
        when(eventDAO.update(any())).thenThrow(DbException.class);

        Event actualEvent = eventService.updateEvent(new EventImpl());

        assertNull(actualEvent);
    }

    @Test
    public void updateEventWithNullEventShouldReturnNull() {
        Event actualEvent = eventService.updateEvent(null);

        assertNull(actualEvent);
    }

    @Test
    public void deleteEventExistsEventShouldReturnTrue() {
        when(eventDAO.delete(anyLong())).thenReturn(true);

        boolean actualIsDeleted = eventService.deleteEvent(6L);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void deleteEventWithExceptionShouldReturnFalse() {
        when(eventDAO.delete(anyLong())).thenThrow(DbException.class);

        boolean actualIsDeleted = eventService.deleteEvent(10L);

        assertFalse(actualIsDeleted);
    }
}
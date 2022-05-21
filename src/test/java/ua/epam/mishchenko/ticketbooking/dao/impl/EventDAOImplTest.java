package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

@RunWith(MockitoJUnitRunner.class)
public class EventDAOImplTest {

    private EventDAOImpl eventDAO;

    @Mock
    private Storage storage;

    @Before
    public void setUp() {
        HashMap<String, String> inMemoryStorage = initInMemoryStorage();
        storage.setInMemoryStorage(inMemoryStorage);
        eventDAO = new EventDAOImpl();
        eventDAO.setStorage(storage);

        when(storage.getInMemoryStorage()).thenReturn(inMemoryStorage);
    }

    private HashMap<String, String> initInMemoryStorage() {
        HashMap<String, String> memoryStorage = new HashMap<>();
        memoryStorage.put("user:1", "'id' : 1, 'name' : 'Alan', 'email' : 'alan@gmail.com'");
        memoryStorage.put("user:2", "'id' : 2, 'name' : 'Kate', 'email' : 'kate@gmail.com'");
        memoryStorage.put("user:3", "'id' : 3, 'name' : 'Max', 'email' : 'max@gmail.com'");
        memoryStorage.put("user:4", "'id' : 4, 'name' : 'Sara', 'email' : 'sara@gmail.com'");
        memoryStorage.put("user:5", "'id' : 5, 'name' : 'Alex', 'email' : 'alex@gmail.com'");
        memoryStorage.put("user:6", "'id' : 6, 'name' : 'Alex', 'email' : 'anotheralex@gmail.com'");
        memoryStorage.put("ticket:1", "'id' : 1, 'userId' : 1, 'eventId' : 1, 'place' : 10, 'category' : 'BAR'");
        memoryStorage.put("ticket:2", "'id' : 2, 'userId' : 4, 'eventId' : 3, 'place' : 2, 'category' : 'PREMIUM'");
        memoryStorage.put("ticket:3", "'id' : 3, 'userId' : 2, 'eventId' : 2, 'place' : 4, 'category' : 'STANDARD'");
        memoryStorage.put("ticket:4", "'id' : 4, 'userId' : 1, 'eventId' : 4, 'place' : 20, 'category' : 'BAR'");
        memoryStorage.put("ticket:5", "'id' : 5, 'userId' : 5, 'eventId' : 1, 'place' : 11, 'category' : 'PREMIUM'");
        memoryStorage.put("ticket:6", "'id' : 6, 'userId' : 3, 'eventId' : 5, 'place' : 1, 'category' : 'STANDARD'");
        memoryStorage.put("event:1", "'id' : 1, 'title' : 'First event', 'date' : '18-05-2022 15:30'");
        memoryStorage.put("event:2", "'id' : 2, 'title' : 'Second event', 'date' : '15-05-2022 21:00'");
        memoryStorage.put("event:3", "'id' : 3, 'title' : 'Third event', 'date' : '16-05-2022 12:00'");
        memoryStorage.put("event:4", "'id' : 4, 'title' : 'Fourth event', 'date' : '15-05-2022 21:00'");
        memoryStorage.put("event:5", "'id' : 5, 'title' : 'Third event', 'date' : '25-05-2022 9:10'");
        memoryStorage.put("event:6", "'id' : 6, 'title' : 'Fifth event', 'date' : '1-06-2022 14:20'");
        return memoryStorage;
    }

    @Test
    public void getByIdWithExistsIdShouldBeOk() throws ParseException {
        Event event = new EventImpl(3L, "Third event", DATE_FORMATTER.parse("16-05-2022 12:00"));

        Event actualEvent = eventDAO.getById(3L);

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals(event, actualEvent);
    }

    @Test
    public void getByIdWithNotExistsIdShouldThrowException() {
        DbException dbException = assertThrows(DbException.class, () -> eventDAO.getById(10L));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("Can not to find an event by id: 10", dbException.getMessage());
    }

    @Test
    public void getByTitleWithExistsTitleShouldBeOk() throws ParseException {
        String title = "Third event";
        List<Event> expectedEvents = Arrays.asList(
                new EventImpl(3L, "Third event", DATE_FORMATTER.parse("16-05-2022 12:00")),
                new EventImpl(5L, "Third event", DATE_FORMATTER.parse("25-05-2022 9:10"))
        );
        List<Event> actualEvents = eventDAO.getEventsByTitle(title, 2, 1);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    public void getByTitleWithNotExistsTitleShouldThrowException() {
        String title = "not exists title";
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.getEventsByTitle(title, 1, 1));

        assertEquals("List of all events by title '" + title + "' is empty", dbException.getMessage());
    }

    @Test
    public void getByTitleWithExistsTitleAndWrongPageSizeShouldThrowException() {
        String title = "Third event";
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.getEventsByTitle(title, -1, 1));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The page size and page num must be greater than 0", dbException.getMessage());
    }

    @Test
    public void getByTitleWithExistsTitleAndIndexOutOfRangeParameterShouldThrowException() {
        String title = "Third event";
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.getEventsByTitle(title, 3, 1));

        assertEquals("The size of events list (size=2) is less than end page (last page=3)", dbException.getMessage());
    }

    @Test
    public void getForDayWithExistsDayShouldBeOk() throws ParseException {
        Date day = DATE_FORMATTER.parse("15-05-2022 21:00");
        List<Event> expectedEvents = Arrays.asList(
                new EventImpl(2L, "Second event", DATE_FORMATTER.parse("15-05-2022 21:00")),
                new EventImpl(4L, "Fourth event", DATE_FORMATTER.parse("15-05-2022 21:00"))
        );
        List<Event> actualEvents = eventDAO.getEventsForDay(day, 2, 1);

        assertTrue(expectedEvents.containsAll(actualEvents));
    }

    @Test
    public void getForDayWithNotExistsDayShouldThrowException() throws ParseException {
        Date day = DATE_FORMATTER.parse("15-05-2000 21:00");
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.getEventsForDay(day, 1, 1));

        assertEquals("List of all events for day '" + day + "' is empty", dbException.getMessage());
    }

    @Test
    public void getForDayWithExistsDayAndWrongPageSizeShouldThrowException() throws ParseException {
        Date day = DATE_FORMATTER.parse("15-05-2022 21:00");
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.getEventsForDay(day, -1, 1));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The page size and page num must be greater than 0", dbException.getMessage());
    }

    @Test
    public void getForDayWithExistsDayAndIndexOutOfRangeParameterShouldThrowException() throws ParseException {
        Date day = DATE_FORMATTER.parse("15-05-2022 21:00");
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.getEventsForDay(day, 3, 1));

        assertEquals("The size of events list (size=2) is less than end page (last page=3)", dbException.getMessage());
    }

    @Test
    public void insertWithEventShouldBeOk() {
        Event expectedEvent = new EventImpl("Test title", new Date(System.currentTimeMillis()));

        Event actualEvent = eventDAO.insert(expectedEvent);
        expectedEvent.setId(actualEvent.getId());

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void insertWithNullEventShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.insert(null));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The event can not equal a null", dbException.getMessage());
    }

    @Test
    public void insertWithExistsTitleAndEmailShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.insert(new EventImpl("Second event", DATE_FORMATTER.parse("15-05-2022 21:00"))));

        assertEquals("This email already exists", dbException.getMessage());
    }

    @Test
    public void updateWithExistsEventShouldBeOk() {
        String expectedTitle = "Test title";
        Event expectedEvent = eventDAO.getById(1L);
        expectedEvent.setTitle(expectedTitle);
        Event actualEvent = eventDAO.update(expectedEvent);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void updateWithNullEventShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.update(null));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The event can not equal a null", dbException.getMessage());
    }

    @Test
    public void updateEventWhichNotExistsShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.update(new EventImpl(10L, "Test User", new Date(System.currentTimeMillis()))));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("The event with id 10 does not exist", dbException.getMessage());
    }

    @Test
    public void deleteExistsEventShouldBeOk() {
        boolean actualIsDeleted = eventDAO.delete(6L);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void deleteEventWhichNotExistsShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> eventDAO.delete(10L));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("The event with id 10 does not exist", dbException.getMessage());
    }
}
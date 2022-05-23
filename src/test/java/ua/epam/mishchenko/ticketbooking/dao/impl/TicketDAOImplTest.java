package ua.epam.mishchenko.ticketbooking.dao.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.epam.mishchenko.ticketbooking.db.Storage;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;
import ua.epam.mishchenko.ticketbooking.model.impl.TicketImpl;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

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
import static ua.epam.mishchenko.ticketbooking.utils.StorageUtils.initInMemoryStorage;

@RunWith(MockitoJUnitRunner.class)
public class TicketDAOImplTest {

    private TicketDAOImpl ticketDAO;

    @Mock
    private Storage storage;

    @Before
    public void setUp() {
        HashMap<String, String> inMemoryStorage = initInMemoryStorage();
        storage.setInMemoryStorage(inMemoryStorage);
        ticketDAO = new TicketDAOImpl();
        ticketDAO.setStorage(storage);

        when(storage.getInMemoryStorage()).thenReturn(inMemoryStorage);
    }

    @Test
    public void getByIdWithExistsIdShouldBeOk() {
        Ticket expectedTicket = new TicketImpl(5L, 5L, 1L, 11, Ticket.Category.PREMIUM);
        Ticket actualTicket = ticketDAO.getById(5L);

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    public void getByIdWithNotExistsIdShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.getById(10L));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("Can not to find a ticket by id: 10", dbException.getMessage());
    }

    @Test
    public void getAllShouldBeOk() {
        List<Ticket> expectedListOfTickets = Arrays.asList(
                new TicketImpl(4L, 1L, 4L, 20, Ticket.Category.BAR),
                new TicketImpl(2L, 4L, 3L, 2, Ticket.Category.PREMIUM),
                new TicketImpl(3L, 2L, 2L, 4, Ticket.Category.STANDARD),
                new TicketImpl(1L, 1L, 1L, 10, Ticket.Category.BAR)
        );
        List<Ticket> actualListOfTicket = ticketDAO.getAll();

        assertTrue(actualListOfTicket.containsAll(expectedListOfTickets));
    }

    @Test
    public void getAllByUserWithExistsUserIdShouldBeOk() {
        User user = new UserImpl();
        user.setId(1L);
        List<Ticket> expectedListOfTickets = Arrays.asList(
                new TicketImpl(4L, 1L, 4L, 20, Ticket.Category.BAR),
                new TicketImpl(1L, 1L, 1L, 10, Ticket.Category.BAR)
        );
        List<Ticket> actualListOfTickets = ticketDAO.getAllByUser(user, 2, 1);

        assertTrue(expectedListOfTickets.containsAll(actualListOfTickets));
    }

    @Test
    public void getAllByUserWithNotExistsUserShouldThrowException() {
        User user = new UserImpl();
        user.setId(100L);
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.getAllByUser(user, 1, 1));

        assertEquals("List of all booked tickets by user " + user + " is empty", dbException.getMessage());
    }

    @Test
    public void getAllByUserWithExistsUserAndWrongPageSizeShouldThrowException() {
        User user = new UserImpl();
        user.setId(1L);
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.getAllByUser(user, -1, 1));

        assertEquals("The page size and page num must be greater than 0", dbException.getMessage());
    }

    @Test
    public void getAllByEventWithExistsUserIdShouldBeOk() {
        Event event = new EventImpl();
        event.setId(1L);
        List<Ticket> expectedListOfTickets = Arrays.asList(
                new TicketImpl(5L, 5L, 1L, 11, Ticket.Category.PREMIUM),
                new TicketImpl(1L, 1L, 1L, 10, Ticket.Category.BAR)
        );
        List<Ticket> actualListOfTickets = ticketDAO.getAllByEvent(event, 2, 1);

        assertTrue(expectedListOfTickets.containsAll(actualListOfTickets));
    }

    @Test
    public void getAllByEventWithNotExistsEmailShouldThrowException() {
        Event event = new EventImpl();
        event.setId(100L);
        event.setDate(new Date(System.currentTimeMillis()));
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.getAllByEvent(event, 1, 1));

        assertEquals("List of all booked tickets by event " + event + " is empty",
                dbException.getMessage());
    }

    @Test
    public void getByNameWithExistsNameAndWrongPageSizeShouldThrowException() {
        Event event = new EventImpl();
        event.setId(1L);
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.getAllByEvent(event, -1, 1));

        assertEquals("The page size and page num must be greater than 0", dbException.getMessage());
    }

    @Test
    public void insertWithTicketShouldBeOk() {
        Ticket expectedTicket = new TicketImpl(5L, 3L, 20, Ticket.Category.STANDARD);
        Ticket actualTicket = ticketDAO.insert(expectedTicket);
        expectedTicket.setId(6L);

        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    public void insertWithBookedTicketShouldThrowException() {
        Ticket expectedTicket = new TicketImpl(5L, 4L, 20, Ticket.Category.STANDARD);
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.insert(expectedTicket));

        assertEquals("This ticket already booked", dbException.getMessage());
    }

    @Test
    public void insertWithNullTicketShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.insert(null));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The ticket can not equal a null", dbException.getMessage());
    }

    @Test
    public void updateWithExistsTicketShouldBeOk() {
        Ticket expectedTicket = ticketDAO.getById(5L);
        expectedTicket.setCategory(Ticket.Category.STANDARD);
        Ticket actualTicket = ticketDAO.update(expectedTicket);

        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    public void updateWithNullTicketShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.update(null));

        verify(storage, times(0)).getInMemoryStorage();

        assertEquals("The ticket can not equal a null", dbException.getMessage());
    }

    @Test
    public void updateTicketWhichNotExistsShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.update(new TicketImpl(10L, 10L, 10L, 100, Ticket.Category.BAR)));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("The ticket with id 10 does not exist", dbException.getMessage());
    }

    @Test
    public void deleteExistsTicketShouldBeOk() {
        boolean actualIsDeleted = ticketDAO.delete(6L);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void deleteTicketWhichNotExistsShouldThrowException() {
        DbException dbException = assertThrows(DbException.class,
                () -> ticketDAO.delete(10L));

        verify(storage, times(1)).getInMemoryStorage();

        assertEquals("The ticket with id 10 does not exist", dbException.getMessage());
    }
}
package ua.epam.mishchenko.ticketbooking.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ua.epam.mishchenko.ticketbooking.dao.impl.TicketDAOImpl;
import ua.epam.mishchenko.ticketbooking.exception.DbException;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;
import ua.epam.mishchenko.ticketbooking.model.impl.TicketImpl;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

@RunWith(MockitoJUnitRunner.class)
public class TicketServiceImplTest {

    private TicketServiceImpl ticketService;

    @Mock
    private TicketDAOImpl ticketDAO;

    @Before
    public void setUp() {
        ticketService = new TicketServiceImpl();
        ticketService.setTicketDAO(ticketDAO);
    }

    @Test
    public void bookTicketWithNotBookedTicketShouldBeOk() {
        Ticket expectedTicket = new TicketImpl(1L, 10L, 10L, 20, Ticket.Category.PREMIUM);

        when(ticketDAO.insert(any())).thenReturn(expectedTicket);

        Ticket actualTicket = ticketService.bookTicket(10L, 10L, 20, Ticket.Category.PREMIUM);

        assertEquals(expectedTicket, actualTicket);
    }

    @Test
    public void bookTicketWithExceptionShouldReturnNull() {
        when(ticketDAO.insert(any())).thenThrow(DbException.class);

        Ticket actualTicket = ticketService.bookTicket(4L, 1L, 10, Ticket.Category.BAR);

        assertNull(actualTicket);
    }

    @Test
    public void getBookedTicketsWithNotNullUserAndProperPageSizeAndPageNumShouldBeOk() {
        User user = new UserImpl(1, "Alan", "alan@gmail.com");
        List<Ticket> expectedListOfTicketsByUser = Arrays.asList(
                new TicketImpl(1L, 1L, 1L, 10, Ticket.Category.BAR),
                new TicketImpl(4L, 1L, 4L, 20, Ticket.Category.BAR)
        );

        when(ticketDAO.getAllByUser(any(), anyInt(), anyInt())).thenReturn(expectedListOfTicketsByUser);

        List<Ticket> actualListOfTicketsByUser = ticketService.getBookedTickets(user, 2, 1);

        assertEquals(expectedListOfTicketsByUser, actualListOfTicketsByUser);
    }

    @Test
    public void getBookedTicketsByUserWithExceptionShouldReturnNull() {
        when(ticketDAO.getAllByUser(any(), anyInt(), anyInt())).thenThrow(DbException.class);

        List<Ticket> actualListOfTicketsByUser = ticketService.getBookedTickets(new UserImpl(), 2, 1);

        assertNull(actualListOfTicketsByUser);
    }

    @Test
    public void getBookedTicketsByUserWithNullUserShouldReturnNull() {
        List<Ticket> actualTicketsByUser = ticketService.getBookedTickets((User) null, 1, 2);

        assertNull(actualTicketsByUser);
    }

    @Test
    public void getBookedTicketsWithNotNullEventAndProperPageSizeAndPageNumShouldBeOk() throws ParseException {
        Event event = new EventImpl(4, "Fourth event", DATE_FORMATTER.parse("15-05-2022 21:00"));
        List<Ticket> expectedListOfTicketsByEvent = Arrays.asList(
                new TicketImpl(4L, 1L, 4L, 20, Ticket.Category.BAR),
                new TicketImpl(2L, 3L, 4L, 10, Ticket.Category.PREMIUM)
        );

        when(ticketDAO.getAllByEvent(any(), anyInt(), anyInt())).thenReturn(expectedListOfTicketsByEvent);

        List<Ticket> actualListOfTicketsByEvent = ticketService.getBookedTickets(event, 2, 1);

        assertEquals(expectedListOfTicketsByEvent, actualListOfTicketsByEvent);
    }

    @Test
    public void getBookedTicketsByEventWithExceptionShouldReturnNull() {
        when(ticketDAO.getAllByEvent(any(), anyInt(), anyInt())).thenThrow(DbException.class);

        List<Ticket> actualListOfTicketsByEvent = ticketService.getBookedTickets(new EventImpl(), 2, 1);

        assertNull(actualListOfTicketsByEvent);
    }

    @Test
    public void getBookedTicketsWithNullEventShouldReturnNull() {
        List<Ticket> actualTicketsByEvent = ticketService.getBookedTickets((Event) null, 1, 2);

        assertNull(actualTicketsByEvent);
    }

    @Test
    public void cancelTicketExistsTicketShouldReturnTrue() {
        when(ticketDAO.delete(anyLong())).thenReturn(true);

        boolean actualIsDeleted = ticketService.cancelTicket(6L);

        assertTrue(actualIsDeleted);
    }

    @Test
    public void cancelTicketWithExceptionShouldReturnFalse() {
        when(ticketDAO.delete(anyLong())).thenThrow(DbException.class);

        boolean isRemoved = ticketService.cancelTicket(10L);

        assertFalse(isRemoved);
    }
}
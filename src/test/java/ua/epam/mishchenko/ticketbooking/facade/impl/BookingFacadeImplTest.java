package ua.epam.mishchenko.ticketbooking.facade.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.impl.EventImpl;
import ua.epam.mishchenko.ticketbooking.model.impl.UserImpl;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:/test-applicationContext.xml"})
public class BookingFacadeImplTest {

    @Autowired
    private BookingFacadeImpl bookingFacade;

    @Test
    public void createUserThenCreateEventThenBookTicketForThisEventForUserAndThenCancelItShouldBeOk() {
        User user = new UserImpl("Andrii", "andrii@gmail.com");
        Event event = new EventImpl("Integration Event", new Date(System.currentTimeMillis()));
        int place = 10;

        user = bookingFacade.createUser(user);

        assertNotNull(bookingFacade.getUserById(user.getId()));

        event = bookingFacade.createEvent(event);

        assertNotNull(bookingFacade.getEventById(event.getId()));

        Ticket ticket = bookingFacade.bookTicket(user.getId(), event.getId(), place, Ticket.Category.STANDARD);

        List<Ticket> bookedTicketsByUserBeforeCanceling = bookingFacade.getBookedTickets(user, 1, 1);
        List<Ticket> bookedTicketsByEventBeforeCanceling = bookingFacade.getBookedTickets(event, 1, 1);

        assertTrue(bookedTicketsByUserBeforeCanceling.contains(ticket));
        assertTrue(bookedTicketsByEventBeforeCanceling.contains(ticket));

        bookingFacade.cancelTicket(ticket.getId());

        List<Ticket> bookedTicketsByUserAfterCanceling = bookingFacade.getBookedTickets(user, 1, 1);
        List<Ticket> bookedTicketsByEventAfterCanceling = bookingFacade.getBookedTickets(event, 1, 1);

        assertNull(bookedTicketsByUserAfterCanceling);
        assertNull(bookedTicketsByEventAfterCanceling);
    }

}
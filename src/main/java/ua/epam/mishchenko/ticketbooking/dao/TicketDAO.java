package ua.epam.mishchenko.ticketbooking.dao;

import ua.epam.mishchenko.ticketbooking.model.Ticket;

import java.util.List;

/**
 * The interface Ticket dao.
 */
public interface TicketDAO {

    /**
     * Gets by id.
     *
     * @param id the id
     * @return the by id
     */
    Ticket getById(long id);

    /**
     * Gets all.
     *
     * @return the all
     */
    List<Ticket> getAll();

    /**
     * Insert ticket.
     *
     * @param ticket the ticket
     * @return the ticket
     */
    Ticket insert(Ticket ticket);

    /**
     * Update ticket.
     *
     * @param ticket the ticket
     * @return the ticket
     */
    Ticket update(Ticket ticket);

    /**
     * Delete boolean.
     *
     * @param ticketId the ticket id
     * @return the boolean
     */
    boolean delete(long ticketId);
}

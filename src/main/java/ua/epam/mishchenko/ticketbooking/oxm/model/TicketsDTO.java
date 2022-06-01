package ua.epam.mishchenko.ticketbooking.oxm.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * The type Tickets dto.
 */
@XmlRootElement(name = "tickets")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketsDTO {

    /**
     * The Tickets.
     */
    @XmlElement(name = "ticket")
    private List<TicketDTO> tickets = null;

    /**
     * Gets tickets.
     *
     * @return the tickets
     */
    public List<TicketDTO> getTickets() {
        return tickets;
    }

    /**
     * Sets tickets.
     *
     * @param tickets the tickets
     */
    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }
}

package ua.epam.mishchenko.ticketbooking.oxm.model;

import ua.epam.mishchenko.ticketbooking.model.Ticket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The type Ticket dto.
 */
@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketDTO {

    /**
     * The User id.
     */
    @XmlAttribute(name = "user")
    private long userId;

    /**
     * The Event id.
     */
    @XmlAttribute(name = "event")
    private long eventId;

    /**
     * The Place.
     */
    @XmlAttribute(name = "place")
    private int place;

    /**
     * The Category.
     */
    @XmlAttribute(name = "category")
    private Ticket.Category category;

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets event id.
     *
     * @return the event id
     */
    public long getEventId() {
        return eventId;
    }

    /**
     * Sets event id.
     *
     * @param eventId the event id
     */
    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    public int getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place
     */
    public void setPlace(int place) {
        this.place = place;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public Ticket.Category getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(Ticket.Category category) {
        this.category = category;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "TicketDTO{" +
                "userId=" + userId +
                ", eventId=" + eventId +
                ", place=" + place +
                ", category=" + category +
                '}';
    }
}

package ua.epam.mishchenko.ticketbooking.oxm.model;

import ua.epam.mishchenko.ticketbooking.model.Ticket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketDTO {

    @XmlAttribute(name = "user")
    private long userId;

    @XmlAttribute(name = "event")
    private long eventId;

    @XmlAttribute(name = "place")
    private int place;

    @XmlAttribute(name = "category")
    private Ticket.Category category;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public Ticket.Category getCategory() {
        return category;
    }

    public void setCategory(Ticket.Category category) {
        this.category = category;
    }

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

package ua.epam.mishchenko.ticketbooking.model.impl;

import ua.epam.mishchenko.ticketbooking.model.Ticket;

import java.util.Objects;

/**
 * The type Ticket.
 */
public class TicketImpl implements Ticket {

    /**
     * The Id.
     */
    private Long id;

    /**
     * The User id.
     */
    private Long userId;

    /**
     * The Event id.
     */
    private Long eventId;

    /**
     * The Place.
     */
    private int place;

    /**
     * The Category.
     */
    private Category category;

    /**
     * Instantiates a new Ticket.
     */
    public TicketImpl() {}

    /**
     * Instantiates a new Ticket.
     *
     * @param id       the id
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     */
    public TicketImpl(Long id, Long userId, Long eventId, int place, Category category) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.place = place;
        this.category = category;
    }

    /**
     * Instantiates a new Ticket.
     *
     * @param userId   the user id
     * @param eventId  the event id
     * @param place    the place
     * @param category the category
     */
    public TicketImpl(Long userId, Long eventId, int place, Category category) {
        this.userId = userId;
        this.eventId = eventId;
        this.place = place;
        this.category = category;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets event id.
     *
     * @return the event id
     */
    @Override
    public long getEventId() {
        return eventId;
    }

    /**
     * Sets event id.
     *
     * @param eventId the event id
     */
    @Override
    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    @Override
    public long getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    @Override
    public Category getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets place.
     *
     * @return the place
     */
    @Override
    public int getPlace() {
        return place;
    }

    /**
     * Sets place.
     *
     * @param place the place
     */
    @Override
    public void setPlace(int place) {
        this.place = place;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketImpl ticket = (TicketImpl) o;
        return place == ticket.place && Objects.equals(id, ticket.id) && Objects.equals(userId, ticket.userId) && Objects.equals(eventId, ticket.eventId) && category == ticket.category;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, eventId, place, category);
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "{" +
                "'id' : " + id +
                ", 'userId' : " + userId +
                ", 'eventId' : " + eventId +
                ", 'place' : " + place +
                ", 'category' : '" + category +
                "'}";
    }
}

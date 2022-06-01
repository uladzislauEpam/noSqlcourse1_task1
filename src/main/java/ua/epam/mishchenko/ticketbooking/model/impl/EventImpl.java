package ua.epam.mishchenko.ticketbooking.model.impl;

import ua.epam.mishchenko.ticketbooking.model.Event;

import java.util.Date;
import java.util.Objects;

import static ua.epam.mishchenko.ticketbooking.utils.Constants.DATE_FORMATTER;

/**
 * The type Event.
 */
public class EventImpl implements Event {

    /**
     * The Id.
     */
    private long id;

    /**
     * The Title.
     */
    private String title;

    /**
     * The Date.
     */
    private Date date;

    /**
     * Instantiates a new Event.
     */
    public EventImpl() {}

    /**
     * Instantiates a new Event.
     *
     * @param title the title
     * @param date  the date
     */
    public EventImpl(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    /**
     * Instantiates a new Event.
     *
     * @param id    the id
     * @param title the title
     * @param date  the date
     */
    public EventImpl(long id, String title, Date date) {
        this.id = id;
        this.title = title;
        this.date = date;
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
     * Gets title.
     *
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    @Override
    public void setDate(Date date) {
        this.date = date;
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
        EventImpl event = (EventImpl) o;
        return id == event.id && Objects.equals(title, event.title) && Objects.equals(date, event.date);
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, date);
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
                ", 'title' : '" + title + '\'' +
                ", 'date' : '" + DATE_FORMATTER.format(date) +
                "'}";
    }
}

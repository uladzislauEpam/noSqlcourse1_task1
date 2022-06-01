package ua.epam.mishchenko.ticketbooking.model.impl;

import ua.epam.mishchenko.ticketbooking.dao.impl.UserDAOImpl;
import ua.epam.mishchenko.ticketbooking.model.User;

import java.util.Objects;

/**
 * The type User.
 */
public class UserImpl implements User, Comparable<UserImpl> {

    /**
     * The Id.
     */
    private long id;

    /**
     * The Name.
     */
    private String name;

    /**
     * The Email.
     */
    private String email;

    /**
     * Instantiates a new User.
     */
    public UserImpl() {}

    /**
     * Instantiates a new User.
     *
     * @param id    the id
     * @param name  the name
     * @param email the email
     */
    public UserImpl(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Instantiates a new User.
     *
     * @param name  the name
     * @param email the email
     */
    public UserImpl(String name, String email) {
        this.name = name;
        this.email = email;
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
     * Gets name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
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
        UserImpl user = (UserImpl) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(email, user.email);
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
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
                ", 'name' : '" + name + '\'' +
                ", 'email' : '" + email + '\'' +
                '}';
    }

    /**
     * Compare to int.
     *
     * @param o the o
     * @return the int
     */
    @Override
    public int compareTo(UserImpl o) {
        return Long.compare(this.id, o.id);
    }
}


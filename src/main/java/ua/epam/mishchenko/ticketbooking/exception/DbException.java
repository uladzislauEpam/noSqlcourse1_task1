package ua.epam.mishchenko.ticketbooking.exception;

/**
 * The type Db exception.
 */
public class DbException extends RuntimeException {

    /**
     * Instantiates a new Db exception.
     *
     * @param message the message
     */
    public DbException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Db exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}

package ua.epam.mishchenko.ticketbooking.db;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Storage.
 */
public class Storage {

    /**
     * The In memory storage.
     */
    private Map<String, String> inMemoryStorage = new HashMap<>();

    /**
     * The constant STORAGE.
     */
    private static Storage STORAGE;

    /**
     * Instantiates a new Storage.
     */
    private Storage() {}

    /**
     * Gets storage.
     *
     * @return the storage
     */
    public static Storage getStorage() {
        if (STORAGE == null) {
            STORAGE = new Storage();
        }
        return STORAGE;
    }

    /**
     * Gets in memory storage.
     *
     * @return the in memory storage
     */
    public Map<String, String> getInMemoryStorage() {
        return inMemoryStorage;
    }

    /**
     * Sets in memory storage.
     *
     * @param inMemoryStorage the in memory storage
     */
    public void setInMemoryStorage(Map<String, String> inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }
}

package ua.epam.mishchenko.ticketbooking.db;

import java.util.HashMap;
import java.util.Map;

public class Storage {

    private Map<String, String> inMemoryStorage = new HashMap<>();

    private static Storage STORAGE;

    private Storage() {}

    public static Storage getStorage() {
        if (STORAGE == null) {
            STORAGE = new Storage();
        }
        return STORAGE;
    }

    public Map<String, String> getInMemoryStorage() {
        return inMemoryStorage;
    }

    public void setInMemoryStorage(Map<String, String> inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }
}

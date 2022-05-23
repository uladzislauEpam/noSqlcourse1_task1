package ua.epam.mishchenko.ticketbooking.utils;

import java.util.HashMap;

public class StorageUtils {

    public static HashMap<String, String> initInMemoryStorage() {
        HashMap<String, String> memoryStorage = new HashMap<>();
        memoryStorage.put("user:1", "'id' : 1, 'name' : 'Alan', 'email' : 'alan@gmail.com'");
        memoryStorage.put("user:2", "'id' : 2, 'name' : 'Kate', 'email' : 'kate@gmail.com'");
        memoryStorage.put("user:3", "'id' : 3, 'name' : 'Max', 'email' : 'max@gmail.com'");
        memoryStorage.put("user:4", "'id' : 4, 'name' : 'Sara', 'email' : 'sara@gmail.com'");
        memoryStorage.put("user:5", "'id' : 5, 'name' : 'Alex', 'email' : 'alex@gmail.com'");
        memoryStorage.put("user:6", "'id' : 6, 'name' : 'Alex', 'email' : 'anotheralex@gmail.com'");
        memoryStorage.put("ticket:1", "'id' : 1, 'userId' : 1, 'eventId' : 1, 'place' : 10, 'category' : 'BAR'");
        memoryStorage.put("ticket:2", "'id' : 2, 'userId' : 4, 'eventId' : 3, 'place' : 2, 'category' : 'PREMIUM'");
        memoryStorage.put("ticket:3", "'id' : 3, 'userId' : 2, 'eventId' : 2, 'place' : 4, 'category' : 'STANDARD'");
        memoryStorage.put("ticket:4", "'id' : 4, 'userId' : 1, 'eventId' : 4, 'place' : 20, 'category' : 'BAR'");
        memoryStorage.put("ticket:5", "'id' : 5, 'userId' : 5, 'eventId' : 1, 'place' : 11, 'category' : 'PREMIUM'");
        memoryStorage.put("ticket:6", "'id' : 6, 'userId' : 3, 'eventId' : 5, 'place' : 1, 'category' : 'STANDARD'");
        memoryStorage.put("event:1", "'id' : 1, 'title' : 'First event', 'date' : '18-05-2022 15:30'");
        memoryStorage.put("event:2", "'id' : 2, 'title' : 'Second event', 'date' : '15-05-2022 21:00'");
        memoryStorage.put("event:3", "'id' : 3, 'title' : 'Third event', 'date' : '16-05-2022 12:00'");
        memoryStorage.put("event:4", "'id' : 4, 'title' : 'Fourth event', 'date' : '15-05-2022 21:00'");
        memoryStorage.put("event:5", "'id' : 5, 'title' : 'Third event', 'date' : '25-05-2022 9:10'");
        memoryStorage.put("event:6", "'id' : 6, 'title' : 'Fifth event', 'date' : '1-06-2022 14:20'");
        return memoryStorage;
    }
}

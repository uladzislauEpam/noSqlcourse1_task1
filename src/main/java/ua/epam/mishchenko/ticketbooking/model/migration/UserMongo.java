package ua.epam.mishchenko.ticketbooking.model.migration;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserMongo {

    @Id
    private String id;
    private String name;
    private String email;

}

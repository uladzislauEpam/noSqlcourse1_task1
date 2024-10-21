package ua.epam.mishchenko.ticketbooking.model.migration;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class UserMongo {

    @Id
    private String id;
    private String name;
    private String email;
    private UserAccount userAccount;
    @DBRef
    private final List<Ticket> tickets = new ArrayList<>();

}

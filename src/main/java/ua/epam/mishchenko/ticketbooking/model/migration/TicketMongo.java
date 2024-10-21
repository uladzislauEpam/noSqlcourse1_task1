package ua.epam.mishchenko.ticketbooking.model.migration;

import org.springframework.data.mongodb.core.mapping.Document;
import ua.epam.mishchenko.ticketbooking.model.Category;
import ua.epam.mishchenko.ticketbooking.model.Event;
import ua.epam.mishchenko.ticketbooking.model.User;

import javax.persistence.*;

@Document(collection = "tickets")
public class TicketMongo {

    @Id
    private Long id;
    private User user;
    private Event event;
    private Integer place;
    private Category category;

}

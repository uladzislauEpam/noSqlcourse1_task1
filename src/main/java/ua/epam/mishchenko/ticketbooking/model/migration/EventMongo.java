package ua.epam.mishchenko.ticketbooking.model.migration;

import org.springframework.data.mongodb.core.mapping.Document;
import ua.epam.mishchenko.ticketbooking.model.Ticket;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "events")
public class EventMongo {

    @Id
    private Long id;
    private String title;
    private Date date;
    private BigDecimal ticketPrice;
    private final List<Ticket> tickets = new ArrayList<>();

}

package ua.epam.mishchenko.ticketbooking.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.epam.mishchenko.ticketbooking.facade.impl.BookingFacadeImpl;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.utils.CreatePDF;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Controller
@RequestMapping(value = "/tickets/user", produces = APPLICATION_PDF_VALUE)
public class BookedTicketsPDFController {

    private static final Logger log = LoggerFactory.getLogger(TicketsController.class);

    private final BookingFacadeImpl bookingFacade;

    public BookedTicketsPDFController(BookingFacadeImpl bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getBookedTicketsByUserPDF(@PathVariable long userId,
                                                            @RequestParam int pageSize,
                                                            @RequestParam int pageNum) {
        log.info("Showing the tickets by user with id: {}", userId);

        User userById = getUserById(userId);
        List<Ticket> bookedTickets = getBookedTickets(userId, pageSize, pageNum, userById);

        log.info("The tickets successfully found");

        CreatePDF.createPDFFileOfBookedTicketsByUser(bookedTickets);
        return createResponseEntityWithPDFDocument(userId);
    }

    private ResponseEntity<Object> createResponseEntityWithPDFDocument(long userId) {
        try {
            Path path = Paths.get("Booked Tickets.pdf");
            InputStream inputStream = Files.newInputStream(path);
            InputStreamResource pdfDocument = new InputStreamResource(inputStream);
            deletePDFDocument(path);
            return new ResponseEntity<>(pdfDocument, HttpStatus.OK);
        } catch (IOException e) {
            log.warn("Can not to download pdf document of booked ticket by user with id: {}", userId, e);
            throw new RuntimeException("Can not to download pdf document of booked tickets by user with id: " + userId, e);
        }
    }

    private static void deletePDFDocument(Path path) {
        log.info("Removing pdf file with path: {}", path);
        try {
            Files.delete(path);
            log.info("The pdf file with path: '{}' successfully removed", path);
        } catch (IOException e) {
            log.info("Can not to remove a pdf file with path: {}", path, e);
            throw new RuntimeException("Can not to remove a pdf file with path: " + path, e);
        }
    }

    private List<Ticket> getBookedTickets(long userId, int pageSize, int pageNum, User userById) {
        List<Ticket> bookedTickets = bookingFacade.getBookedTickets(userById, pageSize, pageNum);
        if (bookedTickets.isEmpty()) {
            log.info("Can not to find the tickets by user with id: {}", userId);
            throw new RuntimeException("Can not to find the tickets by user with id: " + userId);
        }
        return bookedTickets;
    }

    private User getUserById(long userId) {
        User userById = bookingFacade.getUserById(userId);
        if (isNull(userById)) {
            log.info("Can not to find a user by id: {}", userId);
            throw new RuntimeException("Can not to find a user by id: " + userId);
        }
        return userById;
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}

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
import ua.epam.mishchenko.ticketbooking.utils.PDFUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Controller
@RequestMapping(value = "/tickets/user", produces = APPLICATION_PDF_VALUE)
public class BookedTicketsPDFController {

    private static final Logger log = LoggerFactory.getLogger(TicketsController.class);

    private final BookingFacadeImpl bookingFacade;

    private final PDFUtils pdfUtils;

    public BookedTicketsPDFController(BookingFacadeImpl bookingFacade, PDFUtils pdfUtils) {
        this.bookingFacade = bookingFacade;
        this.pdfUtils = pdfUtils;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getBookedTicketsByUserPDF(@PathVariable long userId,
                                                            @RequestParam int pageSize,
                                                            @RequestParam int pageNum) {
        log.info("Showing the tickets by user with id: {}", userId);

        User userById = getUserById(userId);
        List<Ticket> bookedTickets = getBookedTickets(userId, pageSize, pageNum, userById);

        log.info("The tickets successfully found");

        return createResponseEntityWithPDFDocument(bookedTickets);
    }

    private User getUserById(long userId) {
        User userById = bookingFacade.getUserById(userId);
        if (isNull(userById)) {
            log.info("Can not to find a user by id: {}", userId);
            throw new RuntimeException("Can not to find a user by id: " + userId);
        }
        return userById;
    }

    private List<Ticket> getBookedTickets(long userId, int pageSize, int pageNum, User userById) {
        List<Ticket> bookedTickets = bookingFacade.getBookedTickets(userById, pageSize, pageNum);
        if (bookedTickets.isEmpty()) {
            log.info("Can not to find the tickets by user with id: {}", userId);
            throw new RuntimeException("Can not to find the tickets by user with id: " + userId);
        }
        return bookedTickets;
    }

    private ResponseEntity<Object> createResponseEntityWithPDFDocument(List<Ticket> bookedTickets) {
        createPDFDocument(bookedTickets);
        InputStreamResource pdfDocument = pdfUtils.getPDFDocument();
        pdfUtils.deletePDFDocument();
        return new ResponseEntity<>(pdfDocument, HttpStatus.OK);
    }

    private void createPDFDocument(List<Ticket> bookedTickets) {
        Path path = Paths.get("Booked Tickets.pdf");
        pdfUtils.setTickets(bookedTickets);
        pdfUtils.createPDFFileOfBookedTicketsByUser();
        pdfUtils.setPath(path);
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}

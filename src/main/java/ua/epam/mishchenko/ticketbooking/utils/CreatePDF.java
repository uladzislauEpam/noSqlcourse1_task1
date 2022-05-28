package ua.epam.mishchenko.ticketbooking.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.epam.mishchenko.ticketbooking.model.Ticket;
import ua.epam.mishchenko.ticketbooking.model.impl.TicketImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CreatePDF {

    private static final Logger log = LoggerFactory.getLogger(CreatePDF.class);

    public static void createPDFFileOfBookedTicketsByUser(List<Ticket> tickets) {
        log.info("Creating a pdf file of booked tickets by user");
        Document doc = new Document();
        try {
            Path path = Paths.get("Booked Tickets.pdf");
            PdfWriter writer = PdfWriter.getInstance(doc, createFile(path));
            doc.open();
            doc.add(createTableAndInsertDate(tickets));
            doc.close();
            writer.close();
            log.info("The pdf file of booked tickets by user successfully created");
        } catch (DocumentException e) {
            log.info("Can not to create a pdf file of booked tickets by user", e);
            throw new RuntimeException("Can not to create a pdf fle of booked tickets by user", e);
        }
    }

    private static OutputStream createFile(Path path) {
        log.info("Creating pdf file with path: {}", path);
        try {
            OutputStream outputStream = Files.newOutputStream(path);
            log.info("The pdf file with path: '{}' successfully created", path);
            return outputStream;
        } catch (IOException e) {
            log.info("Can not to create a pdf file with path: {}", path, e);
            throw new RuntimeException("Can not to create a pdf file with path: " + path, e);
        }
    }

    private static PdfPTable createTableAndInsertDate(List<Ticket> tickets) {
        PdfPTable table = createTable();
        insertDataInTable(table, tickets);
        return table;
    }

    private static PdfPTable createTable() {
        PdfPTable table = new PdfPTable(5);
        createAndAddCells(table, "ID", "User ID", "Event ID", "Place", "Category");
        table.setHeaderRows(1);
        return table;
    }

    private static void createAndAddCells(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value));
            table.addCell(cell);
        }
    }

    private static void insertDataInTable(PdfPTable table, List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            createAndAddCells(table,
                    String.valueOf(ticket.getId()),
                    String.valueOf(ticket.getUserId()),
                    String.valueOf(ticket.getEventId()),
                    String.valueOf(ticket.getPlace()),
                    String.valueOf(ticket.getCategory()));
        }
    }
}

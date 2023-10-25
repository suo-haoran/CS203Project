package com.cs203g3.ticketing.emailAttachments;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.email.attachments.EmailAttachmentService;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.seat.Seat;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.venue.Venue;

@SpringBootTest
@ContextConfiguration(classes = { EmailAttachmentService.class, TemplateEngine.class })
@TestInstance(Lifecycle.PER_CLASS)
public class ThymeleafToHtmlIntegrationTest {

    @Autowired
    private EmailAttachmentService attachmentService;

    private static final String DIR = "./email-attachments/";

    private final User USER = new User(
            1L, "testuser1", "", "exam@gmail.com", "12312312", "SG", new Date(), null);

    private final ConcertSession CONCERT_SESSION = new ConcertSession(
            1L, LocalDateTime.now(), new Concert(
                    1L, "testconcert1", "testdescription1", "artist",
                    new Venue(1L, "Singapore National Stadium", new ArrayList<>(),
                            new ArrayList<>()), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
            new ArrayList<>());

    private final Ticket[] TICKETS = new Ticket[] {
            new Ticket(UUID.randomUUID(),new Seat(1L, "A2", 12,
                    new Section(1L, "Section 1", new Category(), new ArrayList<>())),
                    CONCERT_SESSION, new Receipt()),
             new Ticket(UUID.randomUUID(),new Seat(1L, "A2", 13,
                    new Section(1L, "Section 1", new Category(), new ArrayList<>())),
                    CONCERT_SESSION, new Receipt()),
             new Ticket(UUID.randomUUID(),new Seat(1L, "A2", 14,
                    new Section(1L, "Section 1", new Category(), new ArrayList<>())),
                    CONCERT_SESSION, new Receipt()),
             new Ticket(UUID.randomUUID(),new Seat(1L, "A2", 15,
                    new Section(1L, "Section 1", new Category(), new ArrayList<>())),
                    CONCERT_SESSION, new Receipt()),
    };

    

    private final Receipt INVOICE = new Receipt(
            UUID.randomUUID(),
            USER,
            new BigDecimal(100.0),
            Arrays.stream(TICKETS).toList());

    @BeforeAll
    public void setup() {
        File file = new File(DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Test
    public void generateReceiptFromThymeleaf() throws IOException {
        String path = attachmentService.generateReceipt(USER, TICKETS, CONCERT_SESSION, INVOICE);
        assertTrue(Files.exists(Paths.get(path)));
        Files.deleteIfExists(Paths.get(path));
    }

    @Test
    public void generateTicketsFromThymeleaf() throws IOException {
        String path = attachmentService.generateTickets(USER, TICKETS, CONCERT_SESSION);
        assertTrue(Files.exists(Paths.get(path)));
        Files.deleteIfExists(Paths.get(path));
    }

    
}

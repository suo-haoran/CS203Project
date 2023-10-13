package com.cs203g3.ticketing.email;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.email.htmlToPdf.PdfGenerator;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.seat.Seat;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.venue.Venue;

import jakarta.mail.MessagingException;

@SpringBootTest
@ContextConfiguration(classes = { EmailService.class, PdfGenerator.class, EmailConfig.class, JavaMailSender.class })
@TestPropertySource(locations = "classpath:application-dev.properties")
public class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    private final User USER = new User(
            1L, "testuser1", "", "ticketingwinners@gmail.com", "12312312", "SG", new Date(), null);

    private final ConcertSession CONCERT_SESSION = new ConcertSession(
            1L, LocalDateTime.now(), new Concert(
                    1L, "testconcert1", "testdescription1", "artist",
                    new Venue(1L, "Singapore National Stadium", new ArrayList<>(),
                            new ArrayList<>()),
                    new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
            new ArrayList<>());

    private final Ticket[] TICKETS = new Ticket[] {
            new Ticket(new Seat(1L, "A2", 12,
                    new Section(1L, "Section 1", new Category(), new ArrayList<>())),
                    CONCERT_SESSION)
    };

    private final Receipt INVOICE = new Receipt(
            UUID.randomUUID(),
            USER,
            new BigDecimal(100.0),
            Arrays.stream(TICKETS).toList());

    @Test
    public void sendPurchaseConfirmationMessageWithTicket_Valid() throws MessagingException {
        emailService.sendPurchaseConfirmationWithTicketEmail(USER, TICKETS, INVOICE, CONCERT_SESSION);
    }

    @Test
    public void sendBallotingSuccessMessage_Valid() {
        emailService.sendBallotingSuccessEmail(USER, CONCERT_SESSION, "http://somerandomurl.com");
    }

    @Test
    public void sendBallotingFailedMessage_Valid() {
        emailService.sendBallotingFailedEmail(USER, CONCERT_SESSION);
    }

    @Test
    public void sendBallotingConfirmationMessage_Valid() {
        emailService.sendBallotingConfirmationEmail(USER, CONCERT_SESSION);
    }
}

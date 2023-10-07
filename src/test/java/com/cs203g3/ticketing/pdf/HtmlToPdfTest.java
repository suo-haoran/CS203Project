package com.cs203g3.ticketing.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.email.htmlToPdf.PdfGenerator;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.seat.Seat;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.Role;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.venue.Venue;
import com.lowagie.text.DocumentException;

@SpringBootTest
@ContextConfiguration(classes = { PdfGenerator.class, TemplateEngine.class })
@TestPropertySource(locations = "classpath:application-dev.properties")
public class HtmlToPdfTest {

    @Autowired
    private PdfGenerator generator;

    private TemplateEngine engine = new TemplateEngine();

    private final User USER = new User(
            1L, "testuser1", "", "exam@gmail.com", "12312312", "SG", new Date(), null);

    private final ConcertSession CONCERT_SESSION = new ConcertSession(
            1L, LocalDateTime.now(), new Concert(
                    1L, "testconcert1", "testdescription1", "artist",
                    new Venue(1L, "Singapore National Stadium", new ArrayList<>(),
                            new ArrayList<>()),
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

    // https://www.w3docs.com/snippets/html/how-to-display-base64-images-in-html.html
    @Test
    public void testGenerateTicket() throws DocumentException, IOException {
        Map<String, Object> data = Map.of(
                "user", USER,
                "ticket", TICKETS[0],
                "session", CONCERT_SESSION);

        String html = parseThymeleafTemplate("ticket-template", data);
        System.out.println(html);
        generatePdfFromHtml(html);
    }

    // @Test
    // public void test() throws DocumentException, IOException {
    // Map<String, Object> data = Map.of("name", "John Doe");
    // String html = parseThymeleafTemplate("ticket-template", data);
    // System.out.println(html);
    // generatePdfFromHtml(html);
    // }

    private String parseThymeleafTemplate(String templateName, Map<String, Object> data) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("pdf-templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        engine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariables(data);

        return engine.process(templateName, context);
    }

    public void generatePdfFromHtml(String html) throws DocumentException, IOException {
        String outputFolder = "/tmp/thymeleaf.pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }
}

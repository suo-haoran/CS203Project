package com.cs203g3.ticketing.email.attachments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;

@Service
public class EmailAttachmentService {
    private TemplateEngine engine;

    private static final String ATTACHMENTS_DIR = "./email-attachments/";

    static {
        File file = new File(ATTACHMENTS_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public EmailAttachmentService(TemplateEngine templateEngine) {
        this.engine = templateEngine;
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        engine.setTemplateResolver(templateResolver);
    }


    public String generateReceipt(User user, Ticket[] tickets, ConcertSession concertSession, Receipt receipt) throws IOException {
        String path = ATTACHMENTS_DIR + "/invoice-" + tickets[0].getId() + ".html";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("tickets", tickets);
        variables.put("receipt", receipt);
        variables.put("createdDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm")));
        variables.put("concertName", concertSession.getConcert().getTitle());
        variables.put("perTicketCost", receipt.getAmountPaid().doubleValue() / tickets.length);

        String invoiceTemplate = parseThymeleafTemplate("receipt-template", variables);
        writeToHtml(invoiceTemplate, path);
        return path;
    }

    public String generateTickets(User user, Ticket[] tickets, ConcertSession concertSession) throws IOException {
        String path = ATTACHMENTS_DIR + "/ticket-" + tickets[0].getId() + ".html";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("tickets", tickets);
        variables.put("dayOfWeek", DayOfWeek.from(concertSession.getDatetime()));
        variables.put("date", concertSession.getDatetime().format(DateTimeFormatter.ofPattern("dd MMMM")));
        variables.put("year", concertSession.getDatetime().format(DateTimeFormatter.ofPattern("yyyy")));
        variables.put("time", concertSession.getDatetime().format(DateTimeFormatter.ofPattern("HH:mm")));
        variables.put("location", concertSession.getConcert().getVenue().getName());
        variables.put("concertName", concertSession.getConcert().getTitle());
        variables.put("artist", concertSession.getConcert().getArtist());

        String ticketTemplate = parseThymeleafTemplate("ticket-template", variables);
        writeToHtml(ticketTemplate, path);
        return path;
    }

    private boolean writeToHtml(String htmlContent, String path) throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(htmlContent.getBytes());
            fileOutputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String parseThymeleafTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return engine.process(templateName, context);
    }
}

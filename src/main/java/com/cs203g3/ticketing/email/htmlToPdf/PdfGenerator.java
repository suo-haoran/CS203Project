package com.cs203g3.ticketing.email.htmlToPdf;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;
import com.lowagie.text.DocumentException;


@Component
public class PdfGenerator {

    private TemplateEngine templateEngine;

    @Value("${pdf.directory}")
    private String pdfDirectory;

    public PdfGenerator() {
        this.templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("pdf-templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
    }

    public String[] generateTicketsAndReceipt(User user, Ticket[] tickets, Receipt receipt, ConcertSession session) {
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("ticket", tickets[0]);
        variables.put("session", session);

        String ticketTemplate = parseThymeleafTemplate("ticket-template", variables);
        String ticketPdfPath = pdfDirectory + "ticket.pdf";
        writeHtmlToPdf(ticketTemplate, ticketPdfPath);

        variables.clear();
        variables.put("to", "SHR");
        // variables.put("user", user);
        // variables.put("receipt", receipt);
        // variables.put("concert", concert);

        String receiptTemplate = parseThymeleafTemplate("receipt-template", variables);
        String receiptPdfPath = pdfDirectory + "receipt.pdf";
        writeHtmlToPdf(receiptTemplate, receiptPdfPath);
        return new String[] { ticketPdfPath, receiptPdfPath };
    }

    private String parseThymeleafTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(templateName, context);
    }

    private boolean writeHtmlToPdf(String htmlContent, String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ITextRenderer renderer = new ITextRenderer();
            System.out.println(htmlContent);
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(fileOutputStream, false);
            renderer.finishPDF();
            return true;
        } catch (FileNotFoundException | DocumentException e) {
            throw new PdfException("PDF generation failed");
        }
    }

}

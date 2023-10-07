package com.cs203g3.ticketing.email;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.email.htmlToPdf.PdfGenerator;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {
    private JavaMailSender mailSender;
    private PdfGenerator pdfGenerator;

    public EmailService(JavaMailSender mailSender, PdfGenerator pdfGenerator) {
        this.mailSender = mailSender;
        this.pdfGenerator = pdfGenerator;
    }

    public void sendPurchaseConfirmationMessageWithTicket(
            User user, Ticket[] tickets, Receipt receipt, ConcertSession session) {
        String to = user.getEmail();
        String subject = "Purchase Confirmation";
        String text = "Dear " + user.getUsername() + ",\n\n"
                + "Thank you for purchasing the ticket(s) for the event: " + session.getConcert().getTitle() + ".\n\n"
                + "Please find the attached ticket and receipt for your purchase.\n\n"
                + "Regards,\n"
                + "Ticketing Winners";
        String[] pathsToPdfs = pdfGenerator.generateTicketsAndReceipt(user, tickets, receipt, session);
        sendEmailWithAttachments(to, subject, text, pathsToPdfs);
    }

    private void sendEmailWithAttachments(
            String to, String subject, String text, String[] pathsToAttachment) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("suohaoran99@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            FileSystemResource ticket = new FileSystemResource(new File(pathsToAttachment[0]));
            helper.addAttachment("Ticket", ticket);
            FileSystemResource receipt = new FileSystemResource(new File(pathsToAttachment[1]));
            helper.addAttachment("Receipt", receipt);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } 
    }
}

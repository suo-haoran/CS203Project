package com.cs203g3.ticketing.email;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.email.attachments.EmailAttachmentService;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailService {
    private JavaMailSender mailSender;
    private EmailAttachmentService attachmentService;

    @Value("${spring.mail.username}")
    private String mailServerUsername;

    public EmailService(JavaMailSender mailSender, EmailAttachmentService attachmentService) {
        this.mailSender = mailSender;
        this.attachmentService = attachmentService;
    }

    public void sendBallotingConfirmationEmail(User user, Concert concert) {
        String message = String.format(EmailTemplate.BALLOT_JOIN, user.getUsername(),
                concert.getTitle());
        sendSimpleEmail(user.getEmail(), EmailTemplate.BALLOT_JOIN_TITLE, message);
    }

    public void sendBallotingFailedEmail(User user, Concert concert) {
        String message = String.format(EmailTemplate.BALLOT_FAILED, user.getUsername(),
                concert.getTitle());
        sendSimpleEmail(user.getEmail(), EmailTemplate.BALLOT_FAILED_TITLE, message);
    }

    public void sendBallotingSuccessEmail(User user, Concert concert, String url) {
        String text = String.format(EmailTemplate.BALLOT_SUCCESS, user.getUsername(),
                concert.getTitle(), url);
        sendSimpleEmail(user.getEmail(), EmailTemplate.BALLOT_SUCCESS_TITLE, text);
    }

    public void sendPurchaseConfirmationWithTicketEmail(
            User user, Ticket[] tickets, Receipt receipt, ConcertSession session) throws IOException {
        String to = user.getEmail();
        String subject = EmailTemplate.PURCHASE_CONFIRMATION_TITLE;
        String text = String.format(EmailTemplate.PURCHASE_CONFIRMATION, user.getUsername(),
                session.getConcert().getTitle());

        String ticketPath = attachmentService.generateTickets(user, tickets, session);
        String receiptPath = attachmentService.generateReceipt(user, tickets, session, receipt);
        sendEmailWithAttachments(to, subject, text, ticketPath, receiptPath);
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailServerUsername);
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        mailSender.send(message);
    }

    private void sendEmailWithAttachments(
            String to, String subject, String text, String ticketPath, String receiptPath) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailServerUsername);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);
            FileSystemResource ticket = new FileSystemResource(new File(ticketPath));
            helper.addAttachment("Ticket.html", ticket);
            FileSystemResource receipt = new FileSystemResource(new File(receiptPath));
            helper.addAttachment("Receipt.html", receipt);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailException("Failed to send email");
        }

    }
}

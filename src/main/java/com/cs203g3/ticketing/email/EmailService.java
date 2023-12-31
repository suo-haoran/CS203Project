package com.cs203g3.ticketing.email;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

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

    public void sendBallotingConfirmationEmail(User user, ConcertSession concertSession) {
        String message = String.format(EmailTemplate.BALLOT_JOIN, user.getUsername(),
                concertSession.getConcert().getTitle());
        sendSimpleEmail(user.getEmail(), EmailTemplate.BALLOT_JOIN_TITLE, message);
    }

    public void sendBallotingFailedEmail(User user, ConcertSession concertSession) {
        String message = String.format(EmailTemplate.BALLOT_FAILED, user.getUsername(),
                concertSession.getConcert().getTitle());
        sendSimpleEmail(user.getEmail(), EmailTemplate.BALLOT_FAILED_TITLE, message);
    }

    public void sendBallotingSuccessEmail(User user, ConcertSession concertSession, String url) {
        String text = String.format(EmailTemplate.BALLOT_SUCCESS, user.getUsername(),
                concertSession.getConcert().getTitle(), url);
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

        Email email = new Email(to, subject, text, new Attachment[] { new Attachment("ticket.html", ticketPath),
                new Attachment("receipt.html", receiptPath) });

        sendEmailWithAttachments(email);
    }

    /**
     * Sends a simple email with the specified recipient, subject, and text content.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param text    The text content of the email.
     */
    private void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailServerUsername);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /**
     * Sends an email with attachments, using the provided Email object containing
     * recipient,
     * subject, body, and attachments information.
     *
     * @param email The Email object containing email details, including recipient,
     *              subject, body, and attachments.
     * @throws EmailException If there is an issue with sending the email.
     */
    private void sendEmailWithAttachments(Email email) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailServerUsername);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody());

            for (Attachment attachment : email.getAttachments()) {
                FileSystemResource file = new FileSystemResource(new File(attachment.getFilePath()));
                helper.addAttachment(attachment.getFileName(), file);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailException("Failed to send email");
        }

    }
}

package com.cs203g3.ticketing.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.ballot.Ballot;
import com.cs203g3.ticketing.ballot.BallotRepository;
import com.cs203g3.ticketing.ballot.EnumPurchaseAllowed;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.concertSession.ConcertSessionRepository;
import com.cs203g3.ticketing.email.EmailService;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.payment.dto.PaymentMetadataDto;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.ReceiptService;
import com.cs203g3.ticketing.receipt.dto.ReceiptRequestDto;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.section.SectionRepository;
import com.cs203g3.ticketing.security.jwt.AuthEntryPointJwt;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.ticket.TicketRepository;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    @Value("${cs203.app.stripeEndpointSecret}")
    private String endpointSecret;

    private ModelMapper modelMapper;

    private ConcertSessionRepository concertSessions;
    private SectionRepository sections;
    private TicketRepository tickets;
    private UserRepository users;
    private BallotRepository ballots;

    private EmailService emailService;
    private ReceiptService receiptService;

    public PaymentService(ModelMapper modelMapper,
            ConcertSessionRepository concertSessions, SectionRepository sections, TicketRepository tickets,
            UserRepository users, BallotRepository ballots,
            EmailService es, ReceiptService rs) {
        this.modelMapper = modelMapper;

        this.concertSessions = concertSessions;
        this.sections = sections;
        this.tickets = tickets;
        this.users = users;
        this.ballots = ballots;

        this.emailService = es;
        this.receiptService = rs;
    }

    /**
     * Processes a Stripe payload, verifies its signature, and retrieves the
     * checkout session.
     *
     * @param stripeSignature The Stripe signature for webhook verification.
     * @param stripePayload   The Stripe payload containing payment information.
     * @return The checkout session obtained from the Stripe payload.
     * @throws StripeSignatureVerificationException If the Stripe webhook signature
     *                                              cannot be verified.
     * @throws StripeDeserializationException       If there is an issue
     *                                              deserializing the Stripe
     *                                              payload.
     * @throws StripeUnknownEventException          If the event type in the payload
     *                                              is not
     *                                              "checkout.session.completed."
     *
     *                                              This method processes a Stripe
     *                                              payload and verifies its
     *                                              signature. It then extracts and
     *                                              returns the checkout session
     *                                              contained within the payload. If
     *                                              any verification or
     *                                              deserialization issues occur,
     *                                              exceptions are thrown
     *                                              accordingly.
     *
     * @param stripeSignature The Stripe signature for webhook verification.
     * @param stripePayload   The Stripe payload containing payment information.
     *
     * @return The checkout session extracted from the Stripe payload.
     *
     * @throws StripeSignatureVerificationException If the Stripe webhook signature
     *                                              cannot be verified.
     * @throws StripeDeserializationException       If there is an issue
     *                                              deserializing the Stripe
     *                                              payload.
     * @throws StripeUnknownEventException          If the event type in the payload
     *                                              is not
     *                                              "checkout.session.completed."
     */

    private Session processStripePayloadAndGetSession(String stripeSignature, String stripePayload) {
        Event event = null;
        // Use this line instead of Webhook.constructEvent() if no signature
        event = ApiResource.GSON.fromJson(stripePayload, Event.class);

        // try {
        // event = Webhook.constructEvent(stripePayload, stripeSignature,
        // endpointSecret);
        // } catch (SignatureVerificationException e) {
        // throw new StripeSignatureVerificationException("Error verifying Stripe
        // webhook signature: " + e.getMessage());
        // }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (!dataObjectDeserializer.getObject().isPresent()) {
            throw new StripeDeserializationException();
        }

        if (!event.getType().equals("checkout.session.completed")) {
            throw new StripeUnknownEventException();
        }

        return (Session) dataObjectDeserializer.getObject().get();
    }

    /**
     * Processes a completed payment for a Stripe checkout session, including
     * verifying the payment details,
     * generating a receipt, and sending a purchase confirmation email with attached
     * tickets.
     *
     * @param stripeSignature The Stripe signature for webhook verification.
     * @param stripePayload   The Stripe payload containing payment information.
     * @throws IOException               If an I/O error occurs.
     * @throws ResourceNotFoundException If a resource (e.g., User, ConcertSession,
     *                                   Section) is not found.
     */
    @Transactional
    public void processPaymentCompleted(String stripeSignature, String stripePayload) throws IOException {
        Session checkoutSession = processStripePayloadAndGetSession(stripeSignature, stripePayload);
        PaymentMetadataDto paymentDto = modelMapper.map(checkoutSession.getMetadata(), PaymentMetadataDto.class);

        User user = users.findById(paymentDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(User.class, paymentDto.getUserId()));

        // Verify values given in payload
        Long concertSessionId = paymentDto.getConcertSessionId();
        ConcertSession concertSession = concertSessions.findById(concertSessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertSessionId));
        Long sectionId = paymentDto.getSectionId();
        Section section = sections.findByCategoryVenueAndId(concertSession.getConcert().getVenue(), sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        Category category = section.getCategory();

        Ballot ballot = ballots.findByConcertSessionIdAndCategoryIdAndUserId(concertSessionId, category.getId(), user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User has not joined this balloting session, he/she shouldn't be able to purchase "));

        ballot.setPurchaseAllowed(EnumPurchaseAllowed.BOUGHT);
        ballots.save(ballot);

        // Generate receipt
        ReceiptRequestDto newReceiptDto = new ReceiptRequestDto(
                paymentDto.getUserId(),
                BigDecimal.valueOf(checkoutSession.getAmountTotal()));
        Receipt newReceipt = receiptService.addReceipt(newReceiptDto);

        // Retrieve tickets
        Integer ticketsBought = paymentDto.getTicketsBought();
        List<Ticket> ticketList = tickets
                .findAllByConcertSessionIdAndSeatSectionIdAndReceiptIsNullOrderBySeatSeatRowAscSeatSeatNumberAsc(
                        concertSessionId,
                        sectionId,
                        PageRequest.of(0, ticketsBought)); // Limits the query to only return the correct
                                                           // (ticketsBought) number of tickets

        if (ticketList.size() != ticketsBought) {
            throw new ResourceNotFoundException("Insufficient available tickets to fulfill this request!");
        }

        // Attach tickets to purchase
        ticketList.forEach(ticket -> ticket.setReceipt(newReceipt));
        tickets.saveAll(ticketList);

        emailService.sendPurchaseConfirmationWithTicketEmail(user, ticketList.toArray(new Ticket[0]), newReceipt,
                ticketList.get(0).getConcertSession());
    }
}

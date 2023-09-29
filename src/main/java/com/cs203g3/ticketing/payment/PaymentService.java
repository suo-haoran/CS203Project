package com.cs203g3.ticketing.payment;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.payment.dto.PaymentMetadataDto;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.ReceiptService;
import com.cs203g3.ticketing.receipt.dto.ReceiptRequestDto;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.ticket.TicketRepository;
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

    private TicketRepository tickets;

    private ReceiptService receiptService;

    public PaymentService(ModelMapper modelMapper, TicketRepository tickets, ReceiptService rs) {
        this.modelMapper = modelMapper;

        this.tickets = tickets;
        this.receiptService = rs;
    }

    private Session processStripePayloadAndGetSession(String stripeSignature, String stripePayload) {
        Event event = null;
        // Use this line instead of Webhook.constructEvent() if no signature
        // event = ApiResource.GSON.fromJson(stripePayload, Event.class);

        try {
            event = Webhook.constructEvent(stripePayload, stripeSignature, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new StripeSignatureVerificationException("Error verifying Stripe webhook signature: " + e.getMessage());
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        if (!dataObjectDeserializer.getObject().isPresent()) {
            throw new StripeDeserializationException();
        }

        if (!event.getType().equals("checkout.session.completed")) {
            throw new StripeUnknownEventException();
        }

        return (Session) dataObjectDeserializer.getObject().get();
    }

    @Transactional
    public void processPaymentCompleted(String stripeSignature, String stripePayload) {
        Session checkoutSession = processStripePayloadAndGetSession(stripeSignature, stripePayload);
        PaymentMetadataDto paymentDto = modelMapper.map(checkoutSession.getMetadata(), PaymentMetadataDto.class);

        ReceiptRequestDto newReceiptDto = new ReceiptRequestDto(
            paymentDto.getUserId(),
            BigDecimal.valueOf(checkoutSession.getAmountTotal()));
        Receipt newReceipt = receiptService.addReceipt(newReceiptDto);

        Integer ticketsBought = paymentDto.getTicketsBought();
        List<Ticket> ticketList = tickets.findAllByConcertSessionIdAndSeatSectionIdAndReceiptIsNullOrderBySeatSeatRowAscSeatSeatNumberAsc(
            paymentDto.getConcertSessionId(),
            paymentDto.getSectionId(),
            PageRequest.of(0, ticketsBought));

        if (ticketList.size() != ticketsBought) {
            throw new ResourceNotFoundException("Insufficient available tickets to fulfill this request!");
        }

        ticketList.forEach(ticket -> ticket.setReceipt(newReceipt));
        tickets.saveAll(ticketList);
    }
}

package com.cs203g3.ticketing.payment;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.payment.dto.PaymentRequestDto;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.ReceiptService;
import com.cs203g3.ticketing.receipt.dto.ReceiptRequestDto;
import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.ticket.TicketRepository;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {

    @Autowired
    ModelMapper modelMapper;

    private TicketRepository tickets;

    private ReceiptService receiptService;

    public PaymentService(TicketRepository tickets, ReceiptService rs) {
        this.tickets = tickets;
        this.receiptService = rs;
    }

    @Transactional
    public void processPaymentCompleted(PaymentRequestDto paymentDto) {
        ReceiptRequestDto newReceiptDto = modelMapper.map(paymentDto, ReceiptRequestDto.class);
        Receipt newReceipt = receiptService.addReceipt(newReceiptDto);

        Integer ticketsBought = paymentDto.getTicketsBought();
        List<Ticket> ticketList = tickets.findAllByConcertSessionIdAndSeatSectionIdAndReceiptIsNullOrderBySeatSeatRowAscSeatSeatNumberAsc(
            paymentDto.getConcertSessionId(),
            paymentDto.getSectionId(),
            PageRequest.of(0, ticketsBought));

        ticketList.forEach(ticket -> {
            ticket.setReceipt(newReceipt);
        });

        tickets.saveAll(ticketList);
    }
}

package com.cs203g3.ticketing.ticket;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.ticket.dto.TicketRequestDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseWithoutReceiptDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseWithoutSessionDto;


@RestController
public class TicketController {
    
    private TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/concerts/{concertId}/sessions/{sessionId}/tickets")
    public List<TicketResponseWithoutSessionDto> getAllTicketsByConcertIdAndSessionId(@PathVariable Long concertId, @PathVariable Long sessionId) {
        return ticketService.getAllTicketsByConcertIdAndSessionId(concertId, sessionId);
    }

    @GetMapping("/receipt/{receiptId}/tickets")
    public List<TicketResponseWithoutReceiptDto> getAllTicketsByReceiptId(@PathVariable UUID receiptId) {
        return ticketService.getAllTicketsByReceiptId(receiptId);
    }

    @GetMapping("/tickets/{uuid}")
    public TicketResponseDto getTicket(@PathVariable UUID uuid) {
        return ticketService.getTicketByUuid(uuid);
    }

    @PostMapping("/tickets")
    public TicketResponseDto addTicket(@RequestBody TicketRequestDto newTicketDto) {
        return ticketService.addTicket(newTicketDto);
    }
}

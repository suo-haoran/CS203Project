package com.cs203g3.ticketing.ticket.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class TicketRequestDto {
    private Long seatId;
    private Long concertSessionId;
    private UUID receiptId;
}

package com.cs203g3.ticketing.ticket.dto;

import java.util.UUID;

import com.cs203g3.ticketing.receipt.dto.ReceiptResponseDto;
import com.cs203g3.ticketing.seat.Seat;

import lombok.Data;

@Data
public class TicketResponseWithoutSessionDto {
    private UUID id;
    private Seat seat;
    private ReceiptResponseDto receipt;
}

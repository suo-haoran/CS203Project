package com.cs203g3.ticketing.receipt.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.cs203g3.ticketing.ticket.Ticket;

import lombok.Data;

@Data
public class ReceiptResponseDto {
    private UUID uuid;
    private String username;
    private BigDecimal amountPaid;
    private List<Ticket> tickets;
}

package com.cs203g3.ticketing.receipt.dto;

import java.math.BigDecimal;
import java.util.UUID;


import lombok.Data;

@Data
public class ReceiptResponseDto {
    private UUID uuid;
    private String username;
    private BigDecimal amountPaid;
}

package com.cs203g3.ticketing.receipt.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceiptRequestDto {
    private Long userId;
    private BigDecimal amountPaid;
}

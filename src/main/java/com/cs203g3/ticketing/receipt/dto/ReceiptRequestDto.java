package com.cs203g3.ticketing.receipt.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptRequestDto {
    private Long userId;
    private BigDecimal amountPaid;
}

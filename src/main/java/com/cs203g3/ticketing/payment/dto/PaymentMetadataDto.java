package com.cs203g3.ticketing.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentMetadataDto {

    @NotNull(message="userId cannot be null")
    private Long userId; // Here, is it possible for Stripe/ourselves to verify this ?

    @NotNull(message="concertSessionId cannot be null")
    private Long concertSessionId;

    @NotNull(message="sectionId cannot be null")
    private Long sectionId;

    @NotNull(message="ticketsBought cannot be null")
    @Min(value=1, message="ticketsBought must be 1 or more")
    @Max(value=5, message="ticketsBought must not be more than 5")
    private Integer ticketsBought;

    // @NotNull(message="amountPaid cannot be null")
    // @DecimalMin(value="0.0", inclusive=true, message="amountPaid must be greater than 0")
    // @Digits(integer=6, fraction=2, message="Invalid price value (<6 digits>.<2 digits> expected)")
    // private BigDecimal amountPaid;
}

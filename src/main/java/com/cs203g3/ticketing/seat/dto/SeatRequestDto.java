package com.cs203g3.ticketing.seat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SeatRequestDto {
    @NotNull(message="seatRow must not be null")
    @Size(min=1, max=5, message="Length of seatRow must be between 1 and 5")
    private String seatRow;

    @NotNull(message="seatNumber must not be null")
    private Integer seatNumber;
}

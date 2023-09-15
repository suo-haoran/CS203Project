package com.cs203g3.ticketing.seat.dto;

import lombok.Data;

@Data
public class SeatRequestDto {
    private String seatRow;
    private int seatNumber;
}

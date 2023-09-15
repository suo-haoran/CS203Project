package com.cs203g3.ticketing.seat.dto;

import lombok.Data;

@Data
public class SeatResponseDto {
    private Long id;
    private String seatRow;
    private int seatNumber;
}

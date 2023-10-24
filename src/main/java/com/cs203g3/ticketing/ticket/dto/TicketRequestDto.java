package com.cs203g3.ticketing.ticket.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class TicketRequestDto {
    @NotNull(message="seatId must not be null")
    private Long seatId;

    @NotNull(message="concertSessionId must not be null")
    private Long concertSessionId;
}

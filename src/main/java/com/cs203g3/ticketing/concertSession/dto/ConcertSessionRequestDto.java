package com.cs203g3.ticketing.concertSession.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConcertSessionRequestDto {
    @NotNull(message="Datetime must not be null")
    private LocalDateTime datetime;
}

package com.cs203g3.ticketing.concertSession.dto;

import java.time.LocalDateTime;

import com.cs203g3.ticketing.concert.dto.ConcertIdAndTitleOnlyResponseDto;

import lombok.Data;

@Data
public class ConcertSessionResponseDto {
    private Long id;
    private ConcertIdAndTitleOnlyResponseDto concert;
    private LocalDateTime datetime;
}

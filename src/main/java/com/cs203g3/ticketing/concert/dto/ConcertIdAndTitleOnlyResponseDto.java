package com.cs203g3.ticketing.concert.dto;

import com.cs203g3.ticketing.venue.Venue;

import lombok.Data;

@Data
public class ConcertIdAndTitleOnlyResponseDto {
    private Long id;
    private String title;
}

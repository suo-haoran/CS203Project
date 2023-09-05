package com.cs203g3.ticketing.concert.dto;

import lombok.Data;

@Data
public class ConcertRequestDto {
    private String title;
    private String description;
    private String artist;
    private Long venueId;
}

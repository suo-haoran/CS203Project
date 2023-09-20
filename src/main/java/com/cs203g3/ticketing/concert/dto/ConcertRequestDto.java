package com.cs203g3.ticketing.concert.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConcertRequestDto {

    @NotNull(message="Title must not be null")
    @Size(min=1, max=255, message="Length of title must be between 1 and 255")
    private String title;

    @NotNull(message="Description must not be null")
    @Size(min=1, message="Description must not be blank")
    private String description;

    @NotNull(message="Artist must not be null")
    @Size(min=1, max=255, message="Length of artist must be between 1 and 255")
    private String artist;

    @NotNull(message="VenueId must not be null")
    private Long venueId;
}

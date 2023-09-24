package com.cs203g3.ticketing.concert.dto;


import java.util.List;

import com.cs203g3.ticketing.concertImage.ConcertImage;
import com.cs203g3.ticketing.concertSession.dto.ConcertSessionRequestDto;
import com.cs203g3.ticketing.venue.Venue;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConcertResponseDto {

    @NotNull(message="Id must not be null")
    private Long id;

    @NotNull(message="Title must not be null")
    @Size(min=1, max=255, message="Length of title must be between 1 and 255")
    private String title;

    @NotNull(message="Description must not be null")
    @Size(min=1, message="Description must not be blank")
    private String description;

    @NotNull(message="Artist must not be null")
    @Size(min=1, max=255, message="Length of artist must be between 1 and 255")
    private String artist;

    private List<ConcertImage> concertImages;

    private Venue venue;

    private ConcertSessionRequestDto earliestSession;

    private ConcertSessionRequestDto latestSession;
}

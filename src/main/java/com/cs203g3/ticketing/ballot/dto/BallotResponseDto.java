package com.cs203g3.ticketing.ballot.dto;

import com.cs203g3.ticketing.category.dto.CategoryIdAndNameOnlyResponseDto;
import com.cs203g3.ticketing.concertSession.dto.ConcertSessionResponseDto;
import com.cs203g3.ticketing.user.dto.UserIdAndNameOnlyResponseDto;

import lombok.Data;

@Data
public class BallotResponseDto {
    private Long id;

    private UserIdAndNameOnlyResponseDto user;

    private ConcertSessionResponseDto concertSession;

    private CategoryIdAndNameOnlyResponseDto category;
}

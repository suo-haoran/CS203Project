package com.cs203g3.ticketing.ballot.dto;

import com.cs203g3.ticketing.category.dto.CategoryIdAndNameOnlyResponseDto;
import com.cs203g3.ticketing.concert.dto.ConcertIdAndTitleOnlyResponseDto;
import com.cs203g3.ticketing.user.dto.UserIdAndNameOnlyResponseDto;

import lombok.Data;

@Data
public class BallotResponseDto {
    private Long id;

    private UserIdAndNameOnlyResponseDto user;

    private ConcertIdAndTitleOnlyResponseDto concert;

    private CategoryIdAndNameOnlyResponseDto category;
}

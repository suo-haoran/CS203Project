package com.cs203g3.ticketing.activeBallotCategory.dto;

import com.cs203g3.ticketing.activeBallotCategory.EnumActiveBallotCategoryStatus;
import com.cs203g3.ticketing.category.dto.CategoryIdAndNameOnlyResponseDto;
import com.cs203g3.ticketing.concert.dto.ConcertIdAndTitleOnlyResponseDto;

import lombok.Data;

@Data
public class ActiveBallotCategoryWithTimerResponseDto {
    private ConcertIdAndTitleOnlyResponseDto concert;
    private CategoryIdAndNameOnlyResponseDto category;
    private EnumActiveBallotCategoryStatus status;
    private Long timeToNextStatus;
}

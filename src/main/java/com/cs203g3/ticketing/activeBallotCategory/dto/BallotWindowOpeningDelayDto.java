package com.cs203g3.ticketing.activeBallotCategory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BallotWindowOpeningDelayDto {

    @Min(value=1, message="secondsBeforeOpening must be 1 or more")
    @NotNull(message="secondsBeforeOpening cannot be null")
    private Integer secondsBeforeOpening;
}

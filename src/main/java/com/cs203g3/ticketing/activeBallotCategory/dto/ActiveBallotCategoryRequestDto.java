package com.cs203g3.ticketing.activeBallotCategory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActiveBallotCategoryRequestDto {

    @Min(value=1, message="secondsBeforeClosure must be 1 or more")
    @NotNull(message="secondsBeforeClosure cannot be null")
    private Integer secondsBeforeClosure;
}

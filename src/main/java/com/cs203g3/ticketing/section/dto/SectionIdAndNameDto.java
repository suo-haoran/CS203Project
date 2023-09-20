package com.cs203g3.ticketing.section.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SectionIdAndNameDto {
    @NotNull(message="Id must not be null")
    private Long id;

    @NotNull(message="Name must not be null")
    @Size(min=1, max=255, message="Length of name must be between 1 and 255")
    private String name;
}

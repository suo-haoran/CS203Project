package com.cs203g3.ticketing.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class SignupResponseDto {
    @NotNull(message="Id must not be null")
    private Long id;

    @NotNull(message="username must not be null")
    private String username;

    @NotNull(message="email must not be null")
    private String email;

    @NotNull(message="phone must not be null")
    private String phone;

    @NotNull(message="countryOfResidences must not be null")
    private String countryOfResidences;

    @NotNull(message="dob must not be null")
    private String dob;
}

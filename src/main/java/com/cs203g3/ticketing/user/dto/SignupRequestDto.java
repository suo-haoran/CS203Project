package com.cs203g3.ticketing.user.dto;

import java.util.Date;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequestDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    @Size(min = 8, max = 11)
    private String phone;

    @NotBlank
    @Size(min = 4, max=45)
    private String countryOfResidences;

    private Date dob;
}
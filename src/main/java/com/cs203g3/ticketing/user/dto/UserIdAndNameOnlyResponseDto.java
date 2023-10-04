package com.cs203g3.ticketing.user.dto;

import lombok.Data;

@Data
public class UserIdAndNameOnlyResponseDto {
    private Long id;
    private String username;
}

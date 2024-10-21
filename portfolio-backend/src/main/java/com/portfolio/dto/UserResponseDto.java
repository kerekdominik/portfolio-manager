package com.portfolio.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
}

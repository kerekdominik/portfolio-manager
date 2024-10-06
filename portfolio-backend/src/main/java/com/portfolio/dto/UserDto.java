package com.portfolio.dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
}

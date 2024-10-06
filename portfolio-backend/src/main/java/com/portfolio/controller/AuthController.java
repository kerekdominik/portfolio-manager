package com.portfolio.controller;

import com.portfolio.dto.AuthenticationResponseDto;
import com.portfolio.dto.LoginRequestDto;
import com.portfolio.dto.UserDto;
import com.portfolio.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "API for authentication")
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user and returns an authentication token.")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(service.register(userDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs in a user and returns an authentication token.")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(service.login(loginRequestDto));
    }
}

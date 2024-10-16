package com.portfolio.controller;

import com.portfolio.dto.AuthenticationResponseDto;
import com.portfolio.dto.LoginRequestDto;
import com.portfolio.dto.RegisterRequestDto;
import com.portfolio.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "API for authentication")
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Registers a new user and returns an authentication token.")
    public ResponseEntity<AuthenticationResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(service.register(registerRequestDto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs in a user and returns an authentication token.")
    public ResponseEntity<AuthenticationResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(service.login(loginRequestDto));
    }

    @GetMapping("/oauth2/authorize")
    @Operation(summary = "Initiate Google OAuth2 login", description = "Redirects user to Google OAuth2 login page.")
    public void redirectToGoogleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/oauth2/success")
    @Operation(summary = "Handle successful OAuth2 authentication", description = "Handles successful OAuth2 authentication and returns JWT token.")
    public void oauth2Success(HttpServletResponse response, @AuthenticationPrincipal OAuth2User principal) throws IOException {
        String email = principal.getAttribute("email");
        String firstName = principal.getAttribute("given_name");
        String lastName = principal.getAttribute("family_name");

        AuthenticationResponseDto authResponse = service.authenticateWithOAuth2(email, firstName, lastName);

        String redirectUrl = "http://localhost:4200/oauth2/redirect?token=" + authResponse.getToken();
        response.sendRedirect(redirectUrl);
    }
}

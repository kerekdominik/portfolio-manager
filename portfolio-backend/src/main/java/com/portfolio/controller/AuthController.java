package com.portfolio.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "AuthController", description = "API for authentication")
public class AuthController {

    @GetMapping("/")
    @Operation(summary = "Welcome message", description = "Returns a welcome message")
    public String home() {
        return "Hello, World!";
    }

    @GetMapping("/secured")
    public String secured() {
        return "Secured";
    }
}

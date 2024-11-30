package com.portfolio.controller;

import com.portfolio.dto.UserResponseDto;
import com.portfolio.entity.User;
import com.portfolio.mapper.UserMapper;
import com.portfolio.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "API for user-related operations")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user details",
            description = "Retrieves the details of the currently authenticated user."
    )
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        String email = ((User) authentication.getPrincipal()).getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponseDto userDto = userMapper.userToUserResponseDto(user);
        return ResponseEntity.ok(userDto);
    }
}

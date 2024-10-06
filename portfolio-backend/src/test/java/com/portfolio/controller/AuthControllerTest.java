package com.portfolio.controller;

import com.portfolio.dto.AuthenticationResponseDto;
import com.portfolio.dto.LoginRequestDto;
import com.portfolio.dto.UserDto;
import com.portfolio.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testRegister() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUsername("john_doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("SecurePassword123!");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setRole("USER");

        AuthenticationResponseDto authResponse = new AuthenticationResponseDto();
        authResponse.setToken("mocked-jwt-token");

        when(authService.register(any(UserDto.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthenticationResponseDto> response = authController.register(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("mocked-jwt-token", response.getBody().getToken());
    }

    @Test
    void testLogin() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("john_doe");
        loginRequestDto.setPassword("SecurePassword123!");

        AuthenticationResponseDto authResponse = new AuthenticationResponseDto();
        authResponse.setToken("mocked-jwt-token");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthenticationResponseDto> response = authController.login(loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("mocked-jwt-token", response.getBody().getToken());
    }
}

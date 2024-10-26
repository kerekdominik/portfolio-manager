package com.portfolio.controller;

import com.portfolio.dto.auth.AuthenticationResponseDto;
import com.portfolio.dto.auth.LoginRequestDto;
import com.portfolio.dto.auth.RegisterRequestDto;
import com.portfolio.service.impl.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private HttpServletResponse response;

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername("john_doe");
        registerRequestDto.setEmail("john.doe@example.com");
        registerRequestDto.setPassword("SecurePassword123!");
        registerRequestDto.setFirstName("John");
        registerRequestDto.setLastName("Doe");
        registerRequestDto.setRole("USER");

        AuthenticationResponseDto authResponse = AuthenticationResponseDto.builder()
                .token("mocked-jwt-token")
                .build();

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthenticationResponseDto> responseEntity = authController.register(registerRequestDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("mocked-jwt-token", responseEntity.getBody().getToken());

        verify(authService).register(registerRequestDto);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("john.doe@example.com");
        loginRequestDto.setPassword("SecurePassword123!");

        AuthenticationResponseDto authResponse = AuthenticationResponseDto.builder()
                .token("mocked-jwt-token")
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthenticationResponseDto> responseEntity = authController.login(loginRequestDto);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("mocked-jwt-token", responseEntity.getBody().getToken());

        verify(authService).login(loginRequestDto);
    }

    @Test
    void testRedirectToGoogleAuth() throws IOException {
        // Act
        authController.redirectToGoogleAuth(response);

        // Assert
        verify(response).sendRedirect("/oauth2/authorization/google");
    }
}

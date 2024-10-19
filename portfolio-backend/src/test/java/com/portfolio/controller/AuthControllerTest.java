package com.portfolio.controller;

import com.portfolio.dto.AuthenticationResponseDto;
import com.portfolio.dto.LoginRequestDto;
import com.portfolio.dto.RegisterRequestDto;
import com.portfolio.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

    @Mock
    private OAuth2User principal;

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

    @Test
    void testOauth2Success() throws IOException {
        // Arrange
        when(principal.getAttribute("email")).thenReturn("john.doe@example.com");
        when(principal.getAttribute("given_name")).thenReturn("John");
        when(principal.getAttribute("family_name")).thenReturn("Doe");

        AuthenticationResponseDto authResponse = AuthenticationResponseDto.builder()
                .token("mocked-jwt-token")
                .build();

        when(authService.authenticateWithOAuth2("john.doe@example.com", "John", "Doe")).thenReturn(authResponse);

        ArgumentCaptor<String> redirectUrlCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        authController.oauth2Success(response, principal);

        // Assert
        verify(response).sendRedirect(redirectUrlCaptor.capture());
        String redirectUrl = redirectUrlCaptor.getValue();
        assertEquals("http://localhost:4200/oauth2/redirect?token=mocked-jwt-token", redirectUrl);

        verify(authService).authenticateWithOAuth2("john.doe@example.com", "John", "Doe");
    }
}

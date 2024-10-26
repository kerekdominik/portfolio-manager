package com.portfolio.configuration.security;

import com.portfolio.dto.auth.AuthenticationResponseDto;
import com.portfolio.service.impl.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private OAuth2Service oAuth2AuthService;

    @InjectMocks
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @Test
    void testOnAuthenticationSuccess() throws IOException {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("john.doe@example.com");
        when(oAuth2User.getAttribute("given_name")).thenReturn("John");
        when(oAuth2User.getAttribute("family_name")).thenReturn("Doe");

        AuthenticationResponseDto authResponse = AuthenticationResponseDto.builder()
                .token("mocked-jwt-token")
                .build();

        when(oAuth2AuthService.authenticateWithOAuth2("john.doe@example.com", "John", "Doe")).thenReturn(authResponse);

        // Act
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // Assert
        String expectedRedirectUrl = "http://localhost:4200/oauth2/redirect?token=mocked-jwt-token";
        verify(response).sendRedirect(expectedRedirectUrl);

        verify(oAuth2AuthService).authenticateWithOAuth2("john.doe@example.com", "John", "Doe");
    }
}

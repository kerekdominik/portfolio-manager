package com.portfolio.configuration.security;

import com.portfolio.dto.auth.AuthenticationResponseDto;
import com.portfolio.service.impl.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2Service oAuth2AuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        AuthenticationResponseDto authResponse = oAuth2AuthService.authenticateWithOAuth2(email, firstName, lastName);

        String redirectUrl = "http://localhost:4200/oauth2/redirect?token=" + authResponse.getToken();
        response.sendRedirect(redirectUrl);
    }
}

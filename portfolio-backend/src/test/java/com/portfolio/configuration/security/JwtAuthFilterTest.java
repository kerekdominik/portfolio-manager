package com.portfolio.configuration.security;

import com.portfolio.entity.User;
import com.portfolio.service.impl.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private User user;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest("GET", "/api/dashboard/pnl-summary");
        request.setServletPath("/api/dashboard/pnl-summary");
        response = new MockHttpServletResponse();
        user = User.builder().id(1L).email("test@example.com").build();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void excludedPath_skipsTokenHandling() throws Exception {
        request.setServletPath("/api/auth/login");
        request.addHeader("Authorization", "Bearer token");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void missingHeader_continuesUnauthenticated() throws Exception {
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService);
    }

    @Test
    void nonBearerHeader_continuesUnauthenticated() throws Exception {
        request.addHeader("Authorization", "Basic abc");

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void validToken_authenticatesUser() throws Exception {
        request.addHeader("Authorization", "Bearer good-token");
        when(jwtService.extractEmail("good-token")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(jwtService.isTokenValid("good-token", user)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidToken_leavesRequestUnauthenticated() throws Exception {
        request.addHeader("Authorization", "Bearer bad-token");
        when(jwtService.extractEmail("bad-token")).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(jwtService.isTokenValid("bad-token", user)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void alreadyAuthenticated_doesNotReloadUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, null));
        request.addHeader("Authorization", "Bearer good-token");
        when(jwtService.extractEmail("good-token")).thenReturn("test@example.com");

        filter.doFilter(request, response, filterChain);

        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }
}

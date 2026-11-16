package com.portfolio.configuration.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthEntryPointTest {

    @Test
    void commence_respondsWith401AndMessage() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new AuthEntryPoint().commence(new MockHttpServletRequest(), response,
                new InsufficientAuthenticationException("Full authentication is required"));

        assertEquals(401, response.getStatus());
        assertEquals("Full authentication is required", response.getErrorMessage());
    }
}

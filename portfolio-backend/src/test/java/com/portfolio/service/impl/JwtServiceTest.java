package com.portfolio.service.impl;

import com.portfolio.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Define a secret key for testing purposes (64 bytes for HS512)
    private final String secretKey = "MySuperSecretKeyForJwtTokenWhichIsVeryLongAndSecure12345MySuperSecretKeyForJwtTokenWhichIsVeryLongAndSecure12345";

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inject the secretKey into the jwtService instance using reflection
        Field secretKeyField = JwtService.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtService, secretKey);
    }

    @Test
    void testGenerateToken() {
        // Arrange
        UserDetails userDetails = createTestUser();

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token, "Token should not be null");
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        // Arrange
        UserDetails userDetails = createTestUser();
        Map<String, Object> extraClaims = Map.of("role", "ADMIN");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Extract the 'role' claim from the token
        String role = jwtService.extractClaim(token, claims -> (String) claims.get("role"));

        // Assert
        assertEquals("ADMIN", role, "Role claim should be 'ADMIN'");
    }

    @Test
    void testIsTokenValid_ValidToken() {
        // Arrange
        UserDetails userDetails = createTestUser();
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        // Arrange
        UserDetails userDetails = createTestUser();
        String invalidToken = "invalid.token.value";

        // Act
        boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);

        // Assert
        assertFalse(isValid, "Token should be invalid");
    }

    @Test
    void testIsTokenValid_ExpiredToken() {
        // Arrange
        UserDetails userDetails = createTestUser();

        // Generate a token that has already expired
        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // 24 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))     // 1 hour ago
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS512)
                .compact();

        // Act
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);

        // Assert
        assertFalse(isValid, "Expired token should be invalid");
    }

    @Test
    void testExtractEmail() {
        // Arrange
        User user = createTestUser();
        String token = jwtService.generateToken(user);

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals(user.getEmail(), extractedEmail, "Extracted email should match the user's email");
    }

    // Helper method to create a test user
    private User createTestUser() {
        User user = new User();
        user.setEmail("test@example.com");
        // Set other required fields if necessary
        return user;
    }
}

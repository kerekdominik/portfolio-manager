package com.portfolio.service.impl;

import com.portfolio.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private static final Duration TOKEN_LIFETIME = Duration.ofHours(24);

    @Value("${jwt.secret.key}")
    private String secretKey;

    private final Clock clock;

    public JwtService(Clock clock) {
        this.clock = clock;
    }

    /** Token generation **/
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Instant now = clock.instant();
        // jjwt only accepts java.util.Date for these claims, so convert at the library boundary
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(((User) userDetails).getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_LIFETIME)))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }


    /** Token validation **/
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email.equals(((User) userDetails).getEmail()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(clock.instant());
    }


    /** Token extraction **/
    private Instant extractExpiration(String token) {
        return extractClaim(token, claims -> claims.getExpiration().toInstant());
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

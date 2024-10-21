package com.portfolio.service;

import com.portfolio.dto.auth.AuthenticationResponseDto;
import com.portfolio.entity.User;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationResponseDto authenticateWithOAuth2(String email, String firstName, String lastName) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setUsername(firstName + lastName);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userRepository.save(user);
        }

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}

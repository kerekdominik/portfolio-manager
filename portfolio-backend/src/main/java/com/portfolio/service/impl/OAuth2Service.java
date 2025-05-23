package com.portfolio.service.impl;

import com.portfolio.dto.auth.AuthenticationResponseDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
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

        if (portfolioRepository.findByUser(user).isEmpty()) {
            Portfolio newPortfolio = new Portfolio();
            newPortfolio.setUser(user);
            portfolioRepository.save(newPortfolio);
        }

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}

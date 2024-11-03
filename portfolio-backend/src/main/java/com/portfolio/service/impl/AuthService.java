package com.portfolio.service.impl;

import com.portfolio.dto.auth.AuthenticationResponseDto;
import com.portfolio.dto.auth.LoginRequestDto;
import com.portfolio.dto.auth.RegisterRequestDto;
import com.portfolio.entity.Portfolio;
import com.portfolio.entity.User;
import com.portfolio.mapper.UserMapper;
import com.portfolio.repository.PortfolioRepository;
import com.portfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDto register(RegisterRequestDto registerRequestDto) {
        if (userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = userMapper.userDtoToUser(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        userRepository.save(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolioRepository.save(portfolio);

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponseDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}

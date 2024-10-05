package com.portfolio.service;

import com.portfolio.dto.AuthenticationResponseDto;
import com.portfolio.dto.LoginRequestDto;
import com.portfolio.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public AuthenticationResponseDto register(RegisterRequestDto registerRequestDto) {
        // TODO
        return AuthenticationResponseDto.builder().token("token").build();
    }

    public AuthenticationResponseDto login(LoginRequestDto loginRequestDto) {
        // TODO
        return AuthenticationResponseDto.builder().token("token").build();
    }
}

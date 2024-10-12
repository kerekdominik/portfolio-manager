package com.portfolio.service;

import com.portfolio.dto.AuthenticationResponseDto;
import com.portfolio.dto.LoginRequestDto;
import com.portfolio.dto.RegisterRequestDto;
import com.portfolio.entity.User;
import com.portfolio.mapper.UserMapper;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegister() {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername("testUser");
        registerRequestDto.setPassword("password123");
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setFirstName("John");
        registerRequestDto.setLastName("Doe");
        registerRequestDto.setRole("USER");

        // Create a spy for the User object
        User user = spy(new User());
        user.setUsername("testUser");

        // Mock behaviors
        when(userMapper.userDtoToUser(any(RegisterRequestDto.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDto response = authService.register(registerRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        // Verify interactions
        verify(userMapper).userDtoToUser(registerRequestDto);
        verify(passwordEncoder).encode("password123");
        verify(user).setPassword("encodedPassword"); // Now this works because 'user' is a spy
        verify(userRepository).save(user);
        verify(jwtService).generateToken(user);
    }

    @Test
    void testLogin() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testUser");
        loginRequestDto.setPassword("password123");

        User user = new User();
        user.setUsername("testUser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Since authenticate method returns void or Authentication
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDto response = authService.login(loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testUser");
        verify(jwtService).generateToken(user);
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("nonExistentUser");
        loginRequestDto.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequestDto));

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("nonExistentUser");
        verify(jwtService, never()).generateToken(any(User.class));
    }
}

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
    void testRegister_Successful() {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setUsername("testUser");
        registerRequestDto.setPassword("password123");
        registerRequestDto.setEmail("test@example.com");
        registerRequestDto.setFirstName("John");
        registerRequestDto.setLastName("Doe");
        registerRequestDto.setRole("USER");

        User user = spy(new User());
        user.setUsername("testUser");

        // Mock behaviors
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.userDtoToUser(registerRequestDto)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDto response = authService.register(registerRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        // Verify interactions
        verify(userRepository).findByEmail("test@example.com");
        verify(userMapper).userDtoToUser(registerRequestDto);
        verify(passwordEncoder).encode("password123");
        verify(user).setPassword("encodedPassword");
        verify(userRepository).save(user);
        verify(jwtService).generateToken(user);
    }

    @Test
    void testRegister_EmailAlreadyInUse() {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        registerRequestDto.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(registerRequestDto);
        });

        assertEquals("Email already in use", exception.getMessage());

        // Verify interactions
        verify(userRepository).findByEmail("test@example.com");
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository, jwtService);
    }

    @Test
    void testLogin_Successful() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null); // Az authenticate metódus void vagy Authentication típust ad vissza
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDto response = authService.login(loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("test@example.com");
        verify(jwtService).generateToken(user);
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("nonexistent@example.com");
        loginRequestDto.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(loginRequestDto));

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void testAuthenticateWithOAuth2_UserExists() {
        // Arrange
        String email = "test@example.com";
        String firstName = "John";
        String lastName = "Doe";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDto response = authService.authenticateWithOAuth2(email, firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(jwtService).generateToken(user);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticateWithOAuth2_UserDoesNotExist() {
        // Arrange
        String email = "newuser@example.com";
        String firstName = "Jane";
        String lastName = "Doe";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthenticationResponseDto response = authService.authenticateWithOAuth2(email, firstName, lastName);

        // Assert
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());

        User savedUser = userCaptor.getValue();
        assertEquals(firstName + lastName, savedUser.getUsername());
        assertEquals(email, savedUser.getEmail());
        assertEquals(firstName, savedUser.getFirstName());
        assertEquals(lastName, savedUser.getLastName());

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(savedUser);
        verify(jwtService).generateToken(savedUser);
    }
}

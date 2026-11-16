package com.portfolio.controller;

import com.portfolio.dto.UserResponseDto;
import com.portfolio.entity.User;
import com.portfolio.mapper.UserMapper;
import com.portfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController controller;

    private static Authentication authenticationFor(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, List.of());
    }

    @Test
    void getCurrentUser_returnsMappedUser() {
        User user = User.builder().id(1L).email("test@example.com").build();
        UserResponseDto dto = new UserResponseDto();
        dto.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(dto);

        assertEquals(dto, controller.getCurrentUser(authenticationFor(user)).getBody());
    }

    @Test
    void getCurrentUser_unknownUser_throws() {
        User user = User.builder().email("gone@example.com").build();
        when(userRepository.findByEmail("gone@example.com")).thenReturn(Optional.empty());
        Authentication authentication = authenticationFor(user);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.getCurrentUser(authentication));
        assertEquals("User not found", exception.getMessage());
    }
}

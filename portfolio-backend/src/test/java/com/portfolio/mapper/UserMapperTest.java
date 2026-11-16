package com.portfolio.mapper;

import com.portfolio.dto.UserResponseDto;
import com.portfolio.dto.auth.RegisterRequestDto;
import com.portfolio.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void userToUserResponseDto_copiesPublicFields() {
        User user = User.builder()
                .id(5L)
                .email("test@example.com")
                .username("tester")
                .firstName("Test")
                .lastName("User")
                .build();

        UserResponseDto dto = mapper.userToUserResponseDto(user);

        assertEquals(5L, dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("tester", dto.getUsername());
        assertEquals("Test", dto.getFirstName());
        assertEquals("User", dto.getLastName());
    }

    @Test
    void userDtoToUser_copiesFieldsWithoutPassword() {
        RegisterRequestDto request = new RegisterRequestDto();
        request.setEmail("new@example.com");
        request.setUsername("newbie");
        request.setFirstName("New");
        request.setLastName("User");

        User user = mapper.userDtoToUser(request);

        assertEquals("new@example.com", user.getEmail());
        assertEquals("newbie", user.getUsername());
        assertEquals("New", user.getFirstName());
        assertNull(user.getPassword());
    }

    @Test
    void nullInput_mapsToNull() {
        assertNull(mapper.userToUserResponseDto(null));
        assertNull(mapper.userDtoToUser(null));
    }
}

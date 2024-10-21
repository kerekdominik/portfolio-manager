package com.portfolio.mapper;

import com.portfolio.dto.UserResponseDto;
import com.portfolio.dto.auth.RegisterRequestDto;
import com.portfolio.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    User userDtoToUser(RegisterRequestDto registerRequestDto);

    UserResponseDto userToUserResponseDto(User user);
}
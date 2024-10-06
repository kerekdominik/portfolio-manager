package com.portfolio.mapper;

import com.portfolio.dto.UserDto;
import com.portfolio.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "password", target = "password", ignore = true)
    UserDto userToUserDto(User user);

    @Mapping(source = "password", target = "password", ignore = true)
    User userDtoToUser(UserDto userDto);

}
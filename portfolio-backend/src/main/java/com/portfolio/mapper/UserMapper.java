package com.portfolio.mapper;

import com.portfolio.dto.RegisterRequestDto;
import com.portfolio.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User userDtoToUser(RegisterRequestDto registerRequestDto);

}
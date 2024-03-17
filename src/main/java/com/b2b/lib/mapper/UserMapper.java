package com.b2b.lib.mapper;

import com.b2b.lib.dto.UserDto;
import com.b2b.lib.entity.UserLib;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserDto> toDto(List<UserLib> users);
}

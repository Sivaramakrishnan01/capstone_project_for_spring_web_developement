package com.spring.demo.mapper;

import com.spring.demo.dto.UserDto;
import com.spring.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
     User covertToUser(UserDto userDto);
     UserDto covertToUserDto(User user);


//     @Mapping(target = "id", ignore = true)
//     UserDto createUserDTOWithoutId(User user);

}

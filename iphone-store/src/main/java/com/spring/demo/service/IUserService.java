package com.spring.demo.service;

import com.spring.demo.dto.UserDto;

public interface IUserService {
    boolean userRegistration(UserDto userDto);
    boolean authenticate(String email, String password);

}

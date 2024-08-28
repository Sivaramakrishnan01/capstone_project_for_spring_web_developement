package com.spring.demo.service.impl;

import com.spring.demo.dto.UserDto;
import com.spring.demo.entity.User;
import com.spring.demo.mapper.Converter;
import com.spring.demo.repository.UserRepository;
import com.spring.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private  final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public boolean userRegistration(UserDto userDto) {
        User user=new User();
        Converter.copyProperty(userDto,user);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        var email = userRepository.findByEmail(userDto.getEmail());
        if(!email.isPresent()){
            userRepository.save(user);
            log.info("user registered successfully with email:{}",userDto.getEmail());
            return true;
        }
        else {
            log.warn("user already exist with given email:{}",userDto.getEmail());
            return false;
        }
    }

    @Override
    public boolean authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        try{
            Authentication result=authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(result);
            return true;
        }catch (Exception e){
            log.error("Authentication failed for given email {}",email);
            return false;
        }
    }
}

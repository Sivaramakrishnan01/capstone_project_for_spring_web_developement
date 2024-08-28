package com.spring.demo.controller;

import com.spring.demo.dto.UserDto;
import com.spring.demo.dto.request.LoginRequest;
import com.spring.demo.dto.response.SuccessResponse;
import com.spring.demo.exception.InvalidLoginException;
import com.spring.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> registration(@Valid @RequestBody UserDto userDto){
        log.info("Registering user with the email: {}",userDto.getEmail());
        if(userService.userRegistration(userDto)){
            return ResponseEntity.ok("User has created successfully");
        }
        else {
            log.warn("User already exists: {}", userDto.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpSession httpSession){
        log.info("Check user login credential email {}",loginRequest.getEmail());
        if(userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword())){
            SuccessResponse successResponse=new SuccessResponse(true, httpSession.getId());
            return ResponseEntity.ok(successResponse);
        }
        else {
            throw new InvalidLoginException("Invalid username and password");
        }



    }
}

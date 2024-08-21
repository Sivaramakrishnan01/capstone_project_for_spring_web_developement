package com.spring.demo.service;
import com.spring.demo.dto.UserDto;
import com.spring.demo.entity.User;
import com.spring.demo.mapper.UserMapper;
import com.spring.demo.repository.UserRepository;
import com.spring.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceTest {
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testAddUser_Success() {

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userMapper.covertToUser(userDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        boolean result = userService.userRegistration(userDto);

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).covertToUser(userDto);
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testAddUser_UserAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userMapper.covertToUser(userDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = userService.userRegistration(userDto);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, times(1)).covertToUser(userDto);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testAuthenticate_Success() {

        String email = "test@example.com";
        String password = "password";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("test@example.com", "password");
        Authentication authentication = mock(Authentication.class);

        when(authManager.authenticate(token)).thenReturn(authentication);

        boolean result = userService.authenticate(email, password);

        assertTrue(result);
        verify(authManager, times(1)).authenticate(token);
    }

    @Test
    void testAuthenticate_Failure() {

        String email = "test@example.com";
        String password = "wrongpassword";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        when(authManager.authenticate(token)).thenThrow(new RuntimeException("Authentication failed"));

        boolean result = userService.authenticate(email, password);
        assertFalse(result);
        verify(authManager, times(1)).authenticate(token);
    }
}

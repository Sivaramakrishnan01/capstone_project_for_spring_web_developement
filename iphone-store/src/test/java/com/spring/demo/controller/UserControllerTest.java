package com.spring.demo.controller;

import com.spring.demo.dto.UserDto;
import com.spring.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.userRegistration(any(UserDto.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User created successfully"));

        verify(userService, times(1)).userRegistration(any(UserDto.class));
    }

    @Test
    void testRegisterUser_UserAlreadyExists() throws Exception {
        when(userService.userRegistration(any(UserDto.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("User already Exists"));

        verify(userService, times(1)).userRegistration(any(UserDto.class));
    }

    @Test
    void testRegisterUser_InvalidUserDto() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\",\"password\":\"weak\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", hasSize(greaterThan(1))));

        verify(userService, never()).userRegistration(any(UserDto.class));
    }

    @Test
    void testAuthenticate_Success() throws Exception {
        when(userService.authenticate("test@example.com", "StrongPassword123!")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isAuthenticated").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sessionId").exists());

        verify(userService, times(1)).authenticate("test@example.com", "StrongPassword123!");
    }

    @Test
    void testAuthenticate_InvalidLogin() throws Exception {
        when(userService.authenticate("test@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    if (!content.equals("Invalid username or password")) {
                        throw new AssertionError("Expected content 'Invalid username or password' but was '" + content + "'");
                    }
                });

        verify(userService, times(1)).authenticate("test@example.com", "wrongpassword");
    }
}

package com.spring.demo.controller;
import com.spring.demo.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @MockBean
    private IOrderService orderService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testCheckOutOrder_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(orderService.checkOutOrder(anyString())).thenReturn("Order placed successfully");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/checkOutCart").session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Order placed successfully"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testCheckOutOrder_Failure() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(orderService.checkOutOrder(anyString())).thenReturn("Failed to place order");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order/checkOutCart").session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Failed to place order"));
    }
}


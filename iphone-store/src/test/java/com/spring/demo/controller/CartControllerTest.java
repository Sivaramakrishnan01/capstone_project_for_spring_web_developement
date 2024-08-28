package com.spring.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.demo.dto.CartItemDto;
import com.spring.demo.dto.CartRequest;
import com.spring.demo.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testAddToCart() throws Exception {
        CartRequest cartRequest = new CartRequest(10l,10);
        String jsonRequest = objectMapper.writeValueAsString(cartRequest);
        String message = "Item added to cart successfully";

        when(cartService.addToCart(any(CartRequest.class), any(String.class))).thenReturn(message);

        mockMvc.perform(post("/api/cart/addItemsToCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(message));

        verify(cartService).addToCart(any(CartRequest.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testUpdateCartInformation() throws Exception {
        CartRequest cartRequest = new CartRequest(10l,10);
        String jsonRequest = objectMapper.writeValueAsString(cartRequest);
        String message = "Cart item updated successfully";

        when(cartService.updateCartById(any(CartRequest.class), any(String.class))).thenReturn(message);

        mockMvc.perform(put("/api/cart/updateCart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(message));

        verify(cartService).updateCartById(any(CartRequest.class), any(String.class));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testGetCartItems() throws Exception {
        CartItemDto cartItemDto = new CartItemDto(1l,"iphone", BigDecimal.TEN,10,1l);
        when(cartService.getAllCartItems(any(String.class))).thenReturn(Collections.singletonList(cartItemDto));

        mockMvc.perform(get("/api/cart/getCartItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].quantity").exists())
                .andExpect(jsonPath("$[0].subtotal").exists());

        verify(cartService).getAllCartItems(any(String.class));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testDeleteCartItemById() throws Exception {
        Long itemId = 123L;
        String message = "Cart item with ID " + itemId + " deleted successfully";

        when(cartService.deleteCartItemById(any(Long.class), any(String.class))).thenReturn(message);

        mockMvc.perform(delete("/api/cart/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(message));

        verify(cartService,times(1)).deleteCartItemById(any(Long.class), any(String.class));
    }
}


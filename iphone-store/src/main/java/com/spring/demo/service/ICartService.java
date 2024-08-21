package com.spring.demo.service;

import com.spring.demo.dto.CartItemDto;
import com.spring.demo.dto.CartRequest;

import java.util.List;

public interface ICartService {
    String addToCart(CartRequest cartRequest, String sessionId);
    String deleteCartItemById(Long id, String sessionId);
    String updateCartById(CartRequest cartRequest, String id);

    List<CartItemDto> getAllCartItems(String sessionId);
}

package com.spring.demo.controller;

import com.spring.demo.dto.CartItemDto;
import com.spring.demo.dto.CartRequest;
import com.spring.demo.dto.response.ResponseDto;
import com.spring.demo.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @PostMapping("/add-items-to-cart")
    public ResponseEntity<ResponseDto> addToCart(@Valid @RequestBody CartRequest cartRequest, HttpSession session) {
        log.info("Adding item to cart: {}", cartRequest);
        String message = cartService.addToCart(cartRequest, session.getId());
        ResponseDto response = new ResponseDto("success", message);
        log.info("Item added to cart. Session ID: {}, Response: {}", session.getId(), response);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-cart")
    public ResponseEntity<ResponseDto> updateCartInformation(@Valid @RequestBody CartRequest cartRequest, HttpSession session) {
        log.info("Updating cart item: {}", cartRequest);
        String message = cartService.updateCartById(cartRequest, session.getId());
        ResponseDto response = new ResponseDto("success", message);
        log.info("Cart item updated. Session ID: {}, Response: {}", session.getId(), response);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get-cartitems")
    public ResponseEntity<List<CartItemDto>> getCartItems(HttpSession session) {
        log.info("Retrieving cart items. Session ID: {}", session.getId());
        List<CartItemDto> cartItems = cartService.getAllCartItems(session.getId());
        log.info("Cart items retrieved. Session ID: {}, Items: {}", session.getId(), cartItems);
        return ResponseEntity.ok(cartItems);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteCartItemById(@PathVariable("id") Long id, HttpSession session) {
        log.info("Deleting cart item. ID: {}, Session ID: {}", id, session.getId());
        try{
        String message = cartService.deleteCartItemById(id, session.getId());
        ResponseDto response = new ResponseDto("success", message);
        log.info("Cart item deleted. ID: {}, Session ID: {}, Response: {}", id, session.getId(), response);
        return ResponseEntity.ok(response);
    }catch (Exception ex){
        throw ex;
        }
    }





}

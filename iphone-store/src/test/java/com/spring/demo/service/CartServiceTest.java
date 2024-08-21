package com.spring.demo.service;
import com.spring.demo.dto.CartItemDto;
import com.spring.demo.dto.CartRequest;
import com.spring.demo.entity.Cart;
import com.spring.demo.entity.CartItem;
import com.spring.demo.entity.IphoneProduct;
import com.spring.demo.entity.SpringSession;
import com.spring.demo.exception.InsufficientStockException;
import com.spring.demo.exception.ProductNotFoundException;
import com.spring.demo.mapper.CartItemMapper;
import com.spring.demo.repository.CartItemRepository;
import com.spring.demo.repository.CartRepository;
import com.spring.demo.repository.MyCustomSessionRepository;
import com.spring.demo.service.impl.CartServiceImpl;
import com.spring.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
class CartServiceTest {
    @Mock
    private ProductServiceImpl productService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private MyCustomSessionRepository sessionRepositoryApp;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private CartServiceImpl cartService;

    private String sessionId;
    private CartRequest cartRequest;
    private IphoneProduct product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        sessionId = "session123";
        cartRequest = new CartRequest(1L, 2);

        product = IphoneProduct.builder()
                .id(1L)
                .iphoneTitle("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .available(5)
                .build();

        cartItem = CartItem.builder()
                .productId(1L)
                .name("Product 1")
                .quantity(2)
                .subtotal(BigDecimal.valueOf(200.00))
                .build();

        cart = new Cart();
        cart.setSessionId(sessionId);
        cart.setCartItemList(new ArrayList<>());
        cart.getCartItemList().add(cartItem);
    }

    @Test
    void testAddToCart_Success() {
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(sessionRepositoryApp.findBySessionId(sessionId)).thenReturn(Optional.of(new SpringSession()));
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String response = cartService.addToCart(cartRequest, sessionId);

        assertEquals("Product added to cart successfully", response);
        verify(productService, times(1)).getProductById(1L);
        verify(sessionRepositoryApp, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_ProductNotFound() {
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            cartService.addToCart(cartRequest, sessionId);
        });

        verify(productService, times(1)).getProductById(1L);
        verify(sessionRepositoryApp, times(0)).findBySessionId(sessionId);
    }

    @Test
    void testAddToCart_InsufficientStock() {
        product.setAvailable(1);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> {
            cartService.addToCart(cartRequest, sessionId);
        });

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    void testUpdateCartById_Success() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        String response = cartService.updateCartById(cartRequest, sessionId);

        assertEquals("Cart Modified Successfully", response);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(productService, times(1)).getProductById(1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testGetAllCartItems_Success() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(cartItemMapper.toCartItemDto(any(CartItem.class))).thenReturn(new CartItemDto());

        List<CartItemDto> cartItems = cartService.getAllCartItems(sessionId);

        assertFalse(cartItems.isEmpty());
        verify(cartRepository, times(1)).findBySessionId(sessionId);
    }

    @Test
    void testDeleteCartItemById_Success() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        String response = cartService.deleteCartItemById(1L, sessionId);

        assertEquals("CartItem with Product id 1 removed from your cart", response);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testDeleteCartItemById_NotFound() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));

        assertThrows(ProductNotFoundException.class, () -> {
            cartService.deleteCartItemById(999L, sessionId);
        });

        verify(cartRepository, times(1)).findBySessionId(sessionId);
    }
}


package com.spring.demo.service;
import com.spring.demo.entity.*;
import com.spring.demo.exception.EmptyCartException;
import com.spring.demo.exception.ProductNotFoundException;
import com.spring.demo.mapper.CartItemMapper;
import com.spring.demo.repository.CartRepository;
import com.spring.demo.repository.CustomerOrderRepository;
import com.spring.demo.repository.ProductRepository;
import com.spring.demo.repository.UserRepository;
import com.spring.demo.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerOrderRepository customerOrderRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private String sessionId;
    private Cart cart;
    private User user;
    private IphoneProduct product;
    private CartItem cartItem;
    private OrderItem orderItem;
    private CustomerOrder customerOrder;

    @BeforeEach
    void setUp() {
        sessionId = "session123";

        user = User.builder()
                .id(1l)
                .email("user@example.com")
                .build();

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

        orderItem = OrderItem.builder()
                .iphone(product)
                .quantity(cartItem.getQuantity())
                .subtotal(cartItem.getSubtotal())
                .build();

        customerOrder = CustomerOrder.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .user(user)
                .orderAmount(BigDecimal.valueOf(200.00))
                .orderItems(Collections.singleton(orderItem))
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCheckOutOrder_Success() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemMapper.toOrderItem(cartItem)).thenReturn(orderItem);
        when(customerOrderRepository.save(any(CustomerOrder.class))).thenReturn(customerOrder);

        String response = orderService.checkOutOrder(sessionId);

        assertEquals("Order placed successfully. Your order ID: 1", response);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(productRepository, times(1)).findById(product.getId());
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
    }

    @Test
    void testCheckOutOrder_EmptyCart() {
        cart.getCartItemList().clear();
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));

        assertThrows(EmptyCartException.class, () -> {
            orderService.checkOutOrder(sessionId);
        });

        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(userRepository, times(0)).findByEmail(anyString());
    }

    @Test
    void testCheckOutOrder_UserNotFound() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            orderService.checkOutOrder(sessionId);
        });

        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testCheckOutOrder_ProductNotFound() {
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            orderService.checkOutOrder(sessionId);
        });

        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void testCheckOutOrder_ProductInsufficientStock() {
        product.setAvailable(1);
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(cart));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(ProductNotFoundException.class, () -> {
            orderService.checkOutOrder(sessionId);
        });

        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(productRepository, times(1)).findById(product.getId());
    }
}


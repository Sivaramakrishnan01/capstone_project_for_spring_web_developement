package com.spring.demo.service.impl;
import com.spring.demo.entity.CustomerOrder;
import com.spring.demo.entity.OrderItem;
import com.spring.demo.exception.EmptyCartException;
import com.spring.demo.exception.ProductNotFoundException;
import com.spring.demo.mapper.CartItemMapper;
import com.spring.demo.mapper.Converter;
import com.spring.demo.repository.CartRepository;
import com.spring.demo.repository.CustomerOrderRepository;
import com.spring.demo.repository.ProductRepository;
import com.spring.demo.repository.UserRepository;
import com.spring.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CartItemMapper cartItemMapper;
    @Transactional
    @Override
    public String checkOutOrder(String sessionId) {
        try {
            log.info("Checking out order for session ID: {}", sessionId);
            var cartOpt = cartRepository.findBySessionId(sessionId);
            var cart = cartOpt.orElseThrow(() ->  new EmptyCartException("Cart not found for the session."));
            var cartItems = cart.getCartItemList();
            if (cartItems.isEmpty()) {
                log.warn("No items found in cart to check out for session ID: {}", sessionId);
                throw new EmptyCartException("No items found in cart to check out.");
            }

            var email = getLoginUserEmail();
            log.info("User email obtained from security context: {}", email);
            var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));

            var orderItems = cartItems.stream().map(cartItem -> {
                var productId = cartItem.getProductId();
                var  product = productRepository.findWithLockingById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found for ID: " + productId));
                if (product.getAvailable() < cartItem.getQuantity()) {
                    log.error("Product not available. Product ID: {}, Available Quantity: {}, Required Quantity: {}", productId, product.getAvailable(), cartItem.getQuantity());
                    throw new ProductNotFoundException("Product not available. Available Quantity: " + product.getAvailable() + " Required: " + cartItem.getQuantity() + " Check availability after sometime");
                }
                product.setAvailable(product.getAvailable()-cartItem.getQuantity());
                productRepository.save(product);
                OrderItem orderItem=new OrderItem();
                Converter.copyProperty(cartItem,orderItem);
//                var orderItem = cartItemMapper.toOrderItem(cartItem);
                orderItem.setIphone(product);
                orderItem.setName(cartItem.getName());
                orderItem.setQuantity(cartItem.getQuantity());
                if (orderItem.getSubtotal() == null) {
                    orderItem.setSubtotal(BigDecimal.ZERO);
                }
                System.out.println(orderItem);
                return orderItem;
            }).collect(Collectors.toSet());

            var customerOrder = CustomerOrder.builder()
                    .orderDate(LocalDateTime.now())
                    .user(user)
                    .orderAmount(calculateOrderAmount(orderItems))
                    .orderItems(orderItems)
                    .build();

            var order = customerOrderRepository.save(customerOrder);
            log.info("Order placed successfully with ID: {}", order.getId());

            cart.getCartItemList().clear();
            cartRepository.deleteBySessionId(sessionId);

            log.info("Cart cleared for session ID: {}", sessionId);

            return "Order placed successfully. Your order ID: " + order.getId();
        }catch (Exception e){
            throw e;
        }
    }

    private BigDecimal calculateOrderAmount(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getSubtotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getLoginUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

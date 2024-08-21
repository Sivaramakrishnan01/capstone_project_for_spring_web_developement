package com.spring.demo.service.impl;
import com.spring.demo.dto.CartItemDto;
import com.spring.demo.dto.CartRequest;
import com.spring.demo.entity.Cart;
import com.spring.demo.entity.CartItem;
import com.spring.demo.entity.IphoneProduct;
import com.spring.demo.entity.SpringSession;
import com.spring.demo.exception.EmptyCartException;
import com.spring.demo.exception.InsufficientStockException;
import com.spring.demo.exception.ProductNotFoundException;
import com.spring.demo.exception.SessionNotFoundException;
import com.spring.demo.mapper.Converter;
import com.spring.demo.repository.CartItemRepository;
import com.spring.demo.repository.CartRepository;
import com.spring.demo.repository.ProductRepository;
import com.spring.demo.repository.MyCustomSessionRepository;

import com.spring.demo.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements ICartService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private  final MyCustomSessionRepository sessionRepository;
    private final ProductServiceImpl productService;

    @Override
    public String addToCart(CartRequest cartRequest, String sessionId) {
        log.info("Adding product to cart. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
        try{
        IphoneProduct product = getProductOrThrowNotFound(cartRequest.getId());
        checkStockAndThrowIfInsufficient(product, cartRequest.getQuantity());

            Cart cart = getOrCreateCart(sessionId);
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity()));
            CartItem cartItem = createCartItem(product, cartRequest.getQuantity(), itemSubtotal, cart);
            cart.setName(product.getIphoneTitle());
            cart.getCartItemList().add(cartItem);
//            cartItemRepository.save(cartItem);
            cartRepository.save(cart);
            log.info("Product added to cart successfully. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
            return "Product added to cart successfully";
        }
        catch (Exception ex){
            throw ex;
        }
    }
    @Override
    public String updateCartById(CartRequest cartRequest, String sessionId) {
        log.info("Updating cart. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
        try {
            Cart cart = getCartOrThrowIfEmpty(sessionId);
            CartItem cartItem = getCartItemByIdOrThrowNotFound(cartRequest.getId(), cart);

            IphoneProduct product = getProductOrThrowNotFound(cartRequest.getId());
            checkStockAndThrowIfInsufficient(product, cartRequest.getQuantity());

            cartItem.setQuantity(cartRequest.getQuantity());
            cartItemRepository.save(cartItem);
            log.info("Cart updated successfully. Session ID: {}, Product ID: {}", sessionId, cartRequest.getId());
            return "Cart Modified Successfully";
        }
        catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public List<CartItemDto> getAllCartItems(String sessionId) {
        log.info("Fetching all cart items. Session ID: {}", sessionId);
        Cart cart = getCartOrThrowIfEmpty(sessionId);
        List<CartItemDto> cartItems = new ArrayList<>();
        CartItemDto cartItemDto=new CartItemDto();
        List<CartItem> cartItemList=cart.getCartItemList();
        for(var cart1:cartItemList){
            Converter.copyProperty(cart1, cartItemDto);
            cartItems.add(cartItemDto);
        }
        log.info("Fetched all cart items successfully. Session ID: {}", sessionId);
        return cartItems;
    }

    @Override
    public String deleteCartItemById(Long id, String sessionId) {
        log.info("Deleting cart item. Session ID: {}, Product ID: {}", sessionId, id);
        Cart cart = getCartOrThrowIfEmpty(sessionId);
        CartItem cartItem = getCartItemByIdOrThrowNotFound(id, cart);
        cart.getCartItemList().remove(cartItem);
        cartRepository.save(cart);
        log.info("Cart item deleted successfully. Session ID: {}, Product ID: {}", sessionId, id);
        return "CartItem with Product id " + id + " removed from your cart";
    }

    private IphoneProduct getProductOrThrowNotFound(Long productId) {
        log.debug("Fetching product by ID: {}", productId);
            return productService.getProductById(productId)
                    .orElseThrow(() -> {
                        log.error("Product not found. ID: {}", productId);
                        return new ProductNotFoundException("Requested product with ID " + productId + " not found");
                    });

    }
    private void checkStockAndThrowIfInsufficient(IphoneProduct product, int quantity) {
        if (quantity > product.getAvailable()) {
            log.error("Insufficient stock. Product ID: {}, Requested Quantity: {}, Available: {}", product.getId(), quantity, product.getAvailable());
            throw new InsufficientStockException("Requested quantity exceeds available stock for product: " + product.getIphoneTitle() +
                    ". Available quantity: " + product.getAvailable());
        }
    }

    private Cart getCartOrThrowIfEmpty(String sessionId) {
        log.debug("Fetching cart by session ID: {}", sessionId);
            return cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> {
                        log.warn("Cart is empty. Session ID: {}", sessionId);
                        return new EmptyCartException("Your Cart is Empty Add Some Product to cart.");
                    });
    }
    private CartItem getCartItemByIdOrThrowNotFound(Long id, Cart cart){
        return cart.getCartItemList().stream()
                .filter(cartItem -> cartItem.getProductId().equals(id))
                .findFirst()
                .orElseThrow(()->
                {
                    return new ProductNotFoundException("No order found with given id "+id);
                });
    }

    private CartItem createCartItem(IphoneProduct product, int quantity, BigDecimal subTotal, Cart cart){
        log.debug("Creating cart Item with product id {}", product.getId());
        return CartItem.builder()
                .productId(product.getId())
                .name(product.getIphoneTitle())
                .quantity(quantity)
                .subtotal(subTotal)
                .cart(cart).build();
    }

    private Cart getOrCreateCart(String sessionId){
        log.debug("getting or creating the cart for given session id {}",sessionId);
        SpringSession session=sessionRepository.findBySessionId(sessionId)
                .orElseThrow(()->{
                    return  new SessionNotFoundException("Session not found");
                });
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(()->{
                    return createNewCart(sessionId, session);
                });
    }

    private Cart createNewCart(String sessionId, SpringSession session){
        log.info("creating new cart for session ID:{}",sessionId);
        Cart cart=new Cart();
        cart.setSessionId(sessionId);
        cart.setId(session.getPrimaryId());
        log.info("new cart created for given session ID{}",sessionId);
        return cart;
    }
}

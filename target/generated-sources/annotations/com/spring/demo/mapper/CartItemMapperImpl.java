package com.spring.demo.mapper;

import com.spring.demo.dto.CartItemDto;
import com.spring.demo.entity.CartItem;
import com.spring.demo.entity.OrderItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-25T12:17:13+0530",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Override
    public CartItemDto toCartItemDto(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemDto cartItemDto = new CartItemDto();

        return cartItemDto;
    }

    @Override
    public OrderItem toOrderItem(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        return orderItem;
    }
}

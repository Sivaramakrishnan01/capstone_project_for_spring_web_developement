package com.spring.demo.mapper;
import com.spring.demo.dto.CartItemDto;
import com.spring.demo.entity.CartItem;
import com.spring.demo.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
       CartItemDto toCartItemDto(CartItem cartItem);
       OrderItem toOrderItem(CartItem cartItem);
}

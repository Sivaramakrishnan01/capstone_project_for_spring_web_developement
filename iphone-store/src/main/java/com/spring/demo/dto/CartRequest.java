package com.spring.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {

    @NotNull(message = "Id should not be null")
    private Long id;

    @NotNull(message = "Quantity should not be null")
    @Min(value = 1,message = "Quantity should be least 1")
    private Integer quantity;
}

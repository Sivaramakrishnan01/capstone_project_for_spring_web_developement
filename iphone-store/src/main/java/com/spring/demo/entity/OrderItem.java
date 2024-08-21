package com.spring.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;
    private String name;
    private BigDecimal subtotal;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private IphoneProduct iphone;
}

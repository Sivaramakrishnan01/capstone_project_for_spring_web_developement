package com.spring.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity(name = "iphone")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IphoneProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String iphoneTitle;
    private Integer available;
    private BigDecimal price;
    @Version
    @Column(name = "VERSION")
    private Long Version;
}

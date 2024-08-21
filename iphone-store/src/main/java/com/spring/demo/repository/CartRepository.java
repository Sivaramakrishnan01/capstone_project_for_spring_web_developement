package com.spring.demo.repository;

import com.spring.demo.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findBySessionId(String id);
}

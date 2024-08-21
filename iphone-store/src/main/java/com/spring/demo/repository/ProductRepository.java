package com.spring.demo.repository;

import com.spring.demo.entity.IphoneProduct;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;

import java.util.Optional;
public interface ProductRepository extends JpaRepository<IphoneProduct, Long> {
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<IphoneProduct> findWithLockingById(@Param("id") Long id);
}

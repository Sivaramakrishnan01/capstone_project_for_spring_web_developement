package com.spring.demo.repository;
import com.spring.demo.entity.SpringSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyCustomSessionRepository extends JpaRepository<SpringSession,String> {
    Optional<SpringSession> findBySessionId(String id);
}

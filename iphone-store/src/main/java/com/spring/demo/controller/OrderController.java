package com.spring.demo.controller;

import com.spring.demo.dto.response.ResponseDto;
import com.spring.demo.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    public ResponseDto checkOutOrder(HttpSession session) {
        log.info("Checking out order for session ID: {}", session.getId());
        String message = orderService.checkOutOrder(session.getId());
        var orderResponse = new ResponseDto("success", message);
        log.info("Order checked out successfully for session ID: {}. Response: {}", session.getId(), orderResponse);
        return orderResponse;
    }
}

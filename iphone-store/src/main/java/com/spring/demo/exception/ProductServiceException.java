package com.spring.demo.exception;

public class ProductServiceException extends RuntimeException {
    public ProductServiceException(String message) {
        super(message);
    }
}


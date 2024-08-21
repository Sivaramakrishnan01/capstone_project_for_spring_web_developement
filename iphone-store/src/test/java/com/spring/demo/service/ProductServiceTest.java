package com.spring.demo.service;
import com.spring.demo.dto.ProductDto;
import com.spring.demo.entity.IphoneProduct;
import com.spring.demo.mapper.Converter;
import com.spring.demo.repository.ProductRepository;
import com.spring.demo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private Converter productMapper;
    @InjectMocks
    private ProductServiceImpl productService;
    private ProductDto  productRequest;
    private IphoneProduct product;

    @BeforeEach
    void setUp() {
        productRequest = ProductDto.builder()
                .iphoneTitle("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .build();

        product = IphoneProduct.builder()
                .id(1L)
                .iphoneTitle("Product 1")
                .price(BigDecimal.valueOf(100.00))
                .build();
    }

    @Test
    void testGetProductById_ProductExists() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.getReferenceById(1L)).thenReturn(product);

        Optional<IphoneProduct> fetchedProduct = productService.getProductById(1L);

        assertTrue(fetchedProduct.isPresent());
        assertEquals(product.getId(), fetchedProduct.get().getId());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).getReferenceById(1L);
    }

    @Test
    void testGetProductById_ProductDoesNotExist() {
        when(productRepository.existsById(1L)).thenReturn(false);

        Optional<IphoneProduct> fetchedProduct = productService.getProductById(1L);

        assertFalse(fetchedProduct.isPresent());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(0)).getReferenceById(anyLong());
    }
}


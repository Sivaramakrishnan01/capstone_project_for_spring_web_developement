package com.spring.demo.controller;
import com.spring.demo.dto.ProductDto;
import com.spring.demo.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testAddProduct_Success() throws Exception {

        ProductDto productRequest = new ProductDto();
        productRequest.setIphoneTitle("Test Product");
        productRequest.setPrice(BigDecimal.valueOf(10));
        productRequest.setAvailable(10);

        Long productId = 1L;

        when(productService.addProduct(any(ProductDto.class))).thenReturn(productId);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Test Product\", \"description\": \"Test Description\", \"price\": 10.0 , \"available\": 10}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Product added successfully with ID: " + productId));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", password = "user@123")
    void testAddProduct_ValidationFailure() throws Exception {

        String invalidProductRequest = "{ \"description\": \"Test Description\", \"price\": 10.0 }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidProductRequest))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}


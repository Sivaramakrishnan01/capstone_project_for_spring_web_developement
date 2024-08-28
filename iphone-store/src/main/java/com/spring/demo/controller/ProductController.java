package com.spring.demo.controller;

import com.spring.demo.dto.ProductDto;
import com.spring.demo.dto.response.ResponseDto;
import com.spring.demo.entity.IphoneProduct;
import com.spring.demo.exception.ProductServiceException;
import com.spring.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<ResponseDto> addProduct(@Valid @RequestBody ProductDto productDto){
        Long productID= productService.addProduct(productDto);
        if(productID>0) {
            ResponseDto responseDto = new ResponseDto("success", "product created successfully");
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }
        else {
            throw new ProductServiceException("Exception occurred while creating product");
        }
    }

    @GetMapping("/view-product-list")
    public ResponseEntity<List<IphoneProduct>> getProductList(){
        log.info("Retrieving the list of products");
        List<IphoneProduct> product=productService.getListOfProduct();
        return new ResponseEntity<>(product,HttpStatus.OK);
    }
}

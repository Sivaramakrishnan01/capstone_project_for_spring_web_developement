package com.spring.demo.service;

import com.spring.demo.dto.ProductDto;
import com.spring.demo.entity.IphoneProduct;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Long addProduct(ProductDto productDto);
    public List<IphoneProduct> getListOfProduct();
    Optional<IphoneProduct> getProductById(Long id);


}

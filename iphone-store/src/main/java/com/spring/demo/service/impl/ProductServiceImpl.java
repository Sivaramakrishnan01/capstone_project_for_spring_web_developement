package com.spring.demo.service.impl;
import com.spring.demo.dto.ProductDto;
import com.spring.demo.entity.IphoneProduct;
import com.spring.demo.mapper.Converter;
import com.spring.demo.repository.ProductRepository;
import com.spring.demo.service.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    @Override
    public boolean  addProduct(ProductDto productDto) {
        try {
            IphoneProduct iphoneProduct = new IphoneProduct();
            Converter.copyProperty(productDto, iphoneProduct);
            productRepository.save(iphoneProduct);
            return true;
        }catch (Exception e){
            log.error("exception occurred while adding product");
            return false;
        }
    }
    public List<IphoneProduct> getListOfProduct(){
        try {
            log.info("Retrieving product items");
            List<IphoneProduct> productList = new ArrayList<>();
            ProductDto productDto = new ProductDto();
            List<IphoneProduct> iphoneProducts = productRepository.findAll();
            for (var prod : iphoneProducts) {
//            Converter.copyProperty(prod, productDto);
                productList.add(prod);
            }
            return productList;
        }
        catch (Exception e){
            throw new RuntimeException("exception occurred while retrieving list of product items");
        }

    }

    @Override
    public Optional<IphoneProduct> getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        if (productRepository.existsById(id)) {
            IphoneProduct product = productRepository.getReferenceById(id);
            log.info("Product found with ID: {}", id);
            return Optional.ofNullable(product);
        } else {
            log.warn("Product not found with ID: {}", id);
            return Optional.empty();
        }
    }
}

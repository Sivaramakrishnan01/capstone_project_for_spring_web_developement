package com.spring.demo.repository;

import com.spring.demo.entity.IphoneProduct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveProduct() {

        IphoneProduct product = IphoneProduct.builder()
                .iphoneTitle("Test Product")
                .available(10)
                .price(BigDecimal.valueOf(99.99))
                .build();

        IphoneProduct savedProduct = productRepository.save(product);

        assertThat(savedProduct).isNotNull();

        assertThat(savedProduct.getId()).isNotNull();

        assertThat(savedProduct.getIphoneTitle()).isEqualTo("Test Product");
        assertThat(savedProduct.getAvailable()).isEqualTo(10);
        assertThat(savedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(99.99));
    }

    @Test
    void testFindProductById() {

        IphoneProduct product = productRepository.save(IphoneProduct.builder()
                .iphoneTitle("Test Product")
                .available(10)
                .price(BigDecimal.valueOf(99.99))
                .build());

        Optional<IphoneProduct> foundProduct = productRepository.findById(product.getId());

        assertThat(foundProduct).isPresent();

        assertThat(foundProduct.get().getIphoneTitle()).isEqualTo("Test Product");
    }

    @Test
    void testUpdateProduct() {

        IphoneProduct product = productRepository.save(IphoneProduct.builder()
                .iphoneTitle("Original Product")
                .available(5)
                .price(BigDecimal.valueOf(49.99))
                .build());

        product.setIphoneTitle("Updated Product");
        product.setAvailable(15);
        product.setPrice(BigDecimal.valueOf(59.99));

        IphoneProduct updatedProduct = productRepository.save(product);

        assertThat(updatedProduct.getIphoneTitle()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getAvailable()).isEqualTo(15);
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(59.99));
    }

    @Test
    void testDeleteProduct() {

        IphoneProduct product = productRepository.save(IphoneProduct.builder()
                .iphoneTitle("Test Product")
                .available(10)
                .price(BigDecimal.valueOf(99.99))
                .build());

        productRepository.delete(product);

        Optional<IphoneProduct> deletedProduct = productRepository.findById(product.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void testFindAllProducts() {

        productRepository.saveAll(List.of(
                IphoneProduct.builder().iphoneTitle("Product 1").available(10).price(BigDecimal.valueOf(29.99)).build(),
                IphoneProduct.builder().iphoneTitle("Product 2").available(20).price(BigDecimal.valueOf(49.99)).build(),
                IphoneProduct.builder().iphoneTitle("Product 3").available(30).price(BigDecimal.valueOf(69.99)).build()
        ));

        List<IphoneProduct> products = productRepository.findAll();

        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(3);
    }
}






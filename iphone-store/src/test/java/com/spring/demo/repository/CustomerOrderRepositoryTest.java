package com.spring.demo.repository;

import com.spring.demo.entity.CustomerOrder;
import com.spring.demo.entity.IphoneProduct;
import com.spring.demo.entity.OrderItem;
import com.spring.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerOrderRepositoryTest {
    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    private User savedUser;
    private IphoneProduct savedProduct;

    @BeforeEach
    void saveUserToDb() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        savedUser = userRepository.save(user);

        IphoneProduct product = IphoneProduct.builder().iphoneTitle("Iphone 15")
                        .available(10)
                                .price(BigDecimal.valueOf(1000))
                                        .build();
        savedProduct = productRepository.save(product);
    }

    @Test
    public void testSaveCustomerOrder() {

        OrderItem orderItem = OrderItem.builder()
                .name("Product A")
                .subtotal(BigDecimal.valueOf(50))
                .quantity(2)
                .iphone(savedProduct)
                .build();

        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);

        CustomerOrder customerOrder = CustomerOrder.builder()
                .orderDate(LocalDateTime.now())
                .orderAmount(BigDecimal.valueOf(100))
                .orderItems(orderItems)
                .user(savedUser)
                .build();

        CustomerOrder savedCustomerOrder = customerOrderRepository.save(customerOrder);

        assertThat(savedCustomerOrder).isNotNull();

        assertThat(savedCustomerOrder.getId()).isNotNull();

        Optional<CustomerOrder> retrievedCustomerOrder = customerOrderRepository.findById(savedCustomerOrder.getId());

        assertThat(retrievedCustomerOrder).isPresent();
        assertThat(retrievedCustomerOrder.get().getOrderAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    public void testFindAllCustomerOrders() {

        customerOrderRepository.saveAll(createCustomerOrders());

        Iterable<CustomerOrder> allCustomerOrders = customerOrderRepository.findAll();

        assertThat(allCustomerOrders).isNotEmpty();
    }

    @Test
    public void testDeleteCustomerOrder() {

        CustomerOrder customerOrder = customerOrderRepository.save(createCustomerOrders().get(0));

        customerOrderRepository.delete(customerOrder);

        Optional<CustomerOrder> deletedCustomerOrder = customerOrderRepository.findById(customerOrder.getId());
        assertFalse(deletedCustomerOrder.isPresent());
    }

    private List<CustomerOrder> createCustomerOrders() {

        List<CustomerOrder> customerOrders = new ArrayList<>();

        OrderItem orderItem1 = OrderItem.builder()
                .name("Product A")
                .subtotal(BigDecimal.valueOf(50))
                .iphone(savedProduct)
                .quantity(2)
                .build();
        Set<OrderItem> orderItems1 = new HashSet<>();
        orderItems1.add(orderItem1);

        OrderItem orderItem2 = OrderItem.builder()
                .name("Product B")
                .subtotal(BigDecimal.valueOf(80))
                .iphone(savedProduct)
                .quantity(1)
                .build();
        Set<OrderItem> orderItems2 = new HashSet<>();
        orderItems2.add(orderItem2);

        CustomerOrder customerOrder1 = CustomerOrder.builder()
                .orderDate(LocalDateTime.now())
                .orderAmount(BigDecimal.valueOf(100))
                .orderItems(orderItems1)
                .user(savedUser)
                .build();
        customerOrders.add(customerOrder1);

        CustomerOrder customerOrder2 = CustomerOrder.builder()
                .orderDate(LocalDateTime.now())
                .orderAmount(BigDecimal.valueOf(150))
                .orderItems(orderItems2)
                .user(savedUser)
                .build();
        customerOrders.add(customerOrder2);

        return customerOrders;
    }
}

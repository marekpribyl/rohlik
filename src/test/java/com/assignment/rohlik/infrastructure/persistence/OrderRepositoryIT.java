package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderItem;
import com.assignment.rohlik.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndFindOrder() {
        // Given
        String uniqueOrderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        OrderItem item = new OrderItem("SKU-101", "Test Product", 2, BigDecimal.valueOf(10.0));
        Order order = new Order(Arrays.asList(item), 30000L);
        order.setOrderNumber(uniqueOrderNumber);

        // When
        Mono<Order> savedOrderMono = orderRepository.save(order);

        // Then
        StepVerifier.create(savedOrderMono)
                .assertNext(savedOrder -> {
                    assertNotNull(savedOrder.getId());
                    assertEquals(uniqueOrderNumber, savedOrder.getOrderNumber());
                    assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
                })
                .verifyComplete();

        // When finding by order number
        Mono<Order> foundOrderMono = orderRepository.findByOrderNumber(uniqueOrderNumber);

        // Then
        StepVerifier.create(foundOrderMono)
                .assertNext(foundOrder -> {
                    assertEquals(uniqueOrderNumber, foundOrder.getOrderNumber());
                    assertEquals(OrderStatus.CREATED, foundOrder.getStatus());
                    assertNotNull(foundOrder.getItems());
                    assertEquals(1, foundOrder.getItems().size());
                    assertEquals("SKU-101", foundOrder.getItems().get(0).getSku());
                    assertEquals(2, foundOrder.getItems().get(0).getQuantity());
                })
                .verifyComplete();
    }

    @Test
    void shouldFindByOrderNumberAndStatus() {
        // Given
        String uniqueOrderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        OrderItem item = new OrderItem("SKU-102", "Another Product", 1, BigDecimal.valueOf(20.0));
        Order order = new Order(Arrays.asList(item), 30000L);
        order.setOrderNumber(uniqueOrderNumber);

        // When
        Mono<Order> savedOrderMono = orderRepository.save(order)
                .flatMap(savedOrder -> orderRepository.findByOrderNumberAndStatus(uniqueOrderNumber, OrderStatus.CREATED));

        // Then
        StepVerifier.create(savedOrderMono)
                .assertNext(foundOrder -> {
                    assertEquals(uniqueOrderNumber, foundOrder.getOrderNumber());
                    assertEquals(OrderStatus.CREATED, foundOrder.getStatus());
                })
                .verifyComplete();

        // When searching with wrong status
        Mono<Order> notFoundOrderMono = orderRepository.findByOrderNumberAndStatus(uniqueOrderNumber, OrderStatus.PAID);

        // Then
        StepVerifier.create(notFoundOrderMono)
                .verifyComplete(); // Should be empty
    }

    @Test
    void shouldFindByStatusAndExpiresAtBefore() {
        // Given
        String uniqueOrderNumber1 = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueOrderNumber2 = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        
        OrderItem item1 = new OrderItem("SKU-103", "Expired Product 1", 3, BigDecimal.valueOf(15.0));
        OrderItem item2 = new OrderItem("SKU-104", "Expired Product 2", 1, BigDecimal.valueOf(25.0));
        
        Order order1 = new Order(Arrays.asList(item1), 1000L); // Short expiration
        order1.setOrderNumber(uniqueOrderNumber1);
        
        Order order2 = new Order(Arrays.asList(item2), 1000L); // Short expiration
        order2.setOrderNumber(uniqueOrderNumber2);

        // Save orders and wait a bit for them to expire
        Mono<Void> saveOrdersMono = orderRepository.save(order1)
                .then(orderRepository.save(order2))
                .then(Mono.delay(java.time.Duration.ofMillis(1500))) // Wait for expiration
                .then();

        StepVerifier.create(saveOrdersMono)
                .verifyComplete();

        // When finding expired orders
        Flux<Order> expiredOrdersFlux = orderRepository.findByStatusAndExpiresAtBefore(
                OrderStatus.CREATED, OffsetDateTime.now());

        // Then - should find at least our two orders
        StepVerifier.create(expiredOrdersFlux.filter(order -> 
                order.getOrderNumber().equals(uniqueOrderNumber1) || 
                order.getOrderNumber().equals(uniqueOrderNumber2))
                .collectList())
                .assertNext(orders -> {
                    assertTrue(orders.size() >= 2);
                    assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().equals(uniqueOrderNumber1)));
                    assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber().equals(uniqueOrderNumber2)));
                })
                .verifyComplete();
    }
}

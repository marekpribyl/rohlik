package com.assignment.rohlik.domain;

import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderStatus;
import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.domain.model.ProductForOrder;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderProcessing orderProcessing;

    private final long expiresInMillis = 30000;

    @BeforeEach
    void setUp() {
        orderProcessing = new OrderProcessing(orderRepository, productRepository, expiresInMillis);
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() {
        // Given
        Map<String, Integer> items = new HashMap<>();
        items.put("SKU-101", 2);
        items.put("SKU-102", 1);

        ProductForOrder product1 = new ProductForOrder();
        product1.setId(1L);
        product1.setSku("SKU-101");
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(10.0));
        product1.setStockQuantity(10);
        product1.setReservedQuantity(0);
        product1.setRequestedQuantity(2);

        ProductForOrder product2 = new ProductForOrder();
        product2.setId(2L);
        product2.setSku("SKU-102");
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(20.0));
        product2.setStockQuantity(5);
        product2.setReservedQuantity(0);
        product2.setRequestedQuantity(1);

        Order expectedOrder = new Order();
        expectedOrder.setId(1L);
        expectedOrder.setOrderNumber("12345");
        expectedOrder.setStatus(OrderStatus.CREATED);

        when(productRepository.findForOrder(items)).thenReturn(Flux.just(product1, product2));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(new Product()));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(expectedOrder));

        // When
        Mono<Order> result = orderProcessing.createOrder(items);

        // Then
        StepVerifier.create(result)
                .expectNext(expectedOrder)
                .verifyComplete();

        verify(productRepository).findForOrder(items);
        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void statusChange_shouldChangeOrderStatus() {
        // Given
        String orderNumber = "12345";
        OrderStatus targetStatus = OrderStatus.PAID;

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setOrderNumber(orderNumber);
        existingOrder.setStatus(OrderStatus.CREATED);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setOrderNumber(orderNumber);
        updatedOrder.setStatus(targetStatus);

        Map<String, Integer> itemsMap = new HashMap<>();
        itemsMap.put("SKU-101", 2);

        when(orderRepository.findByOrderNumberAndStatus(orderNumber, OrderStatus.CREATED))
                .thenReturn(Mono.just(existingOrder));
        when(productRepository.findForOrder(any())).thenReturn(Flux.just(new ProductForOrder()));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(new Product()));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(updatedOrder));

        // When
        Mono<Order> result = orderProcessing.statusChange(orderNumber, targetStatus);

        // Then
        StepVerifier.create(result)
                .expectNext(updatedOrder)
                .verifyComplete();

        verify(orderRepository).findByOrderNumberAndStatus(orderNumber, OrderStatus.CREATED);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void expireOrders_shouldExpireOrdersSuccessfully() {
        // Given
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderNumber("12345");
        order1.setStatus(OrderStatus.CREATED);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNumber("67890");
        order2.setStatus(OrderStatus.CREATED);

        when(orderRepository.findByStatusAndExpiresAtBefore(eq(OrderStatus.CREATED), any(OffsetDateTime.class)))
                .thenReturn(Flux.just(order1, order2));
        when(productRepository.findForOrder(any())).thenReturn(Flux.just(new ProductForOrder()));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(new Product()));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(new Order()));

        // When
        Mono<Void> result = orderProcessing.expireOrders();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(orderRepository).findByStatusAndExpiresAtBefore(eq(OrderStatus.CREATED), any(OffsetDateTime.class));
        verify(orderRepository, times(2)).save(any(Order.class));
    }
}

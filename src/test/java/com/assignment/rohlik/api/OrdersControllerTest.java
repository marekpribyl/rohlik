package com.assignment.rohlik.api;

import com.assignment.rohlik.api.model.NewOrderDto;
import com.assignment.rohlik.api.model.NewOrderItemDto;
import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.api.model.OrderStatusUpdateDto;
import com.assignment.rohlik.domain.OrderProcessing;
import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderItem;
import com.assignment.rohlik.domain.model.OrderStatus;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersControllerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProcessing orderService;

    @InjectMocks
    private OrdersController ordersController;

    @Test
    void createOrder_shouldReturnCreatedOrder() {
        // Given
        NewOrderItemDto item1 = new NewOrderItemDto("SKU-101", 2);
        NewOrderItemDto item2 = new NewOrderItemDto("SKU-102", 1);
        NewOrderDto newOrderDto = new NewOrderDto(Arrays.asList(item1, item2));

        // Create order items
        OrderItem orderItem1 = new OrderItem("SKU-101", "Product 1", 2, BigDecimal.valueOf(10.0));
        OrderItem orderItem2 = new OrderItem("SKU-102", "Product 2", 1, BigDecimal.valueOf(20.0));

        // Create order with items and 30 minute expiration
        Order createdOrder = new Order(Arrays.asList(orderItem1, orderItem2), 1800000L);
        createdOrder.setId(1L);
        createdOrder.setOrderNumber("12345");

        when(orderService.createOrder(any(Map.class))).thenReturn(Mono.just(createdOrder));

        // When
        Mono<ResponseEntity<OrderDto>> result = ordersController.createOrder(newOrderDto);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                    OrderDto orderDto = responseEntity.getBody();
                    assertNotNull(orderDto);
                    assertEquals("12345", orderDto.orderNumber());
                    assertEquals(OrderStatus.CREATED, orderDto.status());
                    assertEquals(2, orderDto.items().size());
                })
                .verifyComplete();
    }

    @Test
    void getOrderByOrderNumber_shouldReturnOrder() {
        // Given
        String orderNumber = "12345";

        OrderItem orderItem = new OrderItem("SKU-101", "Product 1", 2, BigDecimal.valueOf(10.0));

        // Create order with items and 30 minute expiration
        Order existingOrder = new Order(List.of(orderItem), 1800000L);
        existingOrder.setId(1L);
        existingOrder.setOrderNumber(orderNumber);

        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Mono.just(existingOrder));

        // When
        Mono<ResponseEntity<OrderDto>> result = ordersController.getOrderByOrderNumber(orderNumber);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    OrderDto orderDto = responseEntity.getBody();
                    assertNotNull(orderDto);
                    assertEquals(orderNumber, orderDto.orderNumber());
                    assertEquals(OrderStatus.CREATED, orderDto.status());
                    assertEquals(1, orderDto.items().size());
                })
                .verifyComplete();
    }

    @Test
    void getOrderByOrderNumber_shouldReturnNotFound() {
        // Given
        String orderNumber = "nonexistent";

        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<OrderDto>> result = ordersController.getOrderByOrderNumber(orderNumber);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void updateStatus_shouldUpdateOrderStatus() {
        // Given
        String orderNumber = "12345";
        OrderStatusUpdateDto statusUpdateDto = OrderStatusUpdateDto.PAID;

        OrderItem orderItem = new OrderItem("SKU-101", "Product 1", 2, BigDecimal.valueOf(10.0));

        // Create order with items and 30 minute expiration
        Order updatedOrder = new Order(List.of(orderItem), 1800000L);
        updatedOrder.setId(1L);
        updatedOrder.setOrderNumber(orderNumber);
        updatedOrder.setStatus(OrderStatus.PAID);

        when(orderService.statusChange(anyString(), any(OrderStatus.class))).thenReturn(Mono.just(updatedOrder));

        // When
        Mono<ResponseEntity<Void>> result = ordersController.updateStatus(orderNumber, statusUpdateDto);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }
}

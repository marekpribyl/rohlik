package com.assignment.rohlik.api;

import com.assignment.rohlik.api.mapper.OrderMapper;
import com.assignment.rohlik.api.model.NewOrderDto;
import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.api.model.OrderStatusUpdateDto;
import com.assignment.rohlik.domain.OrderProcessing;
import com.assignment.rohlik.domain.model.OrderStatus;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class OrdersController implements OrdersApi {

    private final OrderRepository orderRepository;

    private final OrderProcessing orderService;

    public OrdersController(OrderRepository orderRepository, OrderProcessing orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<OrderDto>> createOrder(@Valid @RequestBody NewOrderDto newOrder) {
        return orderService.createOrder(newOrder.itemsAsMap())
                .map(OrderMapper.INSTANCE::toApi)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order));
    }

    @GetMapping("/{orderNumber}")
    public Mono<ResponseEntity<OrderDto>> getOrderByOrderNumber(@PathVariable String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(OrderMapper.INSTANCE::toApi)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> updateStatus(String orderNumber, OrderStatusUpdateDto status) {
        OrderStatus targetStatus = OrderStatus.valueOf(status.name());
        return orderService.statusChange(orderNumber, targetStatus)
                .map(orderDto -> ResponseEntity.ok().build());
    }

}

package com.assignment.rohlik.api;

import com.assignment.rohlik.api.mapper.OrderMapper;
import com.assignment.rohlik.api.model.NewOrderDto;
import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.domain.OrderService;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class OrdersController implements OrdersApi {

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    public OrdersController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<OrderDto>> createOrder(@Valid @RequestBody NewOrderDto newOrder) {
        return orderService.createOrder(newOrder.items())
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

    @PostMapping("/{id}/pay")
    public Mono<ResponseEntity<OrderDto>> payOrder(@PathVariable Long id) {
        return orderService.payOrder(id)
                .map(OrderMapper.INSTANCE::toApi)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                .header("X-Error-Message", e.getMessage())
                                .build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/{id}/cancel")
    public Mono<ResponseEntity<OrderDto>> cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id)
                .map(OrderMapper.INSTANCE::toApi)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                .header("X-Error-Message", e.getMessage())
                                .build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}

package com.assignment.rohlik.api;

import com.assignment.rohlik.api.mapper.OrderMapper;
import com.assignment.rohlik.api.model.OrderRecord;
import com.assignment.rohlik.api.model.OrderRequestRecord;
import com.assignment.rohlik.domain.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class OrderController implements OrdersApi {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderRecord>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(orderMapper::toOrderRecord)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<OrderRecord>> createOrder(@Valid @RequestBody OrderRequestRecord orderRequestRecord) {
        return orderService.createOrder(orderMapper.toProductQuantitiesMap(orderRequestRecord))
                .map(orderMapper::toOrderRecord)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order))
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .header("X-Error-Message", e.getMessage())
                                .build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/{id}/pay")
    public Mono<ResponseEntity<OrderRecord>> payOrder(@PathVariable Long id) {
        return orderService.payOrder(id)
                .map(orderMapper::toOrderRecord)
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
    public Mono<ResponseEntity<OrderRecord>> cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id)
                .map(orderMapper::toOrderRecord)
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

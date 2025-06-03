package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Mono<Order> findByOrderNumber(String orderNumber);

    Mono<Order> findByOrderNumberAndStatus(String orderNumber, OrderStatus status);

    //TODO we could consider limiting the resultset here...
    Flux<Order> findByStatusAndExpiresAtBefore(OrderStatus status, OffsetDateTime when);

}

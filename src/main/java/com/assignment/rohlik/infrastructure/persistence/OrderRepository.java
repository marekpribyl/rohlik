package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderStatus;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    
    @Modifying
    @Query("UPDATE orders SET status = :status, paid_at = :paidAt WHERE id = :id AND status = 'CREATED'")
    Mono<Integer> markAsPaid(Long id, OrderStatus status, LocalDateTime paidAt);
    
    @Modifying
    @Query("UPDATE orders SET status = :status, canceled_at = :canceledAt WHERE id = :id AND status = 'CREATED'")
    Mono<Integer> cancel(Long id, OrderStatus status, LocalDateTime canceledAt);
    
    @Modifying
    @Query("UPDATE orders SET status = 'EXPIRED' WHERE status = 'CREATED' AND expires_at < :now")
    Mono<Integer> expireOrders(LocalDateTime now);
    
    Flux<Order> findByStatusAndExpiresAtLessThan(OrderStatus status, LocalDateTime expiresAt);
}

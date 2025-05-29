package com.assignment.rohlik.repository;

import com.assignment.rohlik.model.OrderItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
    
    Flux<OrderItem> findByOrderId(Long orderId);
    
    @Query("SELECT oi.* FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE o.id = :orderId")
    Flux<OrderItem> findItemsByOrderId(Long orderId);
    
    Mono<Void> deleteByOrderId(Long orderId);
    
    @Query("SELECT COUNT(*) > 0 FROM order_items oi JOIN orders o ON oi.order_id = o.id WHERE oi.product_id = :productId AND o.status IN ('CREATED', 'PAID')")
    Mono<Boolean> existsActiveOrderWithProduct(Long productId);
}

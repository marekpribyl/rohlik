package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@Transactional
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Mono<Product> findBySku(String sku);

    @Deprecated
    @Modifying
    @Query("UPDATE products SET stock_quantity = MAX(stock_quantity - :quantity, 0) WHERE id = :id AND stock_quantity >= :quantity")
    Mono<Integer> decreaseStock(Long id, Integer quantity);

    @Deprecated
    @Modifying
    @Query("UPDATE products SET stock_quantity = stock_quantity + :quantity WHERE id = :id")
    Mono<Integer> increaseStock(Long id, Integer quantity);

}

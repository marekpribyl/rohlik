package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
@Transactional
public interface ProductRepository extends ReactiveCrudRepository<Product, Long>,ProductRepositoryExtension {

    Mono<Product> findBySku(String sku);

    Flux<Product> findBySkuIn(Collection<String> skus);

    @Modifying
    @Query("UPDATE products SET stock_quantity = MAX(stock_quantity - :quantity, 0) WHERE id = :id AND stock_quantity >= :quantity")
    Mono<Integer> decreaseStock(Long id, Integer quantity);

    @Modifying
    @Query("UPDATE products SET stock_quantity = stock_quantity + :quantity WHERE id = :id")
    Mono<Integer> releaseStock(Long id, Integer quantity);

}

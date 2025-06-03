package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long>,ProductRepositoryExtension {

    Mono<Product> findBySku(String sku);

}

package com.assignment.rohlik.domain;

import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.infrastructure.persistence.OrderItemRepository;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Mono<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }


    @Transactional
    public Mono<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    return productRepository.save(existingProduct);
                });
    }

    @Transactional
    public Mono<Boolean> decreaseStock(Long id, Integer quantity) {
        return productRepository.decreaseStock(id, quantity)
                .map(updatedRows -> updatedRows > 0);
    }

    @Transactional
    public Mono<Boolean> increaseStock(Long id, Integer quantity) {
        return productRepository.increaseStock(id, quantity)
                .map(updatedRows -> updatedRows > 0);
    }
}

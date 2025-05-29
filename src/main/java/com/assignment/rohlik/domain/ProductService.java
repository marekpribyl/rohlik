package com.assignment.rohlik.domain;

import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.infrastructure.persistence.OrderItemRepository;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Mono<Product> createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Mono<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setStockQuantity(product.getStockQuantity());
                    return productRepository.save(existingProduct);
                });
    }

    @Transactional
    public Mono<Void> deleteProduct(Long id) {
        return orderItemRepository.existsActiveOrderWithProduct(id)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalStateException("Cannot delete product with active orders"));
                    }
                    return productRepository.deleteById(id);
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

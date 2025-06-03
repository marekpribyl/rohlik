package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.domain.model.ProductForOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ProductRepositoryIT {

    @Autowired
    ProductRepository repository;

    private String uniqueSku;

    @BeforeEach
    void setUp() {
        // Generate a unique SKU for each test to avoid conflicts
        uniqueSku = "SKU-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void shouldInsertProduct() {
        Product product = new Product(uniqueSku, "First Product", BigDecimal.TEN, 100);
        Product savedProduct = repository.save(product)
                .block();

        assertNotNull(savedProduct.getId());
        assertEquals(0, savedProduct.getReservedQuantity());
    }

    @Test
    void shouldFindProductsForOrder() {
        // Given - create products with unique SKUs to avoid conflicts
        String sku1 = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        String sku2 = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        String nonExistentSku = "SKU-NONEXISTENT-" + UUID.randomUUID().toString().substring(0, 8);

        Product product1 = new Product(sku1, "Product 1", BigDecimal.valueOf(10.99), 100);
        Product product2 = new Product(sku2, "Product 2", BigDecimal.valueOf(20.50), 50);

        // Save products and wait for completion
        Mono.when(
            repository.save(product1),
            repository.save(product2)
        ).block();

        Map<String, Integer> orderItems = new HashMap<>();
        orderItems.put(sku1, 5);
        orderItems.put(sku2, 10);
        orderItems.put(nonExistentSku, 3); // Non-existent product

        // When
        Flux<ProductForOrder> result = repository.findForOrder(orderItems);

        // Then
        StepVerifier.create(result)
                .assertNext(p -> {
                    assertEquals(sku2, p.getSku());
                    assertEquals(10, p.getRequestedQuantity());
                    assertEquals("Product 2", p.getName());
                    assertEquals(BigDecimal.valueOf(20.50).setScale(2), p.getPrice().setScale(2));
                    assertEquals(50, p.getStockQuantity());
                })
                .assertNext(p -> {
                assertEquals(sku1, p.getSku());
                assertEquals(5, p.getRequestedQuantity());
                assertEquals("Product 1", p.getName());
                assertEquals(BigDecimal.valueOf(10.99), p.getPrice().setScale(2));
                assertEquals(100, p.getStockQuantity());
            })

            .assertNext(p -> {
                assertEquals(nonExistentSku, p.getSku());
                assertEquals(3, p.getRequestedQuantity());
                assertEquals(null, p.getId()); // Product doesn't exist in DB
            })
            .verifyComplete();
    }

    @Test
    void shouldReturnProductBySku() {
        // Given
        String sku = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        Product product = new Product(sku, "Test Product", BigDecimal.valueOf(15.99), 50);

        // Save product and wait for completion
        repository.save(product).block();

        // When
        Mono<Product> result = repository.findBySku(sku);

        // Then
        StepVerifier.create(result)
            .assertNext(p -> {
                assertEquals(sku, p.getSku());
                assertEquals("Test Product", p.getName());
                assertEquals(BigDecimal.valueOf(15.99), p.getPrice());
                assertEquals(50, p.getStockQuantity());
                assertEquals(0, p.getReservedQuantity());
            })
            .verifyComplete();
    }

    @Test
    void shouldReturnEmptyMonoOnNonExistingSku() {
        // Given
        String nonExistentSku = "SKU-NONEXISTENT-" + UUID.randomUUID().toString();

        // When
        Mono<Product> result = repository.findBySku(nonExistentSku);

        // Then
        StepVerifier.create(result)
            .verifyComplete(); // Should be empty
    }

    @Test
    void shouldUpdateProduct() {
        // Given
        String sku = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        Product product = new Product(sku, "Original Name", BigDecimal.valueOf(10.0), 100);

        // Save product and get the saved instance
        Product savedProduct = repository.save(product).block();
        assertNotNull(savedProduct);

        // Update the product
        savedProduct.updateNameAndPrice("Updated Name", BigDecimal.valueOf(20.0));

        // When
        Mono<Product> result = repository.save(savedProduct);

        // Then
        StepVerifier.create(result)
            .assertNext(p -> {
                assertEquals(sku, p.getSku());
                assertEquals("Updated Name", p.getName());
                assertEquals(BigDecimal.valueOf(20.00), p.getPrice());
                assertEquals(100, p.getStockQuantity());
            })
            .verifyComplete();

        // Verify the update was persisted
        StepVerifier.create(repository.findBySku(sku))
            .assertNext(p -> {
                assertEquals("Updated Name", p.getName());
                assertEquals(BigDecimal.valueOf(20.00).setScale(2), p.getPrice().setScale(2));
            })
            .verifyComplete();
    }

    @Test
    void shouldDeleteProduct() {
        // Given
        String sku = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        Product product = new Product(sku, "Product to Delete", BigDecimal.valueOf(5.0), 10);

        // Save product and get the saved instance
        Product savedProduct = repository.save(product).block();
        assertNotNull(savedProduct);

        // When
        Mono<Void> deleteResult = repository.delete(savedProduct);

        // Then
        StepVerifier.create(deleteResult)
            .verifyComplete();

        // Verify the product was deleted
        StepVerifier.create(repository.findBySku(sku))
            .verifyComplete(); // Should be empty
    }

    @Test
    void shouldUpdateStockQuantity() {
        // Given
        String sku = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        Product product = new Product(sku, "Stock Update Test", BigDecimal.valueOf(25.0), 50);

        // Save product and get the saved instance
        Product savedProduct = repository.save(product).block();
        assertNotNull(savedProduct);

        // Update stock quantity
        savedProduct.updateStockQuantity(10); // Add 10 to stock

        // When
        Mono<Product> result = repository.save(savedProduct);

        // Then
        StepVerifier.create(result)
            .assertNext(p -> {
                assertEquals(60, p.getStockQuantity()); // 50 + 10
            })
            .verifyComplete();
    }

    @Test
    void shouldReserveAndReleaseStock() {
        // Given
        String sku = "SKU-TEST-" + UUID.randomUUID().toString().substring(0, 8);
        Product product = new Product(sku, "Reservation Test", BigDecimal.valueOf(30.0), 100);

        // Save product and get the saved instance
        Product savedProduct = repository.save(product).block();
        assertNotNull(savedProduct);

        // Reserve some stock
        savedProduct.reserve(20);

        // When
        Mono<Product> reserveResult = repository.save(savedProduct);

        // Then
        StepVerifier.create(reserveResult)
            .assertNext(p -> {
                assertEquals(100, p.getStockQuantity());
                assertEquals(20, p.getReservedQuantity());
                assertEquals(80, p.getAvailableQuantity());
            })
            .verifyComplete();

        // Now release the reserved stock
        Product reservedProduct = reserveResult.block();
        reservedProduct.releaseReservedStock(20);

        // When
        Mono<Product> releaseResult = repository.save(reservedProduct);

        // Then
        StepVerifier.create(releaseResult)
            .assertNext(p -> {
                assertEquals(100, p.getStockQuantity());
                assertEquals(0, p.getReservedQuantity());
                assertEquals(100, p.getAvailableQuantity());
            })
            .verifyComplete();
    }

}

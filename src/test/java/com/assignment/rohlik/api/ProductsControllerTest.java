package com.assignment.rohlik.api;

import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.ProductsDto;
import com.assignment.rohlik.api.model.UpdateProductDto;
import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductsController productsController;

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        // Given
        Product product1 = new Product("SKU-101", "Product 1", BigDecimal.valueOf(10.0), 100);
        product1.setId(1L);

        Product product2 = new Product("SKU-102", "Product 2", BigDecimal.valueOf(20.0), 50);
        product2.setId(2L);

        when(productRepository.findAll()).thenReturn(Flux.just(product1, product2));

        // When
        Mono<ProductsDto> result = productsController.getAllProducts();

        // Then
        StepVerifier.create(result)
                .assertNext(productsDto -> {
                    assertNotNull(productsDto);
                    assertEquals(2, productsDto.products().size());
                    assertEquals("SKU-101", productsDto.products().get(0).sku());
                    assertEquals("Product 1", productsDto.products().get(0).name());
                    assertEquals(BigDecimal.valueOf(10.0), productsDto.products().get(0).price());
                    assertEquals(100, productsDto.products().get(0).stockInfo().quantity());
                })
                .verifyComplete();
    }

    @Test
    void getProductBySku_shouldReturnProduct() {
        // Given
        String sku = "SKU-101";
        Product product = new Product(sku, "Product 1", BigDecimal.valueOf(10.0), 100);
        product.setId(1L);

        when(productRepository.findBySku(sku)).thenReturn(Mono.just(product));

        // When
        Mono<ResponseEntity<ProductDto>> result = productsController.getProductBySku(sku);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    ProductDto productDto = responseEntity.getBody();
                    assertNotNull(productDto);
                    assertEquals(sku, productDto.sku());
                    assertEquals("Product 1", productDto.name());
                    assertEquals(BigDecimal.valueOf(10.0), productDto.price());
                    assertEquals(100, productDto.stockInfo().quantity());
                })
                .verifyComplete();
    }

    @Test
    void getProductBySku_shouldReturnNotFound() {
        // Given
        String sku = "nonexistent";

        when(productRepository.findBySku(sku)).thenReturn(Mono.empty());

        // When
        Mono<ResponseEntity<ProductDto>> result = productsController.getProductBySku(sku);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void createProduct_shouldCreateProduct() {
        // Given
        NewProductDto newProductDto = new NewProductDto("SKU-101", "New Product", 50, BigDecimal.valueOf(15.0));

        Product savedProduct = new Product("SKU-101", "New Product", BigDecimal.valueOf(15.0), 50);
        savedProduct.setId(1L);

        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        // When
        Mono<ProductDto> result = productsController.createProduct(newProductDto);

        // Then
        StepVerifier.create(result)
                .assertNext(productDto -> {
                    assertNotNull(productDto);
                    assertEquals("SKU-101", productDto.sku());
                    assertEquals("New Product", productDto.name());
                    assertEquals(BigDecimal.valueOf(15.0), productDto.price());
                    assertEquals(50, productDto.stockInfo().quantity());
                })
                .verifyComplete();
    }

    @Test
    void updateProduct_shouldUpdateProduct() {
        // Given
        String sku = "SKU-101";
        UpdateProductDto updateProductDto = new UpdateProductDto("Updated Product", BigDecimal.valueOf(25.0));

        Product existingProduct = new Product(sku, "Original Product", BigDecimal.valueOf(10.0), 100);
        existingProduct.setId(1L);

        Product updatedProduct = new Product(sku, "Updated Product", BigDecimal.valueOf(25.0), 100);
        updatedProduct.setId(1L);

        when(productRepository.findBySku(sku)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        // When
        Mono<ResponseEntity<ProductDto>> result = productsController.updateProduct(sku, updateProductDto);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    ProductDto productDto = responseEntity.getBody();
                    assertNotNull(productDto);
                    assertEquals(sku, productDto.sku());
                    assertEquals("Updated Product", productDto.name());
                    assertEquals(BigDecimal.valueOf(25.0), productDto.price());
                    assertEquals(100, productDto.stockInfo().quantity());
                })
                .verifyComplete();
    }

    @Test
    void updateStock_shouldUpdateProductStock() {
        // Given
        String sku = "SKU-101";
        Integer quantityChange = 10;

        Product existingProduct = new Product(sku, "Product 1", BigDecimal.valueOf(10.0), 100);
        existingProduct.setId(1L);

        Product updatedProduct = new Product(sku, "Product 1", BigDecimal.valueOf(10.0), 110);
        updatedProduct.setId(1L);

        when(productRepository.findBySku(sku)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        // When
        Mono<ResponseEntity<ProductDto>> result = productsController.updateStock(sku, quantityChange);

        // Then
        StepVerifier.create(result)
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    ProductDto productDto = responseEntity.getBody();
                    assertNotNull(productDto);
                    assertEquals(sku, productDto.sku());
                    assertEquals("Product 1", productDto.name());
                    assertEquals(BigDecimal.valueOf(10.0), productDto.price());
                    assertEquals(110, productDto.stockInfo().quantity());
                })
                .verifyComplete();
    }
}

package com.assignment.rohlik.api;

import com.assignment.rohlik.api.mapper.ProductMapper;
import com.assignment.rohlik.api.model.ProductRecord;
import com.assignment.rohlik.api.model.ProductRequestRecord;
import com.assignment.rohlik.domain.ProductService;
import com.assignment.rohlik.domain.model.Product;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductController implements ProductsApi {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public Flux<ProductRecord> getAllProducts() {
        return productService.getAllProducts()
                .map(productMapper::toProductRecord);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductRecord>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(productMapper::toProductRecord)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductRecord> createProduct(@Valid @RequestBody ProductRequestRecord productRequestRecord) {
        Product product = productMapper.toProduct(productRequestRecord);
        return productService.createProduct(product)
                .map(productMapper::toProductRecord);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductRecord>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestRecord productRequestRecord) {
        return productService.getProductById(id)
                .flatMap(existingProduct -> {
                    productMapper.updateProductFromRequest(productRequestRecord, existingProduct);
                    return productService.updateProduct(id, existingProduct);
                })
                .map(productMapper::toProductRecord)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
}

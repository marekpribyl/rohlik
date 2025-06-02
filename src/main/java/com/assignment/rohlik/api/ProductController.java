package com.assignment.rohlik.api;

import com.assignment.rohlik.api.mapper.ProductMapper;
import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.ProductsDto;
import com.assignment.rohlik.api.model.UpdateProductDto;
import com.assignment.rohlik.domain.ProductService;
import com.assignment.rohlik.domain.model.Product;
import jakarta.validation.Valid;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public Mono<ProductsDto> getAllProducts(int offset, int count) {
        return Mono.empty(); //TODO productService.getAllProducts().map(productMapper::toProductRecord);
    }

    @GetMapping("/{sku}")
    public Mono<ResponseEntity<ProductDto>> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(productMapper::toProductRecord)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> createProduct(@Valid @RequestBody NewProductDto productRequestRecord) {
        Product product = productMapper.toProduct(productRequestRecord);
        return productService.createProduct(product)
                .map(productMapper::toProductRecord);
    }

    @PutMapping("/{sku}")
    public Mono<ResponseEntity<ProductDto>> updateProduct(@PathVariable String sku, @Valid @RequestBody UpdateProductDto productRequestRecord) {
        return productService.getProductBySku(sku)
                .flatMap(existingProduct -> {
                    productMapper.updateProductFromRequest(productRequestRecord, existingProduct);
                    return productService.updateProduct(existingProduct.getId(), existingProduct);
                })
                .map(productMapper::toProductRecord)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sku}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .flatMap(product -> productService.deleteProduct(product.getId()))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(e -> {
                    if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @Override
    public Mono<ResponseEntity<Integer>> updateStock(final Integer quantityChange) {
        throw new NotImplementedException("NOT IMPLEMENTED YET");
    }

}

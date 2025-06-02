package com.assignment.rohlik.api;

import com.assignment.rohlik.api.mapper.ProductMapper;
import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.ProductsDto;
import com.assignment.rohlik.api.model.UpdateProductDto;
import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.noContent;

@RestController
public class ProductsController implements ProductsApi {

    private final ProductRepository productRepository;


    public ProductsController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public Mono<ProductsDto> getAllProducts(int offset, int count) {
        return productRepository.findAll()
                .map(ProductMapper.INSTANCE::toApi)
                .collectList()
                .map(products -> new ProductsDto(products, false)); //TODO make the paging work
    }

    @GetMapping("/{sku}")
    public Mono<ResponseEntity<ProductDto>> getProductBySku(@PathVariable String sku) {
        return productRepository.findBySku(sku)
                .map(ProductMapper.INSTANCE::toApi)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> createProduct(@Valid @RequestBody NewProductDto productRequestRecord) {
        Product product = ProductMapper.INSTANCE.fromApi(productRequestRecord);
        return productRepository.save(product)
                .map(ProductMapper.INSTANCE::toApi);
    }

    @PutMapping("/{sku}")
    @Transactional
    public Mono<ResponseEntity<ProductDto>> updateProduct(@PathVariable String sku, @Valid @RequestBody UpdateProductDto productRequestRecord) {
        return productRepository.findBySku(sku)
                .map(product -> product.updateNameAndPrice(productRequestRecord.name(), productRequestRecord.price()))
                .flatMap(productRepository::save)
                .map(ProductMapper.INSTANCE::toApi)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{sku}")
    @Transactional
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable String sku) {
        return productRepository.findBySku(sku)
                .flatMap(product -> {
                    if (product.hasReservedQuantity()) {
                        return Mono.error(new IllegalArgumentException("Cannot delete product [%s] with [%d] reserved items".formatted(sku, product.getReservedQuantity())));
                    }
                    return productRepository.delete(product);
                })
                .then(Mono.just(noContent().build()));
    }

    @Override
    @Transactional
    public Mono<ResponseEntity<ProductDto>> updateStock(String sku, Integer quantityChange) {
        return productRepository.findBySku(sku)
                .map(product -> product.updateStockQuantity(quantityChange))
                .flatMap(productRepository::save)
                .map(ProductMapper.INSTANCE::toApi)
                .map(ResponseEntity::ok);
    }

}

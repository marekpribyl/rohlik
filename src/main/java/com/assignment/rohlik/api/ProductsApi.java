package com.assignment.rohlik.api;

import com.assignment.rohlik.api.model.ProductRecord;
import com.assignment.rohlik.api.model.ProductRequestRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Products", description = "Products management API")
@RequestMapping("/api/products")
public interface ProductsApi {

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(schema = @Schema(implementation = ProductRecord.class)))
    })
    @GetMapping
    Flux<ProductRecord> getAllProducts();

    @Operation(summary = "Get product by ID", description = "Returns a single product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(schema = @Schema(implementation = ProductRecord.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    Mono<ResponseEntity<ProductRecord>> getProductById(
            @Parameter(description = "ID of the product to retrieve", required = true) 
            @PathVariable Long id);

    @Operation(summary = "Create a new product", description = "Creates a new product with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully", 
                    content = @Content(schema = @Schema(implementation = ProductRecord.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ProductRecord> createProduct(
            @Parameter(description = "Product to create", required = true) 
            @Valid @RequestBody ProductRequestRecord productRequestRecord);

    @Operation(summary = "Update a product", description = "Updates an existing product with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", 
                    content = @Content(schema = @Schema(implementation = ProductRecord.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    Mono<ResponseEntity<ProductRecord>> updateProduct(
            @Parameter(description = "ID of the product to update", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated product details", required = true) 
            @Valid @RequestBody ProductRequestRecord productRequestRecord);

    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete product with active orders")
    })
    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Void>> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true) 
            @PathVariable Long id);
}

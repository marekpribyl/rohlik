package com.assignment.rohlik.api;

import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.ProductsDto;
import com.assignment.rohlik.api.model.UpdateProductDto;
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
import reactor.core.publisher.Mono;

@Tag(name = "Products", description = "Products management API")
@RequestMapping(value = "/api/products/v1", produces = "application/json")
public interface ProductsApi {

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(schema = @Schema(implementation = ProductsDto.class)))
    })
    @GetMapping
    Mono<ProductsDto> getAllProducts(int offset, int count);

    @Operation(summary = "Get product by SKU", description = "Returns a single product by its SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{sku}")
    Mono<ResponseEntity<ProductDto>> getProductBySku(
            @Parameter(description = "SKU of the product to retrieve", required = true) 
            @PathVariable String sku);

    @Operation(summary = "Create a new product", description = "Creates a new product with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully", 
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ProductDto> createProduct(
            @Parameter(description = "Product to create", required = true) 
            @Valid @RequestBody NewProductDto productRequestRecord);

    @Operation(summary = "Update a product", description = "Updates an existing product with the given details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", 
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping(path = "/{sku}", consumes = "application/json")
    Mono<ResponseEntity<ProductDto>> updateProduct(
            @Parameter(description = "SKU of the product to update", required = true) 
            @PathVariable String sku,
            @Parameter(description = "Updated product details", required = true) 
            @Valid @RequestBody UpdateProductDto productRequestRecord);

    @Operation(summary = "Delete a product", description = "Deletes a product by its SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete product with active orders")
    })
    @DeleteMapping("/{sku}")
    Mono<ResponseEntity<Void>> deleteProduct(
            @Parameter(description = "SKU of the product to delete", required = true) 
            @PathVariable String sku);


    @Operation(summary = "Update stock of the product by the provided quantity", description = "Positive value to increase the quantity, negative value to decrease it. Zero has no effect")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock successfully updated, returns the new quantity",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))),
            @ApiResponse(responseCode = "400", description = "Provided negative quantity is more than current stock quantity")
    })
    @PutMapping("/{sku}/inventory")
    Mono<ResponseEntity<Integer>> updateStock(
            @Parameter(description = "Product quantity change (positive or negative)", example = "10", required = true)
            @RequestBody Integer quantityChange);

}

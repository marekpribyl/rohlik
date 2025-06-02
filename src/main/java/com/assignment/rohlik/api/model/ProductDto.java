package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import static com.assignment.rohlik.domain.model.Product.PRODUCT_NAME_PATTERN;

@Schema(description = "Product information including stock and price")
public record ProductDto(

    @Schema(description = "Stock Keeping Unit - unique identifier of the product", example = "SKU-123")
    @NotBlank(message = "SKU is required")
    String sku,

    @Schema(description = "Name of the product", example = "Organic Banana")
    @NotBlank(message = "Product name is required")
    @Pattern(regexp = PRODUCT_NAME_PATTERN, message = "Product name must be alphanumeric and less than or equal to 250 characters")
    String name,

    @Schema(description = "Product's stock status")
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    StockInfoDto stockInfo,

    @Schema(description = "Price per unit", example = "1.99")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    BigDecimal price

) {}

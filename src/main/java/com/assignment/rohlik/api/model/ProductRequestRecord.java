package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Record representing a product request in the API.
 */
@Schema(description = "Product creation or update request")
public record ProductRequestRecord(
    @Schema(description = "Name of the product", example = "Organic Banana")
    @NotBlank(message = "Product name is required")
    String name,

    @Schema(description = "Available quantity in stock", example = "100")
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    Integer stockQuantity,

    @Schema(description = "Price per unit", example = "1.99")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    BigDecimal price
) {}

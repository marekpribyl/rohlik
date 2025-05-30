package com.assignment.rohlik.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Record representing a product in the API.
 */
public record ProductRecord(
    Long id,
    
    @NotBlank(message = "Product name is required")
    String name,
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    Integer stockQuantity,
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    BigDecimal price
) {}

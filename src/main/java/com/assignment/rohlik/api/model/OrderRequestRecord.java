package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Record representing an order request in the API.
 */
@Schema(description = "Order request with product quantities")
public record OrderRequestRecord(
    @Schema(description = "Map of product IDs to quantities", example = "{\"1\": 2, \"2\": 1}")
    @NotEmpty(message = "Order must contain at least one product")
    Map<@NotNull(message = "Product ID cannot be null") Long, 
        @NotNull(message = "Quantity cannot be null") @Min(value = 1, message = "Quantity must be at least 1") Integer> productQuantities
) {}

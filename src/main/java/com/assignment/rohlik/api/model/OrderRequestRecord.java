package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Record representing an order request in the API.
 */
@Schema(description = "Order request with product quantities")
public record OrderRequestRecord(
    @Schema(description = "Map of product SKUs to quantities", example = "{\"SKU-123\": 2, \"SKU-456\": 1}")
    @NotEmpty(message = "Order must contain at least one product")
    Map<@NotNull(message = "Product SKU cannot be null") @NotBlank(message = "Product SKU cannot be empty") String, 
        @NotNull(message = "Quantity cannot be null") @Min(value = 1, message = "Quantity must be at least 1") Integer> productQuantities
) {}

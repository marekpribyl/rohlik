package com.assignment.rohlik.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Record representing an order request in the API.
 */
public record OrderRequestRecord(
    @NotEmpty(message = "Order must contain at least one product")
    Map<@NotNull(message = "Product ID cannot be null") Long, 
        @NotNull(message = "Quantity cannot be null") @Min(value = 1, message = "Quantity must be at least 1") Integer> productQuantities
) {}

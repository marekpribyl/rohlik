package com.assignment.rohlik.api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Record representing an order item in the API.
 */
public record OrderItemRecord(
    Long id,
    Long orderId,
    Long productId,
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,
    
    @NotNull(message = "Price per unit cannot be null")
    BigDecimal pricePerUnit,
    
    BigDecimal totalPrice
) {}

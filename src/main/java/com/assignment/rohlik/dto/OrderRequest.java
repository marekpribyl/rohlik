package com.assignment.rohlik.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class OrderRequest {
    
    @NotEmpty(message = "Order must contain at least one product")
    private Map<@NotNull(message = "Product ID cannot be null") Long, 
                @NotNull(message = "Quantity cannot be null") @Min(value = 1, message = "Quantity must be at least 1") Integer> productQuantities;
    
    // Getters and Setters
    public Map<Long, Integer> getProductQuantities() {
        return productQuantities;
    }
    
    public void setProductQuantities(Map<Long, Integer> productQuantities) {
        this.productQuantities = productQuantities;
    }
}

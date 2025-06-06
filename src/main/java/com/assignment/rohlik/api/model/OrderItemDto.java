package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Order item information including product, quantity, and price")
public record OrderItemDto(

    @Schema(description = "SKU of the product in this order item", example = "SKU-123")
    @NotBlank(message = "Product SKU cannot be null")
    String sku,

    @Schema(description = "Quantity of the product ordered", example = "2")
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @Schema(description = "Price per unit at the time of order", example = "9.99")
    @NotNull(message = "Price per unit cannot be null")
    BigDecimal pricePerUnit,

    @Schema(description = "Total price for this order item (quantity * pricePerUnit)", example = "19.98")
    BigDecimal totalPrice

) {}

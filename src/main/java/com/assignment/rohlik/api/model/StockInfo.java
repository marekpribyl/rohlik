package com.assignment.rohlik.api.model;


import io.swagger.v3.oas.annotations.media.Schema;

public record StockInfo(
        @Schema(description = "Quantity of the product on stock including reserved items", example = "100")
        Long quantity,

        @Schema(description = "Quantity of the available items (overallQuantity - reservedQuantity)", example = "90")
        Long available
) {}

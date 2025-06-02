package com.assignment.rohlik.api.model;


import io.swagger.v3.oas.annotations.media.Schema;

public record StockInfoDto(

        @Schema(description = "Quantity of the product on stock including reserved items", example = "100")
        Integer quantity,

        @Schema(description = "Quantity of the available items (overallQuantity - reservedQuantity)", example = "90")
        Integer available

) {}

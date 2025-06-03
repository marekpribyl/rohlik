package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;

@Schema(description = "Order request with product quantities")
public record NewOrderDto(

        @Schema(description = "Order items")
        @Valid
        List<NewOrderItemDto> items

) {}

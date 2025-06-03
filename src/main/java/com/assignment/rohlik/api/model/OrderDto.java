package com.assignment.rohlik.api.model;

import com.assignment.rohlik.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(description = "Order information including items and status")
public record OrderDto(

    @Schema(description = "Unique identifier of the order", example = "1")
    String orderNumber,

    @Schema(description = "Current status of the order", example = "CREATED")
    OrderStatus status,

    @Schema(description = "Date and time when the order was created", example = "2023-07-21T10:30:00+02:00")
    OffsetDateTime createdAt,

    @Schema(description = "Date and time when the order will expire if not paid", example = "2023-07-21T11:00:00+02:00")
    OffsetDateTime expiresAt,

    @Schema(description = "Date and time when the order was paid", example = "2023-07-21T10:45:00+02:00")
    OffsetDateTime updatedAt,

    @Schema(description = "List of items in the order")
    List<OrderItemDto> items,

    @Schema(description = "Total price of the order", example = "125.50")
    BigDecimal totalPrice
) {}

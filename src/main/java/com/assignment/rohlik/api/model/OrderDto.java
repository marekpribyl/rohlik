package com.assignment.rohlik.api.model;

import com.assignment.rohlik.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Record representing an order in the API.
 */
@Schema(description = "Order information including items and status")
public record OrderDto(
    @Schema(description = "Unique identifier of the order", example = "1")
    Long id,

    @Schema(description = "Current status of the order", example = "CREATED")
    OrderStatus status,

    @Schema(description = "Date and time when the order was created", example = "2023-07-21T10:30:00")
    LocalDateTime createdAt,

    @Schema(description = "Date and time when the order will expire if not paid", example = "2023-07-21T11:00:00")
    LocalDateTime expiresAt,

    @Schema(description = "Date and time when the order was paid", example = "2023-07-21T10:45:00")
    LocalDateTime paidAt,

    @Schema(description = "Date and time when the order was canceled", example = "2023-07-21T10:40:00")
    LocalDateTime canceledAt,

    @Schema(description = "List of items in the order")
    List<OrderItemDto> items,

    @Schema(description = "Total price of the order", example = "125.50")
    BigDecimal totalPrice
) {}

package com.assignment.rohlik.api.model;

import com.assignment.rohlik.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Record representing an order in the API.
 */
public record OrderRecord(
    Long id,
    OrderStatus status,
    LocalDateTime createdAt,
    LocalDateTime expiresAt,
    LocalDateTime paidAt,
    LocalDateTime canceledAt,
    List<OrderItemRecord> items,
    BigDecimal totalPrice
) {}

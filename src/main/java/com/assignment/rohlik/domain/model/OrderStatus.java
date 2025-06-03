package com.assignment.rohlik.domain.model;

//simplistic state model
// NONE -> created
// created -> paid
// created -> canceled
// created -> expired

public enum OrderStatus {
    CREATED,
    PAID,
    CANCELED,
    EXPIRED
}

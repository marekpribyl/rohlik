package com.assignment.rohlik.domain;

import com.assignment.rohlik.domain.model.Order;

public class InvalidOrderStateException extends IllegalStateException {
    
    private final Order order;

    public InvalidOrderStateException(String message) {
        super(message);
        this.order = null;
    }

    public Order getOrder() {
        return order;
    }
    
}

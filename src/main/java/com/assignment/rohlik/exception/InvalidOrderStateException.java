package com.assignment.rohlik.exception;

import com.assignment.rohlik.model.Order;
import com.assignment.rohlik.model.OrderStatus;

public class InvalidOrderStateException extends IllegalStateException {
    
    private final Order order;
    private final OrderStatus requiredStatus;
    
    public InvalidOrderStateException(Order order, OrderStatus requiredStatus) {
        super("Order is in " + order.getStatus() + " state, but should be in " + requiredStatus + " state");
        this.order = order;
        this.requiredStatus = requiredStatus;
    }
    
    public InvalidOrderStateException(Order order, String message) {
        super(message);
        this.order = order;
        this.requiredStatus = null;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public OrderStatus getRequiredStatus() {
        return requiredStatus;
    }
}

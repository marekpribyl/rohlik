package com.assignment.rohlik.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_items")
public class OrderItem {
    @Id
    private Long id;
    
    @Column("order_id")
    private Long orderId;
    
    @Column("product_id")
    private Long productId;
    
    @Column("quantity")
    private Integer quantity;
    
    @Column("price_per_unit")
    private java.math.BigDecimal pricePerUnit;

    // Constructors
    public OrderItem() {
    }

    public OrderItem(Long orderId, Long productId, Integer quantity, java.math.BigDecimal pricePerUnit) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public java.math.BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(java.math.BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
    
    // Business methods
    public java.math.BigDecimal getTotalPrice() {
        return pricePerUnit.multiply(new java.math.BigDecimal(quantity));
    }
}

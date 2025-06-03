package com.assignment.rohlik.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class OrderItem {

    private String sku;

    private String name;
    
    private Integer quantity;
    
    private BigDecimal pricePerUnit;

    public OrderItem() {
    }

    public OrderItem(String sku, String name, Integer quantity, BigDecimal pricePerUnit) {
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public static OrderItem fromProductForOrder(ProductForOrder productForOrder) {
        return new OrderItem(
                productForOrder.getSku(),
                productForOrder.getName(),
                productForOrder.getRequestedQuantity(),
                productForOrder.getPrice()
        );
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public OrderItem setName(final String name) {
        this.name = name;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @JsonIgnore
    public BigDecimal getTotalPrice() {
        return pricePerUnit.multiply(new BigDecimal(quantity));
    }

}

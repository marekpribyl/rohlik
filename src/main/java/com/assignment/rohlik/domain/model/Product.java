package com.assignment.rohlik.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("products")
public class Product {

    public static final String PRODUCT_SKU_PATTERN = "[A-Z]{3}-[0-9]{3,30}";

    public static final String PRODUCT_NAME_PATTERN = "[A-Za-z0-9 -_]{1,250}";

    @Id
    private Long id;

    @Column("sku")
    private String sku;

    @Column("name")
    private String name;

    @Column("stock_quantity")
    private int stockQuantity;

    @Column("reserved_quantity")
    private int reservedQuantity;

    @Column("price")
    private BigDecimal price;

    // Constructors
    public Product() {
    }

    public Product(String sku, String name, BigDecimal price, int stockQuantity) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.reservedQuantity = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setName(String name) {
        this.name = name;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public Product setReservedQuantity(final Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAvailableQuantity() {
        return stockQuantity - reservedQuantity;
    }

    public boolean hasReservedQuantity() {
        return reservedQuantity > 0;
    }

    public Product updateNameAndPrice(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
        return this;
    }

    public Product updateStockQuantity(int delta) {
        int newQuantity = stockQuantity + delta;
        if (hasReservedQuantity() && newQuantity < reservedQuantity) {
            throw new IllegalArgumentException("There is reserved quantity of [%d] items, cannot decrease stock by [%d] items".formatted(reservedQuantity, delta));
        }
        this.stockQuantity = Math.max(newQuantity, 0);

        return this;
    }

}

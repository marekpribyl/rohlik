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
    private Integer stockQuantity;

    @Column("price")
    private BigDecimal price;

    // Constructors
    public Product() {
    }

    public Product(String sku, String name, Integer stockQuantity, BigDecimal price) {
        this.sku = sku;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

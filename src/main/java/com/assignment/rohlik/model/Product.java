package com.assignment.rohlik.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("products")
public class Product {
    @Id
    private Long id;
    
    @Column("name")
    private String name;
    
    @Column("stock_quantity")
    private Integer stockQuantity;
    
    @Column("price")
    private BigDecimal price;

    // Constructors
    public Product() {
    }

    public Product(String name, Integer stockQuantity, BigDecimal price) {
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

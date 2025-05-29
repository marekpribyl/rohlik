package com.assignment.rohlik.exception;

import com.assignment.rohlik.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class InsufficientStockException extends IllegalArgumentException {
    
    private final List<Product> productsWithInsufficientStock;
    
    public InsufficientStockException(List<Product> productsWithInsufficientStock) {
        super("Not enough stock for products: " + 
              productsWithInsufficientStock.stream()
                  .map(Product::getName)
                  .collect(Collectors.joining(", ")));
        this.productsWithInsufficientStock = productsWithInsufficientStock;
    }
    
    public List<Product> getProductsWithInsufficientStock() {
        return productsWithInsufficientStock;
    }
}

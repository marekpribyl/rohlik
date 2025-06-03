package com.assignment.rohlik.domain.model;

import java.util.function.BiFunction;

public enum OrderStatus {

    CREATED(OrderStatus::doNothing),

    PAID(Product::removeFromStock),

    CANCELED(Product::releaseReservedStock),

    EXPIRED(Product::releaseReservedStock);


    private BiFunction<Product, Integer, Product> doOnProduct;

    OrderStatus(BiFunction<Product, Integer, Product> doOnProduct) {
        this.doOnProduct = doOnProduct;
    }

    public BiFunction<Product, Integer, Product> doOnProduct() {
        return doOnProduct;
    }

    private static Product doNothing(Product product, Integer quantity) {
        return product;
    }

}

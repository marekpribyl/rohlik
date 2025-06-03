package com.assignment.rohlik.domain.model;


import org.springframework.data.annotation.Transient;

import static java.util.Objects.nonNull;

public class ProductForOrder extends Product {

    @Transient
    private int requestedQuantity;

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public ProductForOrder setRequestedQuantity(int requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
        return this;
    }

    public ProductForOrder reserveIfPossible() {
        if (productIsPresent()) {
            reserve(requestedQuantity);
            return this;
        }

        throw new IllegalArgumentException("Product [%s] is not available".formatted(getSku()));
    }

    public boolean productIsPresent() {
        return nonNull(getId());
    }

}

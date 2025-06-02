package com.assignment.rohlik.api.model;


import java.util.List;

public record ProductsDto(
        List<ProductDto> products,
        boolean hasMore
) {}

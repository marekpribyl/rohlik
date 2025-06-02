package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.OrderItemDto;
import com.assignment.rohlik.domain.ProductService;
import com.assignment.rohlik.domain.model.OrderItem;
import com.assignment.rohlik.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper for converting between OrderItem entity and OrderItemRecord.
 */
@Mapper(componentModel = "spring")
public abstract class OrderItemMapper {

    @Autowired
    protected ProductService productService;

    /**
     * Convert OrderItem entity to OrderItemRecord.
     */
    @Mapping(target = "totalPrice", expression = "java(orderItem.getTotalPrice())")
    @Mapping(source = "productId", target = "sku", qualifiedByName = "productIdToSku")
    public abstract OrderItemDto toOrderItemRecord(OrderItem orderItem);

    /**
     * Convert product ID to SKU.
     */
    @Named("productIdToSku")
    protected String productIdToSku(Long productId) {
        Product product = productService.getProductById(productId).block();
        return product != null ? product.getSku() : null;
    }

    /**
     * Convert product SKU to ID.
     */
    @Named("productSkuToId")
    protected Long productSkuToId(String productSku) {
        Product product = productService.getProductBySku(productSku).block();
        return product != null ? product.getId() : null;
    }
}

package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.api.model.OrderRequestRecord;
import com.assignment.rohlik.domain.ProductService;
import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapper for converting between Order entity and OrderRecord/OrderRequestRecord.
 */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public abstract class OrderMapper {

    @Autowired
    protected ProductService productService;

    /**
     * Convert Order entity to OrderRecord.
     */
    @Mapping(target = "totalPrice", expression = "java(order.getTotalPrice())")
    public abstract OrderDto toOrderRecord(Order order);

    /**
     * Convert product SKUs to IDs and extract quantities map from OrderRequestRecord.
     */
    public Map<Long, Integer> toProductQuantitiesMap(OrderRequestRecord orderRequestRecord) {
        Map<String, Integer> skuQuantities = orderRequestRecord.productQuantities();
        Map<Long, Integer> idQuantities = new HashMap<>();

        skuQuantities.forEach((sku, quantity) -> {
            Product product = productService.getProductBySku(sku).block();
            if (product != null) {
                idQuantities.put(product.getId(), quantity);
            }
        });

        return idQuantities;
    }
}

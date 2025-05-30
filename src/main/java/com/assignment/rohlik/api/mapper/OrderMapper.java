package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.OrderRecord;
import com.assignment.rohlik.api.model.OrderRequestRecord;
import com.assignment.rohlik.domain.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

/**
 * Mapper for converting between Order entity and OrderRecord/OrderRequestRecord.
 */
@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    /**
     * Convert Order entity to OrderRecord.
     */
    @Mapping(target = "totalPrice", expression = "java(order.getTotalPrice())")
    OrderRecord toOrderRecord(Order order);
    
    /**
     * Extract product quantities map from OrderRequestRecord.
     */
    default Map<Long, Integer> toProductQuantitiesMap(OrderRequestRecord orderRequestRecord) {
        return orderRequestRecord.productQuantities();
    }
}

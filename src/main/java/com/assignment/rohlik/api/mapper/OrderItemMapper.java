package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.OrderItemRecord;
import com.assignment.rohlik.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for converting between OrderItem entity and OrderItemRecord.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);
    
    /**
     * Convert OrderItem entity to OrderItemRecord.
     */
    @Mapping(target = "totalPrice", expression = "java(orderItem.getTotalPrice())")
    OrderItemRecord toOrderItemRecord(OrderItem orderItem);
}

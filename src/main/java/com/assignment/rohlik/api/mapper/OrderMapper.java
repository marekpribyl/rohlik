package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.api.model.OrderItemDto;
import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto toApi(Order src);

    OrderItemDto toApi(OrderItem src);

}

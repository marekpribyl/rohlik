package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

import static com.assignment.rohlik.support.CollectionUtil.nullSafeStream;
import static java.util.stream.Collectors.toMap;

@Schema(description = "Order request with product quantities")
public record NewOrderDto(

        @Schema(description = "Order items")
        @Valid
        List<NewOrderItemDto> items

) {

    public Map<String, Integer> itemsAsMap() {
        return nullSafeStream(items)
                .collect(toMap(
                        NewOrderItemDto::sku,
                        NewOrderItemDto::quantity,
                        Integer::sum
                ));
    }

}

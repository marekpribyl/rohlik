package com.assignment.rohlik.infrastructure.persistence;


import com.assignment.rohlik.domain.model.ProductForOrder;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface ProductRepositoryExtension {

    //example: WITH items (sku, requested_quantity) as (values ('SKU-001',10)) select requested_quantity, p.* from items i left join products p on i.sku = p.sku
    Flux<ProductForOrder> findForOrder(Map<String, Integer> orderItems);

}

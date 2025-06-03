package com.assignment.rohlik.infrastructure.persistence;

import com.assignment.rohlik.domain.model.ProductForOrder;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryExtensionImpl implements ProductRepositoryExtension {

    private final DatabaseClient databaseClient;

    public ProductRepositoryExtensionImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    @Transactional
    public Flux<ProductForOrder> findForOrder(Map<String, Integer> orderItems) {
        if (CollectionUtils.isEmpty(orderItems)) {
            return Flux.empty();
        }

        String valuesSql = orderItems.entrySet().stream()
                .map(entry -> "('%s', %d)".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));

        String sql = """
                WITH items (sku, requested_quantity) as (values %s)
                SELECT i.requested_quantity, p.id, p.sku, p.name, p.stock_quantity, p.reserved_quantity, p.price
                FROM items i
                LEFT JOIN products p ON i.sku = p.sku
                """.formatted(valuesSql);

        // Execute the query and map the results to ProductForOrder objects
        return databaseClient.sql(sql)
                .map((row, metadata) -> {
                    ProductForOrder product = new ProductForOrder();
                    product.setId(row.get("id", Long.class));
                    product.setSku(row.get("sku", String.class));
                    product.setName(row.get("name", String.class));
                    product.setStockQuantity(row.get("stock_quantity", Integer.class));
                    product.setReservedQuantity(row.get("reserved_quantity", BigDecimal.class).intValue()); //FIXME NPE risk
                    product.setPrice(row.get("price", BigDecimal.class));
                    product.setRequestedQuantity(row.get("requested_quantity", Integer.class));
                    return product;
                })
                .all();
    }

}

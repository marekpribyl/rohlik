package com.assignment.rohlik.domain.model;

import com.assignment.rohlik.domain.InvalidOrderStateException;
import com.assignment.rohlik.infrastructure.persistence.WithEntityCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.assignment.rohlik.support.CollectionUtil.nullSafeStream;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Arrays.asList;
import static java.util.List.copyOf;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

@Table("orders")
public class Order implements WithEntityCallback<Order> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Id
    private Long id;

    @Column("order_number")
    private String orderNumber;

    @Column("status")
    private OrderStatus status;

    @Column("created_at")
    private OffsetDateTime createdAt;

    @Column("expires_at")
    private OffsetDateTime expiresAt;

    @Column("updated_at")
    private OffsetDateTime updatedAt;

    @Column("order_items")
    private String itemsJson;

    @Transient
    private List<OrderItem> items;


    public Order(List<OrderItem> items, Long expiresInMillis) {
        this.status = OrderStatus.CREATED;
        this.createdAt = OffsetDateTime.now();
        //TODO better order number generator
        Random random = new Random();
        this.orderNumber = createdAt.format(ofPattern("yyyyMMdd-HHmmssSSS-")) + String.format("%03d", random.nextInt(1000));
        this.expiresAt = this.createdAt.plus(expiresInMillis, MILLIS);
        this.items = copyOf(requireNonNull(items));
    }

    //TODO @PersistenceCreator
    public Order() {}

    public static Order fromProductsForOrder(List<ProductForOrder> items, Long expiresInMillis) {
        return new Order(items.stream()
            .map(OrderItem::fromProductForOrder)
            .toList(), expiresInMillis
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    private boolean isInTerminalState() {
        return !status.equals(OrderStatus.CREATED);
    }

    public Order toState(OrderStatus newStatus) {
        if (isInTerminalState()) {
            throw new InvalidOrderStateException("Cannot change status of an order in terminal state [%s]".formatted(status));
        }
        //TODO there is no guard for transition to EXPIRED based on expiresAt - do we want to enforce it here?
        this.status = newStatus;
        this.updatedAt = OffsetDateTime.now();
        return this;
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected Order readJson() {
        try {
            this.items = isNull(itemsJson) ? null : asList(MAPPER.readValue(itemsJson, OrderItem[].class));
            return this;
        } catch (IOException e ) {
            throw new RuntimeException(e);
        }
    }

    protected Order writeJson() {
        try {
            this.itemsJson = isNull(items) ? null : MAPPER.writer().writeValueAsString(items);
            return this;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Order> afterConvertCallback(SqlIdentifier tableName) {
        return Mono.just(readJson());
    }

    @Override
    public Mono<Order> beforeConvertCallback(SqlIdentifier tableName) {
        return Mono.just(writeJson());
    }

    public Map<String, Integer> itemsSkuAndQuantity() {
        return nullSafeStream(items)
                .collect(toMap(
                        OrderItem::getSku,
                        OrderItem::getQuantity,
                        Integer::sum
                ));
    }

}

package com.assignment.rohlik.domain.model;

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
import java.util.Random;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Arrays.asList;
import static java.util.List.copyOf;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Table("orders")
public class Order implements WithEntityCallback {

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


    // Constructors
    public Order(List<OrderItem> items) {
        this.status = OrderStatus.CREATED;
        this.createdAt = OffsetDateTime.now();
        //TODO order number generator
        Random random = new Random();
        this.orderNumber = createdAt.format(ofPattern("yyyyMMdd-HHmmssSSS-")) + String.format("%03d", random.nextInt(1000));
        this.expiresAt = this.createdAt.plusMinutes(30); //TODO configurable Orders expire after 30 minutes if unpaid
        this.items = copyOf(requireNonNull(items));
    }

    //TODO @PersistenceCreator
    public Order() {}

    public static Order fromProductsForOrder(List<ProductForOrder> items) {
        return new Order(items.stream()
            .map(OrderItem::fromProductForOrder)
            .toList()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void  setOrderNumber(final String orderNumber) {
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

    // Getters and setters for items
    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void paid() {
        this.status = OrderStatus.PAID;
        this.updatedAt = OffsetDateTime.now();
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void expire() {
        this.status = OrderStatus.EXPIRED;
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expiresAt) && status == OrderStatus.CREATED;
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
    public Mono afterConvertCallback(SqlIdentifier tableName) {
        return Mono.just(readJson());
    }

    @Override
    public Mono beforeConvertCallback(SqlIdentifier tableName) {
        return Mono.just(writeJson());
    }

}

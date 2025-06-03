package com.assignment.rohlik.domain;

import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderStatus;
import com.assignment.rohlik.domain.model.ProductForOrder;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.assignment.rohlik.domain.model.Order.fromProductsForOrder;
import static com.assignment.rohlik.domain.model.OrderStatus.CREATED;
import static com.assignment.rohlik.domain.model.OrderStatus.EXPIRED;

@Service
public class OrderProcessing {

    private static final Logger LOG = LoggerFactory.getLogger(OrderProcessing.class);

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final long expiresInMillis;


    public OrderProcessing(OrderRepository orderRepository, ProductRepository productRepository, @Value("${order.expiration.expiresMillis:1800000}") long expiresInMillis) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.expiresInMillis = expiresInMillis;
    }

    @Transactional
    public Mono<Order> createOrder(Map<String, Integer> items) {
        return productRepository.findForOrder(items)
                .map(ProductForOrder::reserveIfPossible)
                .flatMap(productForOrder -> productRepository
                        .save(productForOrder)
                        .then(Mono.just(productForOrder))
                )
                .collectList()
                .map(productsForOrder -> fromProductsForOrder(productsForOrder, expiresInMillis))
                .flatMap(orderRepository::save);
    }

    @Transactional
    public Mono<Order> statusChange(String orderNumber, OrderStatus targetStatus) {
        return orderRepository.findByOrderNumberAndStatus(orderNumber, CREATED)
                .map(order -> order.toState(targetStatus))
                .flatMap(this::inventoryClearance)
                .flatMap(orderRepository::save);
    }

    @Scheduled(fixedRateString = "${order.expiration.scheduler.rateMillis:60000}", initialDelayString = "${order.expiration.scheduler.initialDelayMillis:60000}")
    @Transactional
    public Mono<Void> expireOrders() {
        LOG.info("Checking for expired orders...");
        return orderRepository.findByStatusAndExpiresAtBefore(CREATED, OffsetDateTime.now())
                .map(order -> order.toState(EXPIRED))
                .flatMap(this::inventoryClearance)
                .flatMap(orderRepository::save)
                .doOnNext(order -> LOG.info("Order [{}] set as expired", order.getOrderNumber()))
                .then()
                .doFinally(signalType -> LOG.info("Expired orders check completed"));
    }

    private Mono<? extends Order> inventoryClearance(Order order) {
        Map<String, Integer> orderItems = order.itemsSkuAndQuantity();
        return productRepository.findForOrder(orderItems)
                //FIXME this is messy code hard to follow, refactor it
                .map(product -> order.getStatus().doOnProduct().apply(product, orderItems.get(product.getSku())))
                .flatMap(productRepository::save) //TODO this is suboptimal...
                .then(Mono.just(order));
    }

}

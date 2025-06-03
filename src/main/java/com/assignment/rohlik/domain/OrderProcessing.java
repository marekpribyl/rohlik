package com.assignment.rohlik.domain;

import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.OrderStatus;
import com.assignment.rohlik.domain.model.ProductForOrder;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.assignment.rohlik.domain.model.OrderStatus.CREATED;

@Service
public class OrderProcessing {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;


    public OrderProcessing(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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
                .map(Order::fromProductsForOrder)
                .flatMap(orderRepository::save);
    }

    @Transactional
    public Mono<Order> statusChange(String orderNumber, OrderStatus targetStatus) {
        return orderRepository.findByOrderNumberAndStatus(orderNumber, CREATED)
                .map(order -> order.toState(targetStatus))
                .flatMap(order -> productRepository.findForOrder(order.itemsSkuAndQuantity())
                        //FIXME this is messy code hard to follow, refactor it
                        .map(product -> order.getStatus().doOnProduct().apply(product, order.itemsSkuAndQuantity().get(product.getSku())))
                        .flatMap(productRepository::save) //TOOD this is suboptimal...
                        .then(Mono.just(order))
                )
                .flatMap(orderRepository::save);
    }

    @Transactional
    public Mono<Void> expireOrders() {
        //order must be in CREATED state and expired
        //release reserved quantity for each item in the order from inventory
        //update order status to EXPIRED and set updated_at to current time

        return Mono.error(new UnsupportedOperationException("Not yet implemented"));

        /*LocalDateTime now = LocalDateTime.now();
        return orderRepository.findByStatusAndExpiresAtLessThan(OrderStatus.CREATED, now)
                .flatMap(order -> {
                    // Release stock for each item in the order
                    return orderItemRepository.findByOrderId(order.getId())
                            .flatMap(item -> productService.increaseStock(item.getProductId(), item.getQuantity()))
                            .then(Mono.defer(() -> {
                                order.expire();
                                return orderRepository.save(order);
                            }));
                })
                .then();*/
    }
}

package com.assignment.rohlik.domain;

import com.assignment.rohlik.api.model.NewOrderItemDto;
import com.assignment.rohlik.domain.model.Order;
import com.assignment.rohlik.domain.model.Product;
import com.assignment.rohlik.domain.model.ProductForOrder;
import com.assignment.rohlik.infrastructure.persistence.OrderRepository;
import com.assignment.rohlik.infrastructure.persistence.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Mono<Order> createOrder(List<NewOrderItemDto> items) {
        Map<String, Integer> itemsAsMap = items.stream()
                .collect(Collectors.toMap(
                        NewOrderItemDto::sku,
                        NewOrderItemDto::quantity,
                        Integer::sum
                ));

        return productRepository.findForOrder(itemsAsMap)
                .map(ProductForOrder::reserveIfPossible)
                .flatMap(productForOrder -> productRepository
                        .save(productForOrder)
                        .then(Mono.just(productForOrder))
                )
                .collectList()
                .map(Order::fromProductsForOrder)
                .flatMap(orderRepository::save);
    }

    private Mono<List<Product>> checkProductsAvailability(Map<Long, Integer> productQuantities) {
        List<Mono<Product>> productMonos = productQuantities.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();

                    return productRepository.findById(productId)
                            .filter(product -> product.getAvailableQuantity() >= quantity)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found or not enough stock: " + productId)));
                })
                .collect(Collectors.toList());

        return Flux.concat(productMonos)
                .collectList()
                .map(products -> products.stream()
                        .filter(product -> product.getAvailableQuantity() < productQuantities.get(product.getId()))
                        .collect(Collectors.toList()));
    }

    @Transactional
    public Mono<Order> payOrder(Long id) {
        //order must be in CREATED state and not expired
        //remove reserved quantity for each item in the order from inventory
        //update order status to PAID

        return Mono.error(new UnsupportedOperationException("Not yet implemented"));

        /*return orderRepository.findById(id)
                .flatMap(order -> {
                    if (order.getStatus() != OrderStatus.CREATED) {
                        return Mono.error(new InvalidOrderStateException(order, OrderStatus.CREATED));
                    }

                    if (order.isExpired()) {
                        return Mono.error(new InvalidOrderStateException(order, "Order has expired"));
                    }

                    LocalDateTime now = LocalDateTime.now();
                    return orderRepository.markAsPaid(id, OrderStatus.PAID, now)
                            .flatMap(updated -> {
                                if (updated > 0) {
                                    order.paid();
                                    return loadOrderItems(order);
                                }
                                return Mono.error(new IllegalStateException("Failed to pay order"));
                            });
                });*/
    }

    @Transactional
    public Mono<Order> cancelOrder(Long id) {
        //order must be in CREATED state
        //release reserved quantity for each item in the order from inventory
        //update order status to CANCELED and set updated_at to current time

        return Mono.error(new UnsupportedOperationException("Not yet implemented"));

        /*return orderRepository.findById(id)
                .flatMap(order -> {
                    if (order.getStatus() != OrderStatus.CREATED) {
                        return Mono.error(new InvalidOrderStateException(order, OrderStatus.CREATED));
                    }

                    LocalDateTime now = LocalDateTime.now();
                    return orderRepository.cancel(id, OrderStatus.CANCELED, now)
                            .flatMap(updated -> {
                                if (updated > 0) {
                                    // Load order items to release stock
                                    return orderItemRepository.findByOrderId(id)
                                            .flatMap(item -> productService.increaseStock(item.getProductId(), item.getQuantity()))
                                            .then(Mono.defer(() -> {
                                                order.cancel();
                                                return loadOrderItems(order);
                                            }));
                                }
                                return Mono.error(new IllegalStateException("Failed to cancel order"));
                            });
                });*/
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

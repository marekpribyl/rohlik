package com.assignment.rohlik.service;

import com.assignment.rohlik.model.Order;
import com.assignment.rohlik.model.OrderItem;
import com.assignment.rohlik.model.OrderStatus;
import com.assignment.rohlik.model.Product;
import com.assignment.rohlik.exception.InsufficientStockException;
import com.assignment.rohlik.exception.InvalidOrderStateException;
import com.assignment.rohlik.repository.OrderItemRepository;
import com.assignment.rohlik.repository.OrderRepository;
import com.assignment.rohlik.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .flatMap(this::loadOrderItems);
    }

    private Mono<Order> loadOrderItems(Order order) {
        return orderItemRepository.findByOrderId(order.getId())
                .collectList()
                .doOnNext(order::setItems)
                .thenReturn(order);
    }

    @Transactional
    public Mono<Order> createOrder(Map<Long, Integer> productQuantities) {
        // Check if all products exist and have enough stock
        return checkProductsAvailability(productQuantities)
                .flatMap(missingProducts -> {
                    if (!missingProducts.isEmpty()) {
                        return Mono.error(new InsufficientStockException(missingProducts));
                    }

                    // Create the order
                    Order order = new Order();
                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                // Create order items and decrease stock
                                List<Mono<OrderItem>> orderItemMonos = productQuantities.entrySet().stream()
                                        .map(entry -> {
                                            Long productId = entry.getKey();
                                            Integer quantity = entry.getValue();

                                            return productRepository.findById(productId)
                                                    .flatMap(product -> {
                                                        // Decrease stock
                                                        return productService.decreaseStock(productId, quantity)
                                                                .flatMap(decreased -> {
                                                                    if (!decreased) {
                                                                        return Mono.error(new IllegalStateException("Failed to decrease stock for product: " + product.getName()));
                                                                    }

                                                                    // Create order item
                                                                    OrderItem orderItem = new OrderItem(savedOrder.getId(), productId, quantity, product.getPrice());
                                                                    return orderItemRepository.save(orderItem);
                                                                });
                                                    });
                                        })
                                        .collect(Collectors.toList());

                                return Flux.concat(orderItemMonos)
                                        .collectList()
                                        .flatMap(orderItems -> {
                                            savedOrder.setItems(orderItems);
                                            return Mono.just(savedOrder);
                                        });
                            });
                });
    }

    private Mono<List<Product>> checkProductsAvailability(Map<Long, Integer> productQuantities) {
        List<Mono<Product>> productMonos = productQuantities.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    Integer quantity = entry.getValue();

                    return productRepository.findById(productId)
                            .filter(product -> product.getStockQuantity() >= quantity)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found or not enough stock: " + productId)));
                })
                .collect(Collectors.toList());

        return Flux.concat(productMonos)
                .collectList()
                .map(products -> products.stream()
                        .filter(product -> product.getStockQuantity() < productQuantities.get(product.getId()))
                        .collect(Collectors.toList()));
    }

    @Transactional
    public Mono<Order> payOrder(Long id) {
        return orderRepository.findById(id)
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
                                    order.markAsPaid();
                                    return loadOrderItems(order);
                                }
                                return Mono.error(new IllegalStateException("Failed to pay order"));
                            });
                });
    }

    @Transactional
    public Mono<Order> cancelOrder(Long id) {
        return orderRepository.findById(id)
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
                });
    }

    @Transactional
    public Mono<Void> expireOrders() {
        LocalDateTime now = LocalDateTime.now();
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
                .then();
    }
}

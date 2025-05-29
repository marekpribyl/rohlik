package com.assignment.rohlik.scheduler;

import com.assignment.rohlik.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderExpirationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderExpirationScheduler.class);
    
    private final OrderService orderService;

    @Autowired
    public OrderExpirationScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void expireOrders() {
        logger.info("Checking for expired orders...");
        orderService.expireOrders()
                .subscribe(
                        null,
                        error -> logger.error("Error while expiring orders", error),
                        () -> logger.info("Expired orders check completed")
                );
    }
}

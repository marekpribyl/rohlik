package com.assignment.rohlik.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderExpirationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OrderExpirationScheduler.class);
    
    private final OrderProcessing orderService;

    @Autowired
    public OrderExpirationScheduler(OrderProcessing orderService) {
        this.orderService = orderService;
    }

    //@Scheduled(fixedRate = 60000) // Run every minute
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

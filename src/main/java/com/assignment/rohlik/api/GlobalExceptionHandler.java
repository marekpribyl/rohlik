package com.assignment.rohlik.api;

import com.assignment.rohlik.domain.InsufficientStockException;
import com.assignment.rohlik.domain.InvalidOrderStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InsufficientStockException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleInsufficientStockException(InsufficientStockException ex) {
        LOG.error("Insufficient stock for products: {}", ex.getProductsWithInsufficientStock());
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("products", ex.getProductsWithInsufficientStock().stream()
                .map(p -> Map.of(
                    "id", p.getId(),
                    "name", p.getName()
                ))
                .collect(Collectors.toList()));

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public Mono<ResponseEntity<String>> handleInvalidOrderStateException(InvalidOrderStateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        LOG.error("Invalid argument: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalStateException(IllegalStateException ex) {
        LOG.error("Invalid state: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(body));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(WebExchangeBindException ex) {
        LOG.error("Validation error: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        body.put("errors", errors);

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        LOG.error("Unexpected error: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "An unexpected error occurred");

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
    }

}

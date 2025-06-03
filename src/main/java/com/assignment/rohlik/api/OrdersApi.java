package com.assignment.rohlik.api;

import com.assignment.rohlik.api.model.NewOrderDto;
import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.api.model.OrderStatusUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Orders", description = "Orders management API")
@RequestMapping(value = "/api/orders/v1", produces = "application/json")
public interface OrdersApi {

    @Operation(summary = "Create a new order", description = "Creates a new order with the given products and quantities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ResponseEntity<OrderDto>> createOrder(
            @Parameter(description = "Order to create", required = true)
            @Valid @RequestBody NewOrderDto newOrder);

    @Operation(summary = "Get order by ID", description = "Returns a single order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderNumber}")
    Mono<ResponseEntity<OrderDto>> getOrderByOrderNumber(
            @Parameter(description = "Order number to retrieve", required = true)
            @PathVariable String orderNumber
    );

    @Operation(summary = "Update order status", description = "To be used for payment, cancellation or expiration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "409", description = "Transition to the provided state is not allowed"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{orderNumber}/status/{status}")
    Mono<ResponseEntity<Void>> updateStatus(@PathVariable String orderNumber, @PathVariable OrderStatusUpdateDto status);

}

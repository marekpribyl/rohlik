package com.assignment.rohlik.api;

import com.assignment.rohlik.api.model.OrderDto;
import com.assignment.rohlik.api.model.OrderRequestRecord;
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
@RequestMapping(value = "/api/orders/v1", produces = "application/json", consumes = "application/json")
public interface OrdersApi {

    @Operation(summary = "Get order by ID", description = "Returns a single order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    Mono<ResponseEntity<OrderDto>> getOrderById(
            @Parameter(description = "ID of the order to retrieve", required = true) 
            @PathVariable Long id);

    @Operation(summary = "Create a new order", description = "Creates a new order with the given products and quantities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully", 
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or insufficient stock"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<ResponseEntity<OrderDto>> createOrder(
            @Parameter(description = "Order to create", required = true) 
            @Valid @RequestBody OrderRequestRecord orderRequestRecord);

    @Operation(summary = "Pay for an order", description = "Marks an order as paid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order paid successfully", 
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order is in invalid state or has expired"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/pay")
    Mono<ResponseEntity<OrderDto>> payOrder(
            @Parameter(description = "ID of the order to pay", required = true) 
            @PathVariable Long id);

    @Operation(summary = "Cancel an order", description = "Cancels an order and releases reserved stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order canceled successfully", 
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order is in invalid state"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{id}/cancel")
    Mono<ResponseEntity<OrderDto>> cancelOrder(
            @Parameter(description = "ID of the order to cancel", required = true) 
            @PathVariable Long id);
}

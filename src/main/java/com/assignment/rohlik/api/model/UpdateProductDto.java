package com.assignment.rohlik.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

import static com.assignment.rohlik.domain.model.Product.PRODUCT_NAME_PATTERN;

@Schema(description = "Product update")
public record UpdateProductDto(

    @Schema(description = "Name of the product", example = "Organic Banana")
    @NotBlank(message = "Product name is required")
    @Pattern(regexp = PRODUCT_NAME_PATTERN, message = "Product name must be alphanumeric and less than or equal to 250 characters")
    String name,

    @Schema(description = "Price per unit", example = "1.99")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    BigDecimal price

) {}

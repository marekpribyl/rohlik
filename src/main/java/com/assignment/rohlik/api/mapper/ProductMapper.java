package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.UpdateProductDto;
import com.assignment.rohlik.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for converting between Product entity and ProductRecord/ProductRequestRecord.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    
    /**
     * Convert Product entity to ProductRecord.
     */
    ProductDto toProductRecord(Product product);
    
    /**
     * Convert ProductRequestRecord to Product entity.
     */
    Product toProduct(NewProductDto productRequestRecord);
    
    /**
     * Update Product entity from ProductRequestRecord.
     */
    void updateProductFromRequest(UpdateProductDto productRequestRecord, @MappingTarget Product product);

}

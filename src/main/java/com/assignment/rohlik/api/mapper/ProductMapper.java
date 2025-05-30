package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.ProductRecord;
import com.assignment.rohlik.api.model.ProductRequestRecord;
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
    ProductRecord toProductRecord(Product product);
    
    /**
     * Convert ProductRequestRecord to Product entity.
     */
    Product toProduct(ProductRequestRecord productRequestRecord);
    
    /**
     * Update Product entity from ProductRequestRecord.
     */
    void updateProductFromRequest(ProductRequestRecord productRequestRecord, @MappingTarget Product product);
}

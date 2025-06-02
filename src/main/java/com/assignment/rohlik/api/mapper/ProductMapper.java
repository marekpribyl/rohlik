package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.StockInfoDto;
import com.assignment.rohlik.api.model.UpdateProductDto;
import com.assignment.rohlik.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "stockInfo", source = "product", qualifiedByName = "toStockInfoDto")
    ProductDto toApi(Product product);

    @Mapping(target = "stockQuantity", source = "initialStockQuantity")
    Product fromApi(NewProductDto productRequestRecord);
    
    void updateProduct(UpdateProductDto productRequestRecord, @MappingTarget Product product);

    @Named("toStockInfoDto")
    default StockInfoDto toStockInfoDto(Product product) {
        return new StockInfoDto(product.getStockQuantity(), product.getAvailableQuantity());
    }

}

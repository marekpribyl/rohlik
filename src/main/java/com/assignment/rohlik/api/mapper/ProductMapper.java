package com.assignment.rohlik.api.mapper;

import com.assignment.rohlik.api.model.NewProductDto;
import com.assignment.rohlik.api.model.ProductDto;
import com.assignment.rohlik.api.model.StockInfoDto;
import com.assignment.rohlik.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "stockInfo", source = "product", qualifiedByName = "toStockInfoDto")
    ProductDto toApi(Product product);

    @Mapping(target = "stockQuantity", source = "initialStockQuantity")
    Product fromApi(NewProductDto productRequestRecord);

    @Named("toStockInfoDto")
    default StockInfoDto toStockInfoDto(Product product) {
        return new StockInfoDto(product.getStockQuantity(), product.getAvailableQuantity());
    }

}

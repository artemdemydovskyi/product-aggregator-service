package com.mycompany.product.product_aggregator_service.domain.mapper;

import com.mycompany.product.product_aggregator_service.domain.dto.ProductAggregationDto;
import com.mycompany.product.product_aggregator_service.domain.responses.ProductAggregationResponse;

public final class ProductAggregationMapper {

    private ProductAggregationMapper() {
    }

    public static ProductAggregationResponse toResponse(ProductAggregationDto dto) {
        if (dto == null) {
            return null;
        }

        return new ProductAggregationResponse(
                dto.productId(),
                dto.market(),
                dto.catalog(),
                dto.pricing(),
                dto.availability(),
                dto.customer()
        );
    }
}
package com.mycompany.product.product_aggregator_service.service.app;

import com.mycompany.product.product_aggregator_service.domain.dto.ProductAggregationDto;

public interface ProductAggregationService {

    ProductAggregationDto aggregate(String productId, String market, String customerId);
}

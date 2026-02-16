package com.mycompany.product.product_aggregator_service.domain.dto;

import com.mycompany.product.product_aggregator_service.domain.responses.AvailabilityResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.CatalogResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.CustomerResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.PricingResponse;

public record ProductAggregationDto(
        String productId,
        String market,
        CatalogResponse catalog,
        PricingResponse pricing,
        AvailabilityResponse availability,
        CustomerResponse customer) {
}

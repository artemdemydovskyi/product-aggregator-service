package com.mycompany.product.product_aggregator_service.domain.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductAggregationResponse(
        String productId,
        String market,

        CatalogResponse catalog,
        PricingResponse pricing,
        AvailabilityResponse availability,
        CustomerResponse customer) {
}

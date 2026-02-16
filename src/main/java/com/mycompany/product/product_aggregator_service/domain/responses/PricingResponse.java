package com.mycompany.product.product_aggregator_service.domain.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PricingResponse(
        String sku,
        String currency,
        BigDecimal basePrice,
        BigDecimal customerDiscount,
        BigDecimal finalPrice,
        String status
) {
    public static PricingResponse unavailable(String sku) {
        return new PricingResponse(
                sku,
                null,
                null,
                null,
                null,
                "UNAVAILABLE"
        );
    }
}

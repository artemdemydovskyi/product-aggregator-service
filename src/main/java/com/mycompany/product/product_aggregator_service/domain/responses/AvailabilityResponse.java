package com.mycompany.product.product_aggregator_service.domain.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AvailabilityResponse(
        String sku,
        Integer stockLevel,
        String warehouseLocation,
        LocalDate expectedDelivery,
        String status
) {
    public static AvailabilityResponse unknown(String sku) {
        return new AvailabilityResponse(
                sku,
                null,
                null,
                null,
                "UNKNOWN"
        );
    }
}

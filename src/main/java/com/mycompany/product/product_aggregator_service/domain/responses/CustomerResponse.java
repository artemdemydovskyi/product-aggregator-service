package com.mycompany.product.product_aggregator_service.domain.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerResponse(
        String customerId,
        String segment,
        Map<String, Object> preferences
) {
}

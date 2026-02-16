package com.mycompany.product.product_aggregator_service.domain.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogResponse(
        String sku,
        String name,
        String description,
        Map<String, String> specs,
        List<String> images
) {}

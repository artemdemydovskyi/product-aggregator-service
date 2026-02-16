package com.mycompany.product.product_aggregator_service.controller;

import com.mycompany.product.product_aggregator_service.domain.dto.ProductAggregationDto;
import com.mycompany.product.product_aggregator_service.domain.mapper.ProductAggregationMapper;
import com.mycompany.product.product_aggregator_service.domain.responses.ProductAggregationResponse;
import com.mycompany.product.product_aggregator_service.service.app.ProductAggregationService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.products.path}")
@RequiredArgsConstructor
@Validated
public class ProductAggregationController {

    private final ProductAggregationService aggregationService;

    /**
     * Example:
     * GET /api/products/ABC123?market=nl-NL&customerId=42
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductAggregationResponse> getProduct(
            @PathVariable
            @NotBlank(message = "productId must not be blank")
            String productId,

            @RequestParam
            @NotBlank(message = "market must not be blank")
            @Pattern(
                    regexp = "^[a-z]{2}-[A-Z]{2}$",
                    message = "market must look like nl-NL, sv-DE, pl-PL"
            )
            String market,

            @RequestParam(required = false)
            String customerId
    ) {
        ProductAggregationDto dto = aggregationService.aggregate(productId, market, customerId);
        return ResponseEntity.ok(ProductAggregationMapper.toResponse(dto));
    }
}
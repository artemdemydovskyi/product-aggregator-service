package com.mycompany.product.product_aggregator_service.service.api;

import com.mycompany.product.product_aggregator_service.config.CommonFeignConfig;
import com.mycompany.product.product_aggregator_service.domain.responses.PricingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "pricing",
        url = "${client.pricing.url}",
        configuration = CommonFeignConfig.class
)
public interface PricingApiClient {
    @GetMapping("/v1/pricing/products/{productId}")
    PricingResponse getPrice(
            @PathVariable("productId") String productId,
            @RequestParam("market") String market,
            @RequestParam(value = "customerId", required = false) String customerId
    );
}


package com.mycompany.product.product_aggregator_service.service.api;

import com.mycompany.product.product_aggregator_service.config.CommonFeignConfig;
import com.mycompany.product.product_aggregator_service.domain.responses.AvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "availability",
        url = "${client.availability.url}",
        configuration = CommonFeignConfig.class
)
public interface AvailabilityApiClient {
    @GetMapping("/v1/availability/products/{productId}")
    AvailabilityResponse getAvailability(
            @PathVariable("productId") String productId,
            @RequestParam("market") String market
    );
}

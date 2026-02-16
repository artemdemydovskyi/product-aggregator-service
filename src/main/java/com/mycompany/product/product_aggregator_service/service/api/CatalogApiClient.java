package com.mycompany.product.product_aggregator_service.service.api;

import com.mycompany.product.product_aggregator_service.config.CommonFeignConfig;
import com.mycompany.product.product_aggregator_service.domain.responses.CatalogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "catalog",
        url = "${client.catalog.url}",
        configuration = CommonFeignConfig.class
)
public interface CatalogApiClient {
    @GetMapping("/v1/catalog/products/{productId}")
    CatalogResponse getCatalog(
            @PathVariable("productId") String productId,
            @RequestParam("market") String market
    );
}

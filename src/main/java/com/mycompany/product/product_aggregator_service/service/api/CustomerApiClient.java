package com.mycompany.product.product_aggregator_service.service.api;

import com.mycompany.product.product_aggregator_service.config.CommonFeignConfig;
import com.mycompany.product.product_aggregator_service.domain.responses.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "customer",
        url = "${client.customer.url}",
        configuration = CommonFeignConfig.class
)
public interface CustomerApiClient {
    @GetMapping("/v1/customers/{customerId}")
    CustomerResponse getCustomer(
            @PathVariable("customerId") String customerId,
            @RequestParam("market") String market
    );
}

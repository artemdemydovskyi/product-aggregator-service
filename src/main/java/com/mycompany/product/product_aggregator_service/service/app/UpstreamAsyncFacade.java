package com.mycompany.product.product_aggregator_service.service.app;

import com.mycompany.product.product_aggregator_service.domain.responses.*;
import com.mycompany.product.product_aggregator_service.service.api.AvailabilityApiClient;
import com.mycompany.product.product_aggregator_service.service.api.CustomerApiClient;
import com.mycompany.product.product_aggregator_service.service.api.PricingApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class UpstreamAsyncFacade {

    private final PricingApiClient pricingClient;
    private final AvailabilityApiClient availabilityClient;
    private final CustomerApiClient customerClient;

    @Async
    public CompletableFuture<PricingResponse> pricing(String sku, String market, String customerId) {
        try {
            return CompletableFuture.completedFuture(pricingClient.getPrice(sku, market, customerId));
        } catch (Exception ex) {
            return CompletableFuture.completedFuture(PricingResponse.unavailable(sku));
        }
    }

    @Async
    public CompletableFuture<AvailabilityResponse> availability(String sku, String market) {
        try {
            return CompletableFuture.completedFuture(availabilityClient.getAvailability(sku, market));
        } catch (Exception ex) {
            return CompletableFuture.completedFuture(AvailabilityResponse.unknown(sku));
        }
    }

    @Async
    public CompletableFuture<CustomerResponse> customer(String customerId, String market) {
        if (customerId == null || customerId.isBlank()) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            return CompletableFuture.completedFuture(customerClient.getCustomer(customerId, market));
        } catch (Exception ex) {
            return CompletableFuture.completedFuture(null);
        }
    }
}

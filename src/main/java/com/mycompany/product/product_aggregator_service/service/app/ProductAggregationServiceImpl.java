package com.mycompany.product.product_aggregator_service.service.app;

import com.mycompany.product.product_aggregator_service.domain.dto.ProductAggregationDto;
import com.mycompany.product.product_aggregator_service.domain.responses.AvailabilityResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.CatalogResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.CustomerResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.PricingResponse;
import com.mycompany.product.product_aggregator_service.service.api.CatalogApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ProductAggregationServiceImpl implements ProductAggregationService {

    private final CatalogApiClient catalogClient;
    private final UpstreamAsyncFacade upstreamAsyncFacade;

    @Override
    public ProductAggregationDto  aggregate(String sku, String market, String customerId) {
        CatalogResponse catalogResponse = catalogClient.getCatalog(sku, market);

        CompletableFuture<PricingResponse> pricingResponseFuture =
                safelyCallAsync(() -> upstreamAsyncFacade.pricing(sku, market, customerId));

        CompletableFuture<AvailabilityResponse> availabilityResponseFuture =
                safelyCallAsync(() -> upstreamAsyncFacade.availability(sku, market));

        CompletableFuture<CustomerResponse> customerResponseFuture =
                safelyCallAsync(() -> upstreamAsyncFacade.customer(customerId, market));

        PricingResponse pricingResponse = pricingResponseFuture.join();
        AvailabilityResponse availabilityResponse = availabilityResponseFuture.join();
        CustomerResponse customerResponse = customerResponseFuture.join();

        return new ProductAggregationDto(
                sku,
                market,
                catalogResponse,
                pricingResponse,
                availabilityResponse,
                customerResponse
        );

    }

    private static <T> CompletableFuture<T> safelyCallAsync(Supplier<CompletableFuture<T>> asyncCall) {
        try {
            return asyncCall.get().exceptionally(exception -> null);
        } catch (Exception exception) {
            return CompletableFuture.completedFuture(null);
        }
    }
}

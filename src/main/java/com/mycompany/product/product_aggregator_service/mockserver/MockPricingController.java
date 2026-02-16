package com.mycompany.product.product_aggregator_service.mockserver;

import com.mycompany.product.product_aggregator_service.domain.responses.PricingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("mock-upstreams")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pricing")
public class MockPricingController {

    private static final String SERVICE_NAME = "pricing";

    private final ClientMockProperties props;
    private final ClientSimulator simulator;
    private final MockFixtureStore fixtures;

    @GetMapping("/products/{sku}")
    public PricingResponse getPrice(
            @PathVariable("sku") String sku,
            @RequestParam("market") String market,
            @RequestParam(value = "customerId", required = false) String customerId
    ) {
        var serviceProps = props.pricing();
        simulator.simulateCall(SERVICE_NAME, serviceProps.baseLatency(), serviceProps.jitter(), serviceProps.reliability());

        if (customerId == null || customerId.isBlank()) {
            return fixtures.readPricing(sku, market);
        }

        return fixtures.readPricingForCustomer(sku, customerId, market);
    }
}

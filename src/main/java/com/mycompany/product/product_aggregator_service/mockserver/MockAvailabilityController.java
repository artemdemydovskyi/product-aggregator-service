package com.mycompany.product.product_aggregator_service.mockserver;

import com.mycompany.product.product_aggregator_service.domain.responses.AvailabilityResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("mock-upstreams")
@RestController
@RequestMapping("/v1/availability")
public class MockAvailabilityController {

    private final ClientMockProperties props;
    private final ClientSimulator simulator;
    private final MockFixtureStore fixtures;

    public MockAvailabilityController(
            ClientMockProperties props,
            ClientSimulator simulator,
            MockFixtureStore fixtures
    ) {
        this.props = props;
        this.simulator = simulator;
        this.fixtures = fixtures;
    }

    @GetMapping("/products/{sku}")
    public AvailabilityResponse getAvailability(
            @PathVariable String sku,
            @RequestParam String market
    ) {
        var p = props.availability();
        simulator.simulateCall("availability", p.baseLatency(), p.jitter(), p.reliability());

        return fixtures.readAvailability(sku, market);
    }
}
package com.mycompany.product.product_aggregator_service.mockserver;

import com.mycompany.product.product_aggregator_service.domain.responses.CustomerResponse;
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
@RequestMapping("/v1/customers")
public class MockCustomerController {

    private static final String SERVICE_NAME = "customer";

    private final ClientMockProperties props;
    private final ClientSimulator simulator;
    private final MockFixtureStore fixtures;

    @GetMapping("/{customerId}")
    public CustomerResponse getCustomer(
            @PathVariable String customerId,
            @RequestParam String market
    ) {
        var serviceProps = props.customer();
        simulator.simulateCall(SERVICE_NAME, serviceProps.baseLatency(), serviceProps.jitter(), serviceProps.reliability());

        return fixtures.readCustomer(customerId, market);
    }
}

package com.mycompany.product.product_aggregator_service.mockserver;

import com.mycompany.product.product_aggregator_service.domain.responses.CatalogResponse;
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
@RequestMapping("/v1/catalog")
public class MockCatalogController {

    private static final String SERVICE_NAME = "catalog";

    private final ClientMockProperties props;
    private final ClientSimulator clientSimulator;
    private final MockFixtureStore fixtures;

    @GetMapping("/products/{sku}")
    public CatalogResponse getCatalog(
            @PathVariable("sku") String sku,
            @RequestParam("market") String market
    ) {
        var serviceProps = props.catalog();
        clientSimulator.simulateCall(SERVICE_NAME, serviceProps.baseLatency(), serviceProps.jitter(),serviceProps.reliability());

        return fixtures.readCatalog(sku, market);
    }
}

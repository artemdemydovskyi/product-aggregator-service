package com.mycompany.product.product_aggregator_service.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.product.product_aggregator_service.domain.responses.ProductAggregationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Suite
@SelectClasses({
        ProductAggregationIntegrationTest.HappyPath.class,
        ProductAggregationIntegrationTest.GracefulDegradation.class,
        ProductAggregationIntegrationTest.CatalogFailure.class
})
public class ProductAggregationIntegrationTest {

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
    @ActiveProfiles("mock-upstreams")
    @TestPropertySource(properties = {
            "client.catalog.base-latency=0",
            "client.catalog.jitter=0",
            "client.catalog.reliability=1.0",

            "client.pricing.base-latency=0",
            "client.pricing.jitter=0",
            "client.pricing.reliability=1.0",

            "client.availability.base-latency=0",
            "client.availability.jitter=0",
            "client.availability.reliability=1.0",

            "client.customer.base-latency=0",
            "client.customer.jitter=0",
            "client.customer.reliability=1.0"
    })
    public static class HappyPath {

        private static final int PORT = findFreePort();
        private static final String BASE_URL = "http://localhost:" + PORT;

        @Autowired
        ObjectMapper objectMapper;

        @DynamicPropertySource
        static void props(DynamicPropertyRegistry r) {
            r.add("server.port", () -> String.valueOf(PORT));

            r.add("client.catalog.url", () -> BASE_URL);
            r.add("client.pricing.url", () -> BASE_URL);
            r.add("client.availability.url", () -> BASE_URL);
            r.add("client.customer.url", () -> BASE_URL);
        }

        private RestClient rest() {
            return RestClient.builder().baseUrl(BASE_URL).build();
        }

        @ParameterizedTest
        @MethodSource("com.mycompany.product.product_aggregator_service.it.ProductAggregationIntegrationTest#happyPathCases")
        void shouldReturnFullyAggregatedProductWhenAllUpstreamsAvailable(Case scenario) throws Exception {
            String json = rest().get().uri(buildUrl(scenario)).retrieve().body(String.class);

            ProductAggregationResponse response =
                    objectMapper.readValue(json, ProductAggregationResponse.class);

            assertThat(response).isNotNull();
            assertThat(response.productId()).isEqualTo(scenario.sku());
            assertThat(response.catalog()).isNotNull();
            assertThat(response.pricing()).isNotNull();
            assertThat(response.availability()).isNotNull();
            assertThat(response.customer()).isNotNull();
        }
    }

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
    @ActiveProfiles("mock-upstreams")
    @TestPropertySource(properties = {
            "client.catalog.reliability=1.0",
            "client.pricing.reliability=0.0",
            "client.availability.reliability=0.0",
            "client.customer.reliability=0.0",

            "client.catalog.base-latency=0",
            "client.catalog.jitter=0",
            "client.pricing.base-latency=0",
            "client.pricing.jitter=0",
            "client.availability.base-latency=0",
            "client.availability.jitter=0",
            "client.customer.base-latency=0",
            "client.customer.jitter=0"
    })
    public static class GracefulDegradation {

        private static final int PORT = findFreePort();
        private static final String BASE_URL = "http://localhost:" + PORT;

        @Autowired
        ObjectMapper objectMapper;

        @DynamicPropertySource
        static void props(DynamicPropertyRegistry r) {
            r.add("server.port", () -> String.valueOf(PORT));

            r.add("client.catalog.url", () -> BASE_URL);
            r.add("client.pricing.url", () -> BASE_URL);
            r.add("client.availability.url", () -> BASE_URL);
            r.add("client.customer.url", () -> BASE_URL);
        }

        private RestClient rest() {
            return RestClient.builder().baseUrl(BASE_URL).build();
        }

        @Test
        void shouldReturnProductWhenOptionalUpstreamsFail() throws Exception {
            String json = rest().get()
                    .uri("/api/products/DRL-18V-001?market=en-EN&customerId=CUST-1001")
                    .retrieve()
                    .body(String.class);

            var response = objectMapper.readValue(json, ProductAggregationResponse.class);

            assertThat(response.catalog()).isNotNull();

            assertThat(response.pricing()).isNotNull();
            assertThat(response.pricing().status()).isEqualTo("UNAVAILABLE");

            assertThat(response.availability()).isNotNull();
            assertThat(response.availability().status()).isEqualTo("UNKNOWN");

            assertThat(response.customer()).isNull();
        }

        @Test
        void shouldReturnNonPersonalizedWhenCustomerIdNotProvided() throws Exception {
            String json = rest().get()
                    .uri("/api/products/GRN-125-002?market=nl-NL")
                    .retrieve()
                    .body(String.class);

            var response = objectMapper.readValue(json, ProductAggregationResponse.class);

            assertThat(response.catalog()).isNotNull();
            assertThat(response.customer()).isNull();
        }
    }

    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("mock-upstreams")
    @TestPropertySource(properties = {
            "client.pricing.url=http://localhost:18082",
            "client.availability.url=http://localhost:18083",
            "client.customer.url=http://localhost:18084",
            "client.catalog.url=http://localhost:59999"
    })
    public static class CatalogFailure {

        @LocalServerPort
        int port;

        private RestClient rest() {
            return RestClient.builder()
                    .baseUrl("http://localhost:" + port)
                    .build();
        }

        @Test
        void shouldFailRequestWhenCatalogFails() {
            HttpStatusCode status = rest().get()
                    .uri("/api/products/DRL-18V-001?market=en-EN")
                    .exchange((req, res) -> res.getStatusCode());

            assertThat(status.is4xxClientError() || status.is5xxServerError()).isTrue();
        }
    }

    record Case(String sku, String market, String customerId) {
    }

    static Stream<Case> happyPathCases() {
        return Stream.of(
                new Case("DRL-18V-001", "nl-NL", "CUST-1001"),
                new Case("DRL-18V-001", "sv-SE", "CUST-1001"),
                new Case("DRL-18V-001", "en-EN", "CUST-1001"),
                new Case("GRN-125-002", "nl-NL", "CUST-1001"),
                new Case("GRN-125-002", "sv-SE", "CUST-1001"),
                new Case("GRN-125-002", "en-EN", "CUST-1001")
        );
    }

    static String buildUrl(Case scenario) {
        String url = "/api/products/" + scenario.sku() + "?market=" + scenario.market();
        if (scenario.customerId() != null && !scenario.customerId().isBlank()) {
            url += "&customerId=" + scenario.customerId();
        }
        return url;
    }

    static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot find free port", e);
        }
    }
}
package com.mycompany.product.product_aggregator_service.mockserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.product.product_aggregator_service.domain.responses.AvailabilityResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.CatalogResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.CustomerResponse;
import com.mycompany.product.product_aggregator_service.domain.responses.PricingResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockFixtureStore {

    private static final String PRICING_DIR = "pricing/";
    private static final String CATALOG_DIR = "catalog/";
    private static final String AVAILABILITY_DIR = "availability/";
    private static final String CUSTOMER_DIR = "customer/";

    private static final String JSON_EXTENSION = ".json";
    private static final String FILE_SEPARATOR = "_";
    private static final String FIXTURE_ROOT = "mock-data/";

    private final ObjectMapper objectMapper;

    private final Map<String, CatalogResponse> catalogCache = new ConcurrentHashMap<>();
    private final Map<String, PricingResponse> pricingCache = new ConcurrentHashMap<>();
    private final Map<String, CustomerResponse> customerCache = new ConcurrentHashMap<>();
    private final Map<String, AvailabilityResponse> availabilityCache = new ConcurrentHashMap<>();

    public MockFixtureStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AvailabilityResponse readAvailability(String sku, String market) {
        String filename = sku + FILE_SEPARATOR + market + JSON_EXTENSION;
        String path = FIXTURE_ROOT + AVAILABILITY_DIR + filename;

        return availabilityCache.computeIfAbsent(path,
                p -> read(p, AvailabilityResponse.class));
    }

    public CatalogResponse readCatalog(String sku, String market) {
        String filename = sku + FILE_SEPARATOR + market + JSON_EXTENSION;
        String path = FIXTURE_ROOT + CATALOG_DIR + filename;

        return catalogCache.computeIfAbsent(path, p -> read(p, CatalogResponse.class));
    }

    public CustomerResponse readCustomer(String customerId, String market) {
        String filename = customerId + FILE_SEPARATOR + market + JSON_EXTENSION;
        String path = FIXTURE_ROOT + CUSTOMER_DIR + filename;

        return customerCache.computeIfAbsent(path, p -> read(p, CustomerResponse.class));
    }

    public PricingResponse readPricing(String sku, String market) {
        String filename = sku + FILE_SEPARATOR + market + JSON_EXTENSION;
        String path = FIXTURE_ROOT + PRICING_DIR + filename;
        return pricingCache.computeIfAbsent(path, p -> read(p, PricingResponse.class));
    }

    public PricingResponse readPricingForCustomer(String sku, String customerId, String market) {
        String filename = sku + FILE_SEPARATOR + market + JSON_EXTENSION;
        String path = FIXTURE_ROOT + PRICING_DIR + filename;
        return pricingCache.computeIfAbsent(path, p -> read(p, PricingResponse.class));
    }

    private <T> T read(String classpathLocation, Class<T> type) {
        try (InputStream in = new ClassPathResource(classpathLocation).getInputStream()) {
            return objectMapper.readValue(in, type);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read fixture: " + classpathLocation, e);
        }
    }
}

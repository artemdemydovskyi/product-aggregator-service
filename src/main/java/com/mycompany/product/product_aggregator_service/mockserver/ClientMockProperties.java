package com.mycompany.product.product_aggregator_service.mockserver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "client")
public record ClientMockProperties(
        @Valid @NotNull ServiceProps catalog,
        @Valid @NotNull ServiceProps pricing,
        @Valid @NotNull ServiceProps customer,
        @Valid @NotNull ServiceProps availability
) {
    public record ServiceProps(
            @PositiveOrZero int baseLatency,
            @PositiveOrZero int jitter,
            @DecimalMin("0.0") @DecimalMax("1.0") double reliability
    ) {}
}
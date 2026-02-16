package com.mycompany.product.product_aggregator_service.mockserver;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ClientMockProperties.class)
public class ClientMockConfig {}
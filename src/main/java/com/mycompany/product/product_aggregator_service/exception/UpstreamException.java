package com.mycompany.product.product_aggregator_service.exception;

public class UpstreamException extends RuntimeException {
    public UpstreamException(String message) {
        super(message);
    }
}
package com.mycompany.product.product_aggregator_service.mockserver;

public class ClientMockException extends RuntimeException {
    public ClientMockException(String message) { super(message); }
    public ClientMockException(String message, Throwable cause) { super(message, cause); }
}

package com.mycompany.product.product_aggregator_service.mockserver;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class ClientSimulator {

    public void simulateCall(String serviceName, int baseLatencyMs, int jitterMs, double reliability) {
        int latencyMs = computeLatencyMs(baseLatencyMs, jitterMs);
        sleepOrThrow(latencyMs);
        failRandomly(serviceName, reliability);
    }

    private int computeLatencyMs(int baseLatencyMs, int jitterMs) {
        int offsetMs = (jitterMs <= 0)
                ? 0
                : ThreadLocalRandom.current().nextInt(-jitterMs, jitterMs + 1);
        return Math.max(0, baseLatencyMs + offsetMs);
    }

    private void sleepOrThrow(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ClientMockException("Interrupted during client call simulation", e);
        }
    }

    private void failRandomly(String serviceName, double reliability) {
        if (ThreadLocalRandom.current().nextDouble() > reliability) {
            throw new ClientMockException(serviceName + " failed (simulated)");
        }
    }
}
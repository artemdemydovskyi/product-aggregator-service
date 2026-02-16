# Product Aggregator Service

Spring Boot application that aggregates product information from multiple upstream clients (Catalog, Pricing,
Availability, Customer) into one response.

This README is designed so you can:

- start the app via IntelliJ “green arrow” (Run Configuration), **or** via CLI
- run a request immediately (copy/paste)

---

## Prerequisites

- Java 17+
- Gradle Wrapper (`./gradlew`)
- (Optional) Postman or `curl`

---

## Local profile: mock-upstreams (single-port)

Create (or update) this file:

`src/main/resources/application-mock-upstreams.properties`

```properties
# App runs on one fixed port
server.port=8080
# All upstream clients point to the SAME app (mock upstream controllers inside the app)
client.catalog.url=http://localhost:${server.port}
client.pricing.url=http://localhost:${server.port}
client.availability.url=http://localhost:${server.port}
client.customer.url=http://localhost:${server.port}
# Optional: fast + deterministic mocks, meaning no delay, no random failures)
client.catalog.base-latency-ms=0
client.catalog.jitter-ms=0
client.catalog.reliability=1.0
client.pricing.base-latency-ms=0
client.pricing.jitter-ms=0
client.pricing.reliability=1.0
client.availability.base-latency-ms=0
client.availability.jitter-ms=0
client.availability.reliability=1.0
client.customer.base-latency-ms=0
client.customer.jitter-ms=0
client.customer.reliability=1.0
```

---

## Start the app (IntelliJ green arrow)

### Option A (recommended): set Active Profile in Run Configuration

1. Open `ProductAggregatorServiceApplication`
2. Click the green arrow → **Modify Run Configuration…**
3. Set **Active profiles**:  
   `mock-upstreams`
4. Apply → Run

### Option B: VM option instead of Active profiles

In the same Run Configuration, set **VM options**:

```
-Dspring.profiles.active=mock-upstreams
```

Then click the green arrow.

---

## Start the app (CLI)

From the project root:

```bash
./gradlew clean bootRun --args="--spring.profiles.active=mock-upstreams"
```

Base URL:

```
http://localhost:8080
```

---

## Endpoints you can trigger

### 1) Aggregate product (main endpoint)

**GET**

```
/api/products/{sku}?market={market}[&customerId={customerId}]
```

- `sku` (path) — e.g. `DRL-18V-001`, `GRN-125-002`
- `market` (required) — e.g. `en-EN`, `nl-NL`, `sv-SE`, `pl-PL`
- `customerId` (optional) — e.g. `CUST-1001`

---

## Run requests immediately (copy/paste)

### Example 1 — full aggregation (with personalization)

```bash
curl -i "http://localhost:8080/api/products/DRL-18V-001?market=en-EN&customerId=CUST-1001"
```

### Example 2 — non-personalized (no customerId)

```bash
curl -i "http://localhost:8080/api/products/GRN-125-002?market=nl-NL"
```

### Example 3 — Swedish market example

```bash
curl -i "http://localhost:8080/api/products/DRL-18V-001?market=sv-SE&customerId=CUST-1001"
```

---

## Postman quick setup

1. New → **HTTP Request**
2. Method: **GET**
3. URL:
   ```
   http://localhost:8080/api/products/DRL-18V-001?market=en-EN&customerId=CUST-1001
   ```
4. Header:
    - `Accept: application/json`
5. Send

---


---

# Integration Testing

The project contains a comprehensive integration test:

## `ProductAggregationIntegrationTest`

This test class verifies the main aggregation behaviour of the system under different real-world scenarios.

It runs the full Spring Boot context and simulates upstream behaviour using the `mock-upstreams` profile.

### Covered Scenarios

#### Happy Path – Full Aggregation

- All upstream services are available.
- Product is fully aggregated.
- Response contains:
    - `catalog`
    - `pricing`
    - `availability`
    - `customer` (if `customerId` is provided)

This verifies that the system correctly orchestrates and combines all upstream responses.

---

#### Graceful Degradation

- Some optional upstream services (e.g. Pricing, Availability, Customer) fail.
- Catalog remains available.
- The system:
    - Still returns `200 OK`
    - Returns partial response
    - Sets failed components to `specific statuses`

This validates resilience and partial availability design.

Example expected behaviour:

```json
{
  "productId": "DRL-18V-001",
  "catalog": {
    ...
  },
  "pricing": "UNAVAILABLE",
  "availability": "UNKNOWN",
  "customer": null
}
```

---

#### Mandatory Upstream Failure (Catalog)

- The Catalog service fails (timeout / error / unavailable).
- The entire request fails (4xx/5xx).
- No partial response is returned.

Catalog is a critical dependency because it provides core product data.  
Without it, the product cannot be rendered correctly.
---

## Design Consideration: Flash Sales (Rapid Price Changes)

"The Pricing team needs to support 'flash sales' where prices change every few
minutes". A few assumptions in the current design change.

### Caching Strategy

Pricing becomes highly volatile data.

If I cache pricing for more than 30 seconds in the aggregator, that may already be too long during a flash sale.

So I would:

- Reduce TTL to a few seconds
- Or include a version/timestamp from Pricing to detect stale data
- Or disable pricing cache in the aggregator altogether

---

### Load & Latency

Flash sales usually mean some traffic spikes.

Pricing will naturally receive more traffic during these campaigns, and the aggregator should not make the situation
worse by amplifying load unnecessarily.

So the aggregator must be designed properly to avoid introducing extra pressure on Pricing — especially during peak
moments.

I would:

- Keep upstream calls parallel
- Use strict timeouts
- Add a circuit breaker
- Use bulkhead isolation so slow Pricing does not affect Catalog (This means isolating resources between upstream calls
  so that issues in Pricing do not impact other services.
  For example, Pricing can use a separate thread pool, its own connection pool, timeouts, probably its own
  circuit breaker. With this setup, if Pricing becomes slow or overloaded during a flash sale, only the resources
  allocated to Pricing are affected.)

---

### Consistency

If price changes every few minutes, the UI might display inconsistent states.

To improve this:

- Add a “price valid until” field
- Optionally, introduce event-based cache invalidation instead of relying only on pull-based requests. In this approach,
  Pricing would publish an event whenever a price changes (for example, via a message broker). The aggregator
  would subscribe to these events and update or invalidate its local cache accordingly.

---

### Resilience

During flash sales, Pricing may temporarily overload.

Currently pricing is optional, but during flash campaigns it may become business-critical.

So I might:

- Treat pricing as mandatory during campaigns
- Or return a structured fallback (e.g. `status: TEMPORARILY_UNAVAILABLE`)

---

### Observability

During flash campaigns I would monitor:

- Pricing latency
- Timeout rate
- Circuit breaker open rate
- Traffic spikes

---

### Summary

Flash sales do not break the architecture, but they change operational strategy:

- Caching strategy needs to be reconsidered
- Resilience patterns become more important
- Pricing may shift from optional to critical

The aggregation pattern still works — but pricing volatility requires stronger reliability mechanisms and better operational control
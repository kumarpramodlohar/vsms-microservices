# VSMS Local Dev Isolation Guide

> **Goal**: Enable any team member to pull a single service/module and run it independently without spinning up all 14 services.

---

## Table of Contents

1. [The 4 Coupling Layers](#the-4-coupling-layers)
2. [Service Dependency Map](#service-dependency-map)
3. [Per-Service Tradeoffs & Solutions](#per-service-tradeoffs--solutions)
   - [auth-service](#auth-service-port-8090)
   - [master-service](#master-service-port-8083)
   - [customer-service](#customer-service-port-8082)
   - [sales-service](#sales-service-port-8085)
   - [inventory-service](#inventory-service-port-8087)
   - [cost-service](#cost-service-port-8089)
   - [fulfilment-service](#fulfilment-service-port-8088)
   - [purchase-service](#purchase-service-port-8086)
   - [hr-service](#hr-service-port-8084)
   - [report-service](#report-service-port-8091)
   - [notification-service](#notification-service-port-3001)
   - [drs-service](#drs-service-port-3002)
4. [Shared Infra: Minimal Local Stack](#shared-infra-minimal-local-stack)
5. [Reusable Patterns](#reusable-patterns)
   - [Disable Eureka (all services)](#pattern-1-disable-eureka)
   - [Bypass JWT Security (local profile)](#pattern-2-bypass-jwt-security)
   - [Feign Client Stub (local profile)](#pattern-3-feign-client-stub)
   - [Publish Shared Libraries](#pattern-4-publish-shared-libraries)
6. [Recommended Developer Workflow](#recommended-developer-workflow)
7. [Tradeoff Summary Table](#tradeoff-summary-table)

---

## The 4 Coupling Layers

Every service in VSMS has at least 3 of these 4 invisible dependencies. They are the root cause of "I can't run my service without running everything."

### Layer 1 — Eureka (affects ALL Java services)

Every Spring service auto-registers with Eureka on startup. If Eureka is unreachable, the app throws a connection exception and exits immediately.

**Fix**: One `application-local.yml` block per service.

```yaml
eureka:
  client:
    enabled: false
    register-with-eureka: false
    fetch-registry: false
```

### Layer 2 — JWT / auth-service JWKS (affects ALL services via API Gateway)

The API Gateway validates every JWT token against `auth-service:8090/api/v1/auth/jwks`. Without auth-service alive, every API call returns `401 Unauthorized`.

**Fix**: Either run auth-service standalone (it has no Feign deps), or add a local security bypass bean.

### Layer 3 — Feign Clients (affects sales, inventory, cost, fulfilment, report)

Synchronous HTTP calls between services using Spring Feign + Eureka service names. These cause hard runtime failures when the target service is down.

| Service | Feign Calls To |
|---|---|
| `sales-service` | customer-service, master-service |
| `inventory-service` | master-service |
| `cost-service` | sales-service |
| `fulfilment-service` | sales-service |
| `report-service` | sales-service, fulfilment-service, hr-service |

**Fix**: `@Profile("local")` stub implementations that return hardcoded test data.

### Layer 4 — RabbitMQ (affects sales, inventory, fulfilment, notification, drs)

Spring AMQP registers listener beans on startup. If the broker is unreachable, bean initialization fails and the entire service crashes.

| Service | Consumes | Publishes |
|---|---|---|
| `inventory-service` | GrnApproved, SalesOrderActivated | — |
| `sales-service` | SalesOrderCostApproved | SalesOrderActivated |
| `fulfilment-service` | SalesOrderActivated | InvoiceGenerated |
| `cost-service` | — | SalesOrderCostApproved |
| `customer-service` | — | CustomerApproved |
| `purchase-service` | — | GrnApproved |
| `notification-service` | CustomerApproved, SalesOrderActivated, InvoiceGenerated | — |
| `drs-service` | InvoiceGenerated | — |

**Fix**: Run RabbitMQ as a single lightweight Docker container. Do not try to mock it — you lose event testing.

```bash
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3.13-management-alpine
```

---

## Service Dependency Map

```
Legend:
  [R] = Required (always blocks startup)
  [F] = Feign Client (blocks at runtime)
  [E] = Event / RabbitMQ (blocks startup)
  [L] = Shared Library (silent stale JAR risk)

auth-service         [R] Eureka  [R] MySQL(vsms_auth)  [L] vsms-common
master-service       [R] Eureka  [R] MySQL(vsms_master) [R] auth(JWKS)  [L] vsms-common
customer-service     [R] Eureka  [R] MySQL(vsms_customer) [R] auth(JWKS) [E] publishes:CustomerApproved
sales-service        [R] Eureka  [R] MySQL(vsms_sales)  [R] auth(JWKS)
                     [F] customer-service  [F] master-service
                     [E] consumes:SalesOrderCostApproved  [E] publishes:SalesOrderActivated
purchase-service     [R] Eureka  [R] MySQL(vsms_purchase) [R] auth(JWKS)
                     [E] publishes:GrnApproved
inventory-service    [R] Eureka  [R] MySQL(vsms_inventory) [R] auth(JWKS)
                     [F] master-service
                     [E] consumes:GrnApproved  [E] consumes:SalesOrderActivated
cost-service         [R] Eureka  [R] MySQL(vsms_cost)   [R] auth(JWKS)
                     [F] sales-service  [E] publishes:SalesOrderCostApproved
fulfilment-service   [R] Eureka  [R] MySQL(vsms_fulfilment) [R] auth(JWKS)
                     [F] sales-service
                     [E] consumes:SalesOrderActivated  [E] publishes:InvoiceGenerated
hr-service           [R] Eureka  [R] MySQL(vsms_hr)     [R] auth(JWKS)   [L] vsms-common
report-service(Go)   [R] Eureka  [R] auth(JWKS)
                     [F] sales-service  [F] fulfilment-service  [F] hr-service
notification-service(Node.js) [R] RabbitMQ  [R] auth(JWKS)
                     [E] consumes:CustomerApproved  [E] consumes:SalesOrderActivated  [E] consumes:InvoiceGenerated
drs-service(Node.js) [R] MySQL(vsms_drs)  [R] RabbitMQ  [R] auth(JWKS)
                     [F] customer-service
                     [E] consumes:InvoiceGenerated
```

---

## Per-Service Tradeoffs & Solutions

---

### auth-service (Port: 8090)

**Isolation difficulty**: Low — no Feign clients, no RabbitMQ consumers.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | Eureka crash on boot | Service exits immediately if Eureka is unreachable |
| 2 | vsms-common stale JAR | If common lib changes, rebuild uses cached old version silently |

#### Solution: application-local.yml

```yaml
# services/auth-service/src/main/resources/application-local.yml

eureka:
  client:
    enabled: false
    register-with-eureka: false
    fetch-registry: false

spring:
  application:
    name: auth-service
```

#### Solution: Publish shared libs first

```bash
# Run once from monorepo root whenever vsms-common changes
./gradlew :shared:vsms-common:publishToMavenLocal
./gradlew :shared:vsms-events:publishToMavenLocal

# Then run auth-service
cd services/auth-service
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

### master-service (Port: 8083)

**Isolation difficulty**: Low — no Feign clients, no events.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | Eureka crash on boot | Must be disabled |
| 2 | JWT validation needs auth-service | All API calls return 401 without it |
| 3 | Most depended-upon service | sales + inventory both Feign-call this; if it crashes, they break too |

#### Solution A: Disable Eureka + point to local auth

```yaml
# application-local.yml
eureka:
  client:
    enabled: false

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8090/api/v1/auth/jwks
```

Run auth-service and MySQL only. master-service is then fully standalone.

#### Solution B: Disable security entirely for local dev

```java
// src/main/java/com/vsms/master/config/LocalSecurityConfig.java

@Configuration
@Profile("local")
public class LocalSecurityConfig {

  @Bean
  public SecurityFilterChain localSecurity(HttpSecurity http) throws Exception {
    return http
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .csrf(AbstractHttpConfigurer::disable)
      .build();
  }
}
```

> **Warning**: Always keep the `@Profile("local")` annotation. Never commit without it.

---

### customer-service (Port: 8082)

**Isolation difficulty**: Low — no Feign clients, only publishes events.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | Eureka crash on boot | Must be disabled |
| 2 | JWT validation needs auth-service | All API calls return 401 without it |
| 3 | RabbitMQ needed to verify event publishing | Without broker, CustomerApproved publish silently fails |

#### Solution: Minimal local stack

```yaml
# docker-compose.customer-local.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: changeme
    ports:
      - "3306:3306"
    volumes:
      - ./infra/mysql/init:/docker-entrypoint-initdb.d

  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"

  eureka-server:
    image: vsms/eureka-server
    ports:
      - "8761:8761"

  auth-service:
    image: vsms/auth-service
    ports:
      - "8090:8090"
    depends_on:
      - mysql
      - eureka-server
```

```bash
docker-compose -f docker-compose.customer-local.yml up -d
cd services/customer-service
./gradlew bootRun --args='--spring.profiles.active=local'
```

No stubs needed. customer-service is simple to run alone.

---

### sales-service (Port: 8085)

**Isolation difficulty**: High — 2 Feign clients + 2 RabbitMQ bindings.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | CustomerServiceClient throws at runtime | Order creation fails with FeignException without customer-service |
| 2 | MasterServiceClient throws at runtime | Any master data lookup fails without master-service |
| 3 | RabbitMQ required to start | AMQP listener beans crash service on startup if broker is down |
| 4 | Cost-service saga is untestable | Order activation is triggered by SalesOrderCostApproved event |

#### Solution A: Stub Feign clients with @Profile("local")

```java
// Step 1: Guard the real Feign interface
@FeignClient(name = "customer-service")
@Profile("!local")
public interface CustomerServiceClient {
  CustomerDto getCustomer(@PathVariable Long id);
}

// Step 2: Provide a stub for local profile
@Component
@Profile("local")
public class CustomerServiceClientStub implements CustomerServiceClient {

  @Override
  public CustomerDto getCustomer(Long id) {
    return CustomerDto.builder()
      .id(id)
      .status("APPROVED")
      .name("Test Customer Local")
      .gstNumber("29AAAAA0000A1Z5")
      .build();
  }
}

// Repeat same pattern for MasterServiceClientStub
@Component
@Profile("local")
public class MasterServiceClientStub implements MasterServiceClient {

  @Override
  public ItemDto getItem(Long id) {
    return ItemDto.builder()
      .id(id).name("Test Item").uom("PCS").categoryId(1L).build();
  }

  @Override
  public LocationDto getLocation(Long id) {
    return LocationDto.builder().id(id).name("Test Branch").build();
  }
}
```

#### Solution B: docker-compose.sales-local.yml

```yaml
# docker-compose.sales-local.yml — place at repo root
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: changeme
    ports:
      - "3306:3306"
    volumes:
      - ./infra/mysql/init:/docker-entrypoint-initdb.d

  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"

  eureka-server:
    image: vsms/eureka-server
    ports:
      - "8761:8761"

  auth-service:
    image: vsms/auth-service
    ports:
      - "8090:8090"
    depends_on:
      - mysql
      - eureka-server
```

```bash
# Run the minimal stack (4 containers instead of 14)
docker-compose -f docker-compose.sales-local.yml up -d

# Then run sales-service locally
cd services/sales-service
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

### inventory-service (Port: 8087)

**Isolation difficulty**: High — 1 Feign client + 2 RabbitMQ consumer queues.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | RabbitMQ crash on start | Two AMQP listener queues register on boot; service crashes without broker |
| 2 | MasterServiceClient blocks stock queries | Any item name resolution fails without master-service |
| 3 | Core logic only testable via events | stock-in / stock-out paths are event-driven; untestable without message injection |

#### Solution A: Stub MasterServiceClient

```java
@Component
@Profile("local")
public class MasterServiceClientStub implements MasterServiceClient {

  @Override
  public ItemDto getItem(Long id) {
    return ItemDto.builder()
      .id(id).name("Test Item").uom("PCS").build();
  }
}
```

#### Solution B: Inject test events via RabbitMQ Management UI

```bash
# After starting RabbitMQ, publish a GrnApproved event via curl:
curl -u vsms:changeme -X POST \
  http://localhost:15672/api/exchanges/%2F/vsms.purchase/publish \
  -H "Content-Type: application/json" \
  -d '{
    "properties": {},
    "routing_key": "grn.approved",
    "payload": "{\"grnId\":1,\"itemId\":10,\"qty\":50,\"locationId\":1}",
    "payload_encoding": "string"
  }'
```

This tests the full stock-in flow without standing up purchase-service.

---

### cost-service (Port: 8089)

**Isolation difficulty**: Medium — 1 Feign client, publishes 1 event.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | SalesServiceClient hard-fails on approval | PATCH /cost/{id}/approve calls sales-service to trigger activation |
| 2 | Approval saga untestable end-to-end | Full flow spans cost → sales → inventory |

#### Solution: Stub SalesServiceClient

```java
@Component
@Profile("local")
public class SalesServiceClientStub implements SalesServiceClient {

  @Override
  public void activateSalesOrder(Long orderId) {
    // no-op — log and pretend activation succeeded
    log.info("[LOCAL STUB] activateSalesOrder called for order {}", orderId);
  }

  @Override
  public SalesOrderDto getOrder(Long id) {
    return SalesOrderDto.builder()
      .id(id).status("DRAFT").build();
  }
}
```

The cost approval flow completes locally. The `SalesOrderCostApproved` event is still published to RabbitMQ and verifiable in the management UI at `http://localhost:15672`.

---

### fulfilment-service (Port: 8088)

**Isolation difficulty**: Medium — 1 Feign client + 1 RabbitMQ consumer.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | Invoice creation blocked without sales-service | SalesServiceClient validates order is ACTIVE on POST /invoices |
| 2 | RabbitMQ required on start | AMQP listener for SalesOrderActivated registers at boot |

#### Solution: Stub SalesServiceClient to always return ACTIVE

```java
@Component
@Profile("local")
public class SalesServiceClientStub implements SalesServiceClient {

  @Override
  public SalesOrderDto getOrder(Long id) {
    return SalesOrderDto.builder()
      .id(id)
      .status("ACTIVE")
      .customerId(1L)
      .build();
  }
}
```

All invoice creation flows work locally. The stub always returns ACTIVE so the validation gate passes.

---

### purchase-service (Port: 8086)

**Isolation difficulty**: Low — no Feign clients, only publishes events (doesn't consume).

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | Easiest service to isolate | No synchronous dependencies on other domain services |
| 2 | RabbitMQ needed to verify GRN approval | Without broker, GrnApproved publish silently fails |

#### Solution: Minimal local stack

```yaml
# docker-compose.purchase-local.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: changeme
    ports:
      - "3306:3306"
    volumes:
      - ./infra/mysql/init:/docker-entrypoint-initdb.d

  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"

  eureka-server:
    image: vsms/eureka-server
    ports:
      - "8761:8761"
```

```bash
docker-compose -f docker-compose.purchase-local.yml up -d
cd services/purchase-service
./gradlew bootRun --args='--spring.profiles.active=local'
```

No stubs needed. purchase-service is the simplest domain service to run alone.

---

### hr-service (Port: 8084)

**Isolation difficulty**: Very Low — fully standalone, no Feign clients, no events.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | Eureka crash on boot | Must be disabled |
| 2 | JWT validation needs auth-service | All API calls return 401 without it |

#### Solution: Disable Eureka + run MySQL only

```yaml
# application-local.yml
eureka:
  client:
    enabled: false

spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8090/api/v1/auth/jwks
```

```bash
# Just MySQL — no RabbitMQ, no other services needed
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=changeme mysql:8.0

cd services/hr-service
./gradlew bootRun --args='--spring.profiles.active=local'
```

Add the `LocalSecurityConfig` bean from the master-service section if you want to skip auth-service entirely.

---

### report-service (Port: 8091)

**Isolation difficulty**: High — 3 Feign clients, written in Go (no Spring profiles).

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | 3 Feign clients — most coupled read-service | Aggregates from sales, fulfilment, hr; any one down = partial failure |
| 2 | Go service — no Spring @Profile stubs | Cannot use the Java stub pattern; needs WireMock or a mock HTTP server |
| 3 | Read-only aggregator — no writes | Failures are non-destructive; all dependencies are read-only |

#### Solution A: WireMock sidecar

```yaml
# docker-compose.report-local.yml
services:
  wiremock:
    image: wiremock/wiremock:3.3.1
    ports:
      - "9090:8080"
    volumes:
      - ./wiremock:/home/wiremock
```

```json
// wiremock/mappings/sales-orders.json
{
  "request": {
    "method": "GET",
    "urlPattern": "/api/v1/sales-orders.*"
  },
  "response": {
    "status": 200,
    "jsonBody": [
      {"id": 1, "status": "ACTIVE", "customerId": 1, "total": 50000}
    ],
    "headers": {"Content-Type": "application/json"}
  }
}
```

```json
// wiremock/mappings/invoices.json
{
  "request": {
    "method": "GET",
    "urlPattern": "/api/v1/invoices.*"
  },
  "response": {
    "status": 200,
    "jsonBody": [
      {"id": 1, "orderId": 1, "amount": 50000, "type": "TAX"}
    ],
    "headers": {"Content-Type": "application/json"}
  }
}
```

#### Solution B: Override service URLs via environment variables

```bash
# No code changes needed — pure env config
export SALES_SERVICE_URL=http://localhost:9090
export FULFILMENT_SERVICE_URL=http://localhost:9090
export HR_SERVICE_URL=http://localhost:9090
export EUREKA_ENABLED=false

# Start WireMock only, then run the Go service
docker-compose -f docker-compose.report-local.yml up -d
go run main.go
```

All 3 services are mocked from one WireMock container on port 9090. Switch between real services and mocks by changing env vars only.

---

### notification-service (Port: 3001)

**Isolation difficulty**: Medium — Node.js service, consumes 3 RabbitMQ events.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | RabbitMQ required on start | AMQP listener queues register at boot; service crashes without broker |
| 2 | JWT validation needs auth-service | All API calls return 401 without it |
| 3 | Event-driven only — no REST endpoints | Cannot test without publishing events to RabbitMQ |

#### Solution: Minimal local stack with RabbitMQ

```yaml
# docker-compose.notification-local.yml
services:
  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: vsms
      RABBITMQ_DEFAULT_PASS: changeme

  auth-service:
    image: vsms/auth-service
    ports:
      - "8090:8090"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://host.docker.internal:3306/vsms_auth
      MYSQL_ROOT_PASSWORD: changeme
      EUREKA_URL: http://host.docker.internal:8761/eureka/
```

```bash
# Start RabbitMQ and auth-service
docker-compose -f docker-compose.notification-local.yml up -d

# Run notification-service locally
cd services/notification-service
npm install
npm start
```

#### Inject test events via RabbitMQ Management UI

```bash
# Publish a CustomerApproved event:
curl -u vsms:changeme -X POST \
  http://localhost:15672/api/exchanges/%2F/vsms.customer/publish \
  -H "Content-Type: application/json" \
  -d '{
    "properties": {},
    "routing_key": "customer.approved",
    "payload": "{\"customerId\":1,\"customerName\":\"Test Customer\",\"gstNumber\":\"29AAAAA0000A1Z5\"}",
    "payload_encoding": "string"
  }'
```

This tests the notification flow without standing up customer-service.

---

### drs-service (Port: 3002)

**Isolation difficulty**: Medium — Node.js + Prisma, 1 Feign client + 1 RabbitMQ consumer.

#### Tradeoffs

| # | Problem | Impact |
|---|---|---|
| 1 | MySQL required on start | Prisma connects to database on boot; service crashes without DB |
| 2 | RabbitMQ required on start | AMQP listener for InvoiceGenerated registers at boot |
| 3 | CustomerServiceClient blocks document generation | Customer data lookup fails without customer-service |
| 4 | JWT validation needs auth-service | All API calls return 401 without it |

#### Solution A: Stub CustomerServiceClient with environment variable

```bash
# Override customer service URL to point to a mock
export CUSTOMER_SERVICE_URL=http://localhost:9090
export DATABASE_URL=mysql://root:changeme@localhost:3306/vsms_drs
export RABBITMQ_URL=amqp://vsms:changeme@localhost:5672
export JWT_JWKS_URI=http://localhost:8090/api/v1/auth/jwks

# Start MySQL, RabbitMQ, and auth-service
docker-compose -f docker-compose.drs-local.yml up -d

# Run drs-service locally
cd services/drs-service
npm install
npx prisma generate
npm start
```

#### Solution B: docker-compose.drs-local.yml

```yaml
# docker-compose.drs-local.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: changeme
    ports:
      - "3306:3306"
    volumes:
      - ./infra/mysql/init:/docker-entrypoint-initdb.d

  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: vsms
      RABBITMQ_DEFAULT_PASS: changeme

  auth-service:
    image: vsms/auth-service
    ports:
      - "8090:8090"
    depends_on:
      - mysql

  wiremock:
    image: wiremock/wiremock:3.3.1
    ports:
      - "9090:8080"
    volumes:
      - ./wiremock:/home/wiremock
```

```bash
docker-compose -f docker-compose.drs-local.yml up -d
cd services/drs-service
npm install
npx prisma generate
npm start
```

#### Inject test events via RabbitMQ Management UI

```bash
# Publish an InvoiceGenerated event:
curl -u vsms:changeme -X POST \
  http://localhost:15672/api/exchanges/%2F/vsms.fulfilment/publish \
  -H "Content-Type: application/json" \
  -d '{
    "properties": {},
    "routing_key": "invoice.generated",
    "payload": "{\"invoiceId\":1,\"orderId\":1,\"customerId\":1,\"amount\":50000,\"type\":\"TAX\"}",
    "payload_encoding": "string"
  }'
```

This tests the document generation flow without standing up fulfilment-service.

---

## Shared Infra: Minimal Local Stack

Create a single `docker-compose.infra.yml` at the repo root. Every developer runs this once — it covers all shared infra needed by any service.

```yaml
# docker-compose.infra.yml
# Minimal infra for local single-service development
# Usage: docker-compose -f docker-compose.infra.yml up -d

version: "3.9"

services:
  mysql:
    image: mysql:8.0
    container_name: vsms-mysql
    environment:
      MYSQL_ROOT_PASSWORD: changeme
    ports:
      - "3306:3306"
    volumes:
      - ./infra/mysql/init:/docker-entrypoint-initdb.d
      - vsms-mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      retries: 5

  rabbitmq:
    image: rabbitmq:3.13-management-alpine
    container_name: vsms-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: vsms
      RABBITMQ_DEFAULT_PASS: changeme
    ports:
      - "5672:5672"
      - "15672:15672"
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 10s
      retries: 5

  eureka-server:
    image: vsms/eureka-server:latest
    container_name: vsms-eureka
    ports:
      - "8761:8761"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 10s
      retries: 5

  auth-service:
    image: vsms/auth-service:latest
    container_name: vsms-auth
    ports:
      - "8090:8090"
    environment:
      EUREKA_URL: http://eureka-server:8761/eureka/
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/vsms_auth
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: changeme
    depends_on:
      mysql:
        condition: service_healthy
      eureka-server:
        condition: service_healthy

  redis:
    image: redis:7-alpine
    container_name: vsms-redis
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      retries: 5

  zipkin:
    image: openzipkin/zipkin:3
    container_name: vsms-zipkin
    ports:
      - "9411:9411"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9411/health"]
      interval: 10s
      retries: 5

volumes:
  vsms-mysql-data:
```

---

## Reusable Patterns

These patterns can be copy-pasted into any service. Add them once and they work for all future local dev.

### Pattern 1: Disable Eureka

Add to every service's `src/main/resources/application-local.yml`:

```yaml
eureka:
  client:
    enabled: false
    register-with-eureka: false
    fetch-registry: false
```

### Pattern 2: Bypass JWT Security

Add to every Java service (guards are mandatory):

```java
// src/main/java/com/vsms/<service>/config/LocalSecurityConfig.java

@Configuration
@Profile("local")   // MUST have this — never remove
public class LocalSecurityConfig {

  @Bean
  public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
      .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
      .csrf(AbstractHttpConfigurer::disable)
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .build();
  }
}
```

### Pattern 3: Feign Client Stub

Template for any Feign client stub. Replace `TargetServiceClient` and return types with the actual interface:

```java
// Step 1 — Guard the real Feign interface
@FeignClient(name = "target-service")
@Profile("!local")                     // real client only when NOT local
public interface TargetServiceClient {
  SomeDtoType getById(@PathVariable Long id);
}

// Step 2 — Provide a stub for local profile
@Component
@Profile("local")
@Slf4j
public class TargetServiceClientStub implements TargetServiceClient {

  @Override
  public SomeDtoType getById(Long id) {
    log.info("[LOCAL STUB] TargetServiceClient.getById({})", id);
    return SomeDtoType.builder()
      .id(id)
      .status("ACTIVE")
      // ... fill required fields with test data
      .build();
  }
}
```

### Pattern 4: Publish Shared Libraries

Add a `Makefile` at the repo root:

```makefile
# Makefile

.PHONY: publish-shared local-auth local-master local-sales local-hr local-purchase local-inventory local-cost local-fulfilment local-customer

publish-shared:
	./gradlew :shared:vsms-common:publishToMavenLocal
	./gradlew :shared:vsms-events:publishToMavenLocal
	@echo "Shared libs published to local Maven cache"

infra-up:
	docker-compose -f docker-compose.infra.yml up -d
	@echo "Waiting for services to be healthy..."
	sleep 15

infra-down:
	docker-compose -f docker-compose.infra.yml down

local-auth: publish-shared
	cd services/auth-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-master: publish-shared
	cd services/master-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-customer: publish-shared
	cd services/customer-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-sales: publish-shared
	cd services/sales-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-hr: publish-shared
	cd services/hr-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-purchase: publish-shared
	cd services/purchase-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-inventory: publish-shared
	cd services/inventory-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-cost: publish-shared
	cd services/cost-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-fulfilment: publish-shared
	cd services/fulfilment-service && \
	./gradlew bootRun --args='--spring.profiles.active=local'

local-notification:
	cd services/notification-service && \
	npm install && \
	npm start

local-drs:
	cd services/drs-service && \
	npm install && \
	npx prisma generate && \
	npm start

local-report:
	cd services/report-service && \
	go run main.go
```

**Developer workflow becomes:**

```bash
make infra-up          # start 6 containers once
make local-sales       # run sales-service with local profile
```

---

## Recommended Developer Workflow

```
1. Clone repo
   └── git clone <repo-url> && cd vsms-microservices

2. Start minimal infra (once, keep running)
   └── docker-compose -f docker-compose.infra.yml up -d
       → starts: MySQL, RabbitMQ, Eureka, auth-service, Redis, Zipkin

3. Publish shared libs (once, re-run when vsms-common changes)
   └── make publish-shared

4. Run your service
   └── cd services/<service-name>
       ./gradlew bootRun --args='--spring.profiles.active=local'
       OR for Node.js services: npm start
       OR for Go services: go run main.go

5. Verify it's alive
   └── curl http://localhost:<port>/actuator/health
       OR for Node.js: curl http://localhost:<port>/health
       OR for Go: curl http://localhost:<port>/health

6. Test APIs directly (no API Gateway needed locally)
   └── curl http://localhost:<port>/api/v1/<endpoint>
        OR open http://localhost:<port>/swagger-ui.html
```

---

## Tradeoff Summary Table

| Service | Technology | Feign Deps | Event Deps | Isolation Difficulty | Min Containers Needed | Key Action Required |
|---|---|---|---|---|---|---|
| `auth-service` | Java | None | None | Very Low | MySQL only | Disable Eureka |
| `hr-service` | Java | None | None | Very Low | MySQL only | Disable Eureka |
| `master-service` | Java | None | None | Low | MySQL + auth | Disable Eureka, bypass JWT |
| `customer-service` | Java | None | Publishes only | Low | MySQL + RabbitMQ | Disable Eureka |
| `purchase-service` | Java | None | Publishes only | Low | MySQL + RabbitMQ | Disable Eureka |
| `cost-service` | Java | sales-service | Publishes only | Medium | MySQL + RabbitMQ + auth | Stub SalesServiceClient |
| `fulfilment-service` | Java | sales-service | Consumes + publishes | Medium | MySQL + RabbitMQ + auth | Stub SalesServiceClient |
| `sales-service` | Java | customer + master | Consumes + publishes | High | MySQL + RabbitMQ + auth | Stub both Feign clients |
| `inventory-service` | Java | master-service | Consumes only | High | MySQL + RabbitMQ + auth | Stub MasterServiceClient |
| `report-service` | Go | sales + fulfilment + hr | None | High | WireMock container | Env-var URL overrides |
| `notification-service` | Node.js | None | Consumes only | Medium | RabbitMQ + auth | Publish test events |
| `drs-service` | Node.js | customer-service | Consumes only | Medium | MySQL + RabbitMQ + auth | Stub CustomerServiceClient |

---

## Shared Library Risk Note

`vsms-common` and `vsms-events` are compiled into every Java service at build time. If a developer changes these shared libs and runs `./gradlew bootRun` without re-publishing them first, the service silently runs against the old JAR from the local Maven cache.

**Mitigation**: Always run `make publish-shared` (or the raw Gradle commands) before starting any service after a shared lib change. Consider adding a `SNAPSHOT` version bump check to the CI pipeline to catch stale dependency versions early.

---

## Additional Infrastructure Notes

### Redis (Port: 6379)
- **Purpose**: Optional caching layer
- **Isolation**: Not required for most services; can be disabled via configuration
- **Local dev**: Run as part of `docker-compose.infra.yml` or skip if not needed

### Zipkin (Port: 9411)
- **Purpose**: Distributed tracing and performance monitoring
- **Isolation**: Not required for service functionality; purely observability
- **Local dev**: Run as part of `docker-compose.infra.yml` or skip if not needed

### API Gateway (Port: 8080)
- **Purpose**: Single entry point for all client requests
- **Isolation**: Not required for local single-service development
- **Local dev**: Access services directly on their ports (e.g., `http://localhost:8085/api/v1/sales-orders`)

---

## Security Configuration for Local Development

To ensure the production-grade security configuration works in local development, configure the following properties in each service's `application.yml`:

```yaml
app:
  security:
    cors:
      allowed-origins: "http://localhost:8080,http://localhost:3000"  # Comma-separated list of allowed origins for CORS
    headers:
      hsts:
        enabled: false  # Set to false in local development to avoid HTTPS redirects
```

For production, set `hsts.enabled` to `true` and configure `allowed-origins` to the actual frontend domains.

*Generated from VSMS README.md analysis — Last updated: 2026-03-28*

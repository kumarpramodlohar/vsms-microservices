# VSMS Microservices Event Communication Diagram & Status Tracking

## 📋 Executive Summary

This document analyzes the current VSMS microservices event-driven architecture, mapping out the complete communication flow between services and identifying where business process statuses are stored in the database.

## 🏗️ Current Architecture Overview

### Service Communication Pattern
```
API Gateway (8080) → Individual Services → RabbitMQ → Event Consumers
```

### Database per Service Pattern
- Each service owns its domain data
- Events communicate state changes between services
- No direct database coupling between services

---

## 🔄 Event Communication Flow Diagram

```mermaid
graph TD
    %% Client Layer
    Client[👤 Client Applications] --> AG[🌐 API Gateway<br/>Port: 8080]

    %% Infrastructure Layer
    AG --> ES[📋 Eureka Server<br/>Port: 8761<br/>Service Discovery]
    AG --> RabbitMQ[(🐰 RabbitMQ<br/>Port: 5672<br/>Event Broker)]

    %% Business Services Layer
    AG --> CS[👥 Customer Service<br/>Port: 8082<br/>Database: vsms_customer]
    AG --> SS[📦 Sales Service<br/>Port: 8085<br/>Database: vsms_sales]
    AG --> PS[🛒 Purchase Service<br/>Port: 8086<br/>Database: vsms_purchase]
    AG --> IS[📊 Inventory Service<br/>Port: 8087<br/>Database: vsms_inventory]
    AG --> CoS[💰 Cost Service<br/>Port: 8089<br/>Database: vsms_cost]
    AG --> FS[📄 Fulfilment Service<br/>Port: 8088<br/>Database: vsms_fulfilment]

    %% Supporting Services
    AG --> RS[📈 Report Service<br/>Port: 8091<br/>Go + Analytics]
    AG --> NS[📧 Notification Service<br/>Port: 3001<br/>Node.js + Email/SMS]
    AG --> DS[📑 DRS Service<br/>Port: 3002<br/>Node.js + PDF Generation]

    %% Event Flow - Customer Approval Process
    CS -.->|"CustomerApproved<br/>📨 Exchange: vsms.customer<br/>🗝️ Routing Key: customer.approved"| RabbitMQ
    RabbitMQ -.->|"CustomerApproved"| SS

    %% Event Flow - Sales Order Process
    SS -.->|"SalesOrderActivated<br/>📨 Exchange: vsms.sales<br/>🗝️ Routing Key: sales.order.activated"| RabbitMQ
    RabbitMQ -.->|"SalesOrderActivated"| IS
    RabbitMQ -.->|"SalesOrderActivated"| FS

    %% Event Flow - Cost Approval Process
    CoS -.->|"SalesOrderCostApproved<br/>📨 Exchange: vsms.cost<br/>🗝️ Routing Key: cost.approved"| RabbitMQ
    RabbitMQ -.->|"SalesOrderCostApproved"| SS

    %% Event Flow - Purchase Process
    PS -.->|"GrnApproved<br/>📨 Exchange: vsms.purchase<br/>🗝️ Routing Key: grn.approved"| RabbitMQ
    RabbitMQ -.->|"GrnApproved"| IS

    %% Event Flow - Invoice Process
    FS -.->|"InvoiceGenerated<br/>📨 Exchange: vsms.fulfilment<br/>🗝️ Routing Key: invoice.generated"| RabbitMQ

    %% Service Dependencies (Feign Clients)
    SS -.->|"Customer Validation"| CS
    SS -.->|"Master Data"| MS[(🏢 Master Service<br/>Port: 8083<br/>Database: vsms_master)]
    IS -.->|"Master Data"| MS
    FS -.->|"Order Data"| SS

    %% Dead Letter Queues
    RabbitMQ --> DLQ1[📭 vsms.customer.dlq]
    RabbitMQ --> DLQ2[📭 vsms.sales.dlq]
    RabbitMQ --> DLQ3[📭 vsms.purchase.dlq]
    RabbitMQ --> DLQ4[📭 vsms.cost.dlq]
    RabbitMQ --> DLQ5[📭 vsms.fulfilment.dlq]
    RabbitMQ --> DLQ6[📭 vsms.inventory.dlq]

    %% Database Layer
    CS --> C_DB[(🗄️ vsms_customer<br/>customers.approval_status)]
    SS --> S_DB[(🗄️ vsms_sales<br/>trn_order_header.status)]
    PS --> P_DB[(🗄️ vsms_purchase<br/>purchase_header.status)]
    IS --> I_DB[(🗄️ vsms_inventory<br/>stock transactions)]
    CoS --> Co_DB[(🗄️ vsms_cost<br/>trn_cost_header.status)]
    FS --> F_DB[(🗄️ vsms_fulfilment<br/>trn_bill_header.status)]

    %% Styling
    classDef gateway fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef infrastructure fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef business fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef supporting fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef database fill:#ffebee,stroke:#b71c1c,stroke-width:2px
    classDef event fill:#fff8e1,stroke:#f57f17,stroke-width:2px

    class AG gateway
    class ES,RabbitMQ infrastructure
    class CS,SS,PS,IS,CoS,FS business
    class RS,NS,DS supporting
    class C_DB,S_DB,P_DB,I_DB,Co_DB,F_DB database
```

---

## 📊 Event Status Tracking Table

| Event | Publisher Service | Consumer Services | Status Field | Possible Values | Database Table |
|-------|------------------|------------------|--------------|-----------------|----------------|
| **CustomerApproved** | Customer Service | Sales Service | `approval_status` | `PENDING`, `APPROVED`, `REJECTED` | `vsms_customer.customers` |
| **SalesOrderActivated** | Sales Service | Inventory Service,<br/>Fulfilment Service | `status` | `DRAFT`, `ACTIVE`, `CANCELLED` | `vsms_sales.trn_order_header` |
| **SalesOrderCostApproved** | Cost Service | Sales Service | `status` | `PENDING`, `APPROVED`, `REJECTED` | `vsms_cost.trn_cost_header` |
| **GrnApproved** | Purchase Service | Inventory Service | `status` | `DRAFT`, `APPROVED` | `vsms_purchase.trn_purchase_header` |
| **InvoiceGenerated** | Fulfilment Service | - | `status` | `DRAFT`, `APPROVED` | `vsms_fulfilment.trn_bill_header` |

---

## 🔍 Detailed Event Analysis

### 1. CustomerApproved Event
**Business Purpose**: Customer approval workflow completion
```java
public record CustomerApproved(
    UUID customerId,
    String customerName,
    String gstNumber,
    // ... other fields
) {}
```

**Status Tracking**:
- **Table**: `vsms_customer.customers`
- **Field**: `approval_status ENUM('PENDING', 'APPROVED', 'REJECTED')`
- **Trigger**: Customer approval/rejection action
- **Impact**: Unlocks order creation for approved customers

### 2. SalesOrderActivated Event
**Business Purpose**: Order lifecycle progression after cost approval
```java
public record SalesOrderActivated(
    String orderCode,
    UUID customerId,
    Long companyId,
    BigDecimal totalAmount,
    LocalDateTime activatedAt
) {}
```

**Status Tracking**:
- **Table**: `vsms_sales.trn_order_header`
- **Field**: `status VARCHAR(20) DEFAULT 'DRAFT'`
- **Trigger**: Cost approval completion
- **Impact**: Enables inventory allocation and invoice generation

### 3. SalesOrderCostApproved Event
**Business Purpose**: Cost estimation approval completion
```java
public record SalesOrderCostApproved(
    String orderCode,
    UUID customerId,
    BigDecimal approvedAmount,
    LocalDateTime approvedAt
) {}
```

**Status Tracking**:
- **Table**: `vsms_cost.trn_cost_header`
- **Field**: `status VARCHAR(20) DEFAULT 'PENDING'`
- **Trigger**: Cost approval workflow completion
- **Impact**: Triggers order activation

### 4. GrnApproved Event
**Business Purpose**: Goods Receipt Note approval for stock intake
```java
public record GrnApproved(
    Long purchaseHeaderId,
    Long itemId,
    Double quantity,
    LocalDateTime approvedAt
) {}
```

**Status Tracking**:
- **Table**: `vsms_purchase.trn_purchase_header`
- **Field**: `status VARCHAR(20) DEFAULT 'DRAFT'`
- **Trigger**: GRN approval action
- **Impact**: Creates stock-in transactions in inventory

### 5. InvoiceGenerated Event
**Business Purpose**: Invoice creation completion
```java
public record InvoiceGenerated(
    Long invoiceId,
    String invoiceNumber,
    String orderCode,
    UUID customerId,
    BigDecimal totalAmount,
    LocalDateTime generatedAt
) {}
```

**Status Tracking**:
- **Table**: `vsms_fulfilment.trn_bill_header`
- **Field**: `status VARCHAR(20) DEFAULT 'DRAFT'`
- **Trigger**: Invoice approval and generation
- **Impact**: Completes order-to-cash cycle

---

## 🔄 Complete Business Process Flow

### Order-to-Cash Cycle Status Tracking

```mermaid
stateDiagram-v2
    [*] --> CustomerCreated: Customer Registration
    CustomerCreated --> CustomerPending: Status: PENDING

    CustomerPending --> CustomerApproved: Approval Process
    CustomerApproved --> OrderCreationEnabled: Status: APPROVED
    CustomerPending --> CustomerRejected: Rejection Process
    CustomerRejected --> [*]: Status: REJECTED

    OrderCreationEnabled --> SalesOrderDraft: Order Creation
    SalesOrderDraft --> CostEstimationPending: Status: DRAFT

    CostEstimationPending --> CostApproved: Cost Approval
    CostApproved --> SalesOrderActive: Status: APPROVED → ACTIVE
    CostEstimationPending --> CostRejected: Cost Rejection
    CostRejected --> SalesOrderCancelled: Status: REJECTED → CANCELLED

    SalesOrderActive --> InventoryAllocated: Stock Allocation
    SalesOrderActive --> InvoiceGenerated: Invoice Creation
    InvoiceGenerated --> PaymentProcessed: Status: APPROVED
    PaymentProcessed --> [*]: Order Complete

    SalesOrderActive --> GoodsDelivered: Delivery Process
    GoodsDelivered --> [*]: Fulfillment Complete
```

### Status Field Mapping Across Services

| Business Process | Service | Table | Status Field | Values |
|------------------|---------|-------|--------------|---------|
| **Customer Lifecycle** | Customer | `customers` | `approval_status` | `PENDING` → `APPROVED`/`REJECTED` |
| **Sales Order Flow** | Sales | `trn_order_header` | `status` | `DRAFT` → `ACTIVE` → `CANCELLED` |
| **Cost Approval** | Cost | `trn_cost_header` | `status` | `PENDING` → `APPROVED` → `REJECTED` |
| **Purchase Process** | Purchase | `trn_purchase_header` | `status` | `DRAFT` → `APPROVED` |
| **Invoice Process** | Fulfilment | `trn_bill_header` | `status` | `DRAFT` → `APPROVED` |
| **Payment Status** | Fulfilment | `trn_bill_header` | `payment_status` | `PENDING` → `PAID` |

---

## 📋 Service Interaction Matrix

| Publisher → Consumer | Customer Service | Sales Service | Purchase Service | Inventory Service | Cost Service | Fulfilment Service |
|---------------------|------------------|---------------|------------------|-------------------|--------------|-------------------|
| **Customer Service** | - | ✅ CustomerApproved | - | - | - | - |
| **Sales Service** | ✅ (Feign) | - | - | - | - | ✅ (Feign) |
| **Purchase Service** | - | - | - | ✅ GrnApproved | - | - |
| **Inventory Service** | - | - | - | - | - | - |
| **Cost Service** | - | ✅ SalesOrderCostApproved | - | - | - | - |
| **Fulfilment Service** | - | - | - | - | - | - |

**Legend**:
- ✅ **Event Communication** (RabbitMQ)
- ✅ **(Feign)** Direct API calls for data validation
- `-` No direct communication

---

## 🔧 Technical Implementation Details

### RabbitMQ Exchange Configuration
```yaml
# Exchanges defined in docker-compose.yml or service configs
exchanges:
  - name: vsms.customer
    type: topic
  - name: vsms.sales
    type: topic
  - name: vsms.purchase
    type: topic
  - name: vsms.cost
    type: topic
  - name: vsms.fulfilment
    type: topic
  - name: vsms.inventory
    type: topic
```

### Event Publishing Pattern
```java
// Standard event publishing pattern used across services
public interface EventPublisher {
    void publish(String exchange, String routingKey, Object event);
}

// Example implementation
eventPublisher.publish("vsms.customer", "customer.approved",
    new CustomerApproved(customerId, customerName, gstNumber, approvedAt));
```

### Event Consumption Pattern
```java
// Standard event consumption pattern
@RabbitListener(bindings = @QueueBinding(
    exchange = @Exchange("vsms.customer"),
    key = "customer.approved",
    queue = @Queue("customer-approved-queue")
))
public void handleCustomerApproved(CustomerApproved event) {
    // Business logic for customer approval
    updateOrderCreationEligibility(event.customerId());
}
```

---

## 🎯 Key Insights & Recommendations

### Current Architecture Strengths
1. **Event-Driven Decoupling**: Services communicate via events, not direct API calls
2. **Database Isolation**: Each service owns its data, preventing tight coupling
3. **Asynchronous Processing**: Non-blocking event processing improves performance
4. **Business Process Visibility**: Complete audit trail through event logs

### Areas for Enhancement
1. **Event Persistence**: Events should be stored in an event store for replay capabilities
2. **Event Schema Evolution**: Versioning strategy needed for event schema changes
3. **Event Monitoring**: Real-time event processing metrics and alerting
4. **Saga Orchestration**: Distributed transaction management for complex workflows

### Missing Event Consumers
- **InvoiceGenerated**: No current consumers identified in the architecture
- **Potential Consumers**: Accounting systems, external integrations, analytics platforms

### Status Synchronization
- **Cross-Service Status**: Consider event-driven status synchronization
- **Eventual Consistency**: Implement compensation patterns for status conflicts
- **Status Validation**: Add event-driven validation rules between services

---

## 📈 Business Value Delivered

### Operational Benefits
- **Real-time Visibility**: Track order status across the entire business process
- **Automated Workflows**: Event-driven triggers reduce manual intervention
- **System Resilience**: Loose coupling prevents cascade failures
- **Audit Compliance**: Complete transaction history for regulatory requirements

### Technical Benefits
- **Scalability**: Event-driven architecture supports horizontal scaling
- **Maintainability**: Service boundaries reduce complexity
- **Testability**: Isolated services with contract-based event interfaces
- **Monitoring**: Comprehensive observability through event streams

---

## 🔮 Future State Recommendations

### Enhanced Event Architecture
1. **Event Sourcing**: Implement event store for complete system state reconstruction
2. **CQRS Pattern**: Separate read/write models for optimized queries
3. **Event Streaming**: Real-time event processing with Apache Kafka
4. **Saga Orchestration**: Distributed transaction management across services

### Advanced Analytics
1. **Event-driven Dashboards**: Real-time business metrics
2. **Predictive Analytics**: ML models trained on event patterns
3. **Business Intelligence**: Cross-service reporting and insights

This comprehensive event communication analysis provides a solid foundation for understanding the current VSMS architecture and planning future enhancements toward a more robust, event-driven enterprise system.
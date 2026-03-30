# VSMS Microservices - Vehicle Sales Management System

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Architecture](#architecture)
  - [Microservices Pattern](#microservices-pattern)
  - [Microservices Architecture Definition](#microservices-architecture-definition)
  - [High-Level Architecture Diagram](#high-level-architecture-diagram)
- [Technology Stack](#technology-stack)
- [Services](#services)
- [Infrastructure](#infrastructure)
- [Getting Started](#getting-started)
- [Development Guide](#development-guide)
- [API Documentation](#api-documentation)
- [Event-Driven Architecture](#event-driven-architecture)
- [Database Schema](#database-schema)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

**VSMS (Vehicle Sales Management System)** is a microservices-based application for managing vehicle sales operations. The system handles the complete sales lifecycle including customer management, inventory tracking, purchase orders, cost estimation, invoicing, and reporting.

### Key Features
- **Customer Management**: Customer lifecycle with approval workflow
- **Inventory Management**: Stock tracking and transactions
- **Sales Orders**: Order lifecycle with GST calculations
- **Purchase Management**: Vendor procurement and GRN
- **Cost Estimation**: Cost approval workflow
- **Fulfilment**: Invoice and delivery challan generation
- **HR Management**: Employee and payroll management
- **Reporting**: Cross-service analytics and reports

---

## 🏗️ Architecture

### Microservices Pattern
The system follows a **microservices architecture** with:
- **API Gateway**: Single entry point for all client requests
- **Service Discovery**: Eureka-based service registration
- **Event-Driven Communication**: RabbitMQ for async messaging
- **Database per Service**: Each service owns its database
- **Circuit Breaker**: Resilience4j for fault tolerance
- **Distributed Tracing**: OpenTelemetry with Zipkin
- **Centralized Security**: Shared security configuration (JWT, CORS, HSTS) across all services

### Microservices Architecture Definition

This project implements a **Domain-Driven Design (DDD) based Microservices Architecture** for vehicle sales management. The services are categorized into three main types:

#### 1. Infrastructure Services (Cross-Cutting Concerns)

| Service | Type | Purpose |
|---------|------|--------|
| `api-gateway` | **API Gateway** | Single entry point, routing, JWT validation, load balancing |
| `eureka-server` | **Service Discovery** | Service registration and discovery |
| `shared/vsms-common` | **Shared Library** | Common utilities, exceptions, security config (CORS, HSTS, JWT) |

#### 2. Core Domain Services (Business Logic)

| Service | Domain | Bounded Context |
|---------|--------|------------------|
| `auth-service` | **Identity & Access Management** | User authentication, authorization, JWT issuance |
| `master-service` | **Reference Data** | Items, categories, locations, company data |
| `customer-service` | **Customer Management** | Customer lifecycle with approval workflow |
| `sales-service` | **Sales Orders** | Order lifecycle, offers, GST calculations |
| `purchase-service` | **Procurement** | Purchase orders, GRN, vendor management |
| `inventory-service` | **Stock Management** | Stock transactions, balance tracking |
| `cost-service` | **Cost Estimation** | Cost approval workflow |
| `fulfilment-service` | **Invoicing & Delivery** | Invoices, delivery challans |
| `hr-service` | **Human Resources** | Employee management, payroll |

#### 3. Supporting Services (Cross-Domain)

| Service | Technology | Purpose |
|---------|------------|--------|
| `report-service` | **Go** | Cross-service analytics and reporting |
| `notification-service` | **Node.js** | Event-driven email/SMS notifications |
| `drs-service` | **Node.js + Prisma** | Document generation and PDF creation |

#### Architectural Patterns Implemented

**Database per Service:**
- Each service owns its database schema
- Data isolation and autonomy
- Independent schema evolution

**Event-Driven Architecture:**
- RabbitMQ for async communication
- Topic exchanges for domain events
- Dead Letter Queues for failed messages
- Saga Pattern for distributed transactions

**API Gateway Pattern:**
- Single entry point via `api-gateway`
- JWT validation against `auth-service` JWKS
- Route-based load balancing via Eureka

**Circuit Breaker Pattern:**
- Resilience4j for fault tolerance
- Prevents cascade failures
- Graceful degradation

**Service Discovery:**
- Eureka for service registration
- Dynamic service location
- Health monitoring

**Distributed Tracing:**
- OpenTelemetry with Zipkin
- Cross-service request tracking
- Performance monitoring

**Polyglot Microservices:**
- Java 21 (Primary - 11 services)
- Go 1.21+ (report-service)
- Node.js 18+ (notification-service, drs-service)

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTS                              │
│                    (Web/Mobile Apps)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    API GATEWAY (8080)                       │
│              Spring Cloud Gateway + JWT                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   EUREKA SERVER (8761)                      │
│                  Service Discovery                          │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ AUTH SERVICE │    │MASTER SERVICE│    │CUSTOMER SVC  │
│    (8090)    │    │    (8083)    │    │    (8082)    │
└──────────────┘    └──────────────┘    └──────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│SALES SERVICE │    │PURCHASE SVC  │    │INVENTORY SVC │
│    (8085)    │    │    (8086)    │    │    (8087)    │
└──────────────┘    └──────────────┘    └──────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│COST SERVICE  │    │FULFILMENT SVC│    │  HR SERVICE  │
│    (8089)    │    │    (8088)    │    │    (8084)    │
└──────────────┘    └──────────────┘    └──────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│REPORT SERVICE│    │NOTIFICATION  │    │  DRS SERVICE │
│    (8091)    │    │   (3001)     │    │    (3002)    │
└──────────────┘    └──────────────┘    └──────────────┘
```

---

## 🛠️ Technology Stack

### Backend Services
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Primary language for most services |
| Spring Boot | 3.4.3 | Application framework |
| Spring Cloud | 2024.0.0 | Microservices infrastructure |
| Spring Security | OAuth2 Resource Server + CORS + HSTS | Authentication & Authorization |
| Spring Data JPA | Hibernate | Database ORM |
| Flyway | Latest | Database migrations |
| Lombok | Latest | Boilerplate reduction |
| MapStruct | 1.5.5.Final | Object mapping |

### Infrastructure
| Technology | Version | Purpose |
|------------|---------|---------|
| MySQL | 8.0 | Primary database |
| RabbitMQ | 3.13 | Message broker |
| Redis | 7-alpine | Caching (optional) |
| Zipkin | 3 | Distributed tracing |
| Eureka | Latest | Service discovery |
| Docker | Latest | Containerization |

### Additional Services
| Technology | Purpose |
|------------|---------|
| Node.js | notification-service, drs-service |
| Go | report-service |
| Prisma | drs-service ORM |

---

## 🚀 Services

### 1. API Gateway (Port: 8080)
**Purpose**: Single entry point for all client requests

**Responsibilities**:
- Route requests to downstream services
- Validate JWT tokens against auth-service JWKS
- Apply comprehensive security headers (CORS, HSTS, CSRF protection)
- Load balancing via Eureka
- Circuit breaking with Resilience4j

**Routes**:
- `/api/v1/auth/**` → auth-service
- `/api/v1/master/**` → master-service
- `/api/v1/customers/**` → customer-service
- `/api/v1/sales-orders/**` → sales-service
- `/api/v1/offers/**` → sales-service
- `/api/v1/purchase/**` → purchase-service
- `/api/v1/stock/**` → inventory-service
- `/api/v1/invoices/**` → fulfilment-service
- `/api/v1/delivery-challans/**` → fulfilment-service
- `/api/v1/cost/**` → cost-service
- `/api/v1/hr/**` → hr-service
- `/api/v1/drs/**` → drs-service
- `/api/v1/reports/**` → report-service

---

### 2. Eureka Server (Port: 8761)
**Purpose**: Service registry and discovery

**Responsibilities**:
- Register all microservices
- Provide service discovery for inter-service communication
- Health monitoring dashboard

---

### 3. Auth Service (Port: 8090)
**Purpose**: Identity and access management

**Responsibilities**:
- User CRUD operations
- Role and permission management
- JWT token issuance via Spring Authorization Server
- Login history tracking
- Financial year and document serial number management

**Database**: `vsms_auth`

**Tables Owned**:
- `adm_users`, `adm_roles`, `adm_permissions`
- `adm_menus`, `adm_sub_menus`, `adm_modules`
- `adm_login_history`, `adm_user_menu_permission`
- `adm_user_type`, `adm_user_type_menu_detail`
- `adm_document`, `adm_doc_serial_number`
- `adm_setup`, `adm_status`, `adm_supply_type`
- `adm_year`, `adm_year_code`

---

### 4. Master Service (Port: 8083)
**Purpose**: Reference data service

**Responsibilities**:
- Item and product catalog
- Unit of measure management
- Category and sub-category management
- Location and branch management
- Company master data
- Geographic reference data

**Database**: `vsms_master`

**Tables Owned**:
- `mst_company`, `mst_item`, `mst_uom`
- `mst_category`, `mst_subcategory`, `mst_location`
- `mst_state`, `mst_country`, `mst_currency`
- `mst_consignee`, `mst_term`, `mst_industry`
- `mst_signature`, `mst_call_type`

---

### 5. Customer Service (Port: 8082)
**Purpose**: Customer lifecycle management

**Responsibilities**:
- Customer CRUD operations
- Approval workflow: PENDING → APPROVED/REJECTED
- Uniqueness enforcement (customerName, gstNumber, panNumber)
- Publishes CustomerApproved event on approval

**Database**: `vsms_customer`

**Tables Owned**:
- `customers`

**Events Published**:
- `CustomerApproved` (routing key: `customer.approved`)

---

### 6. Sales Service (Port: 8085)
**Purpose**: Sales order lifecycle management

**Responsibilities**:
- Sales order CRUD and lifecycle (DRAFT → ACTIVE → CANCELLED)
- Offer/quotation management
- GST calculations (CGST, SGST, IGST, taxable amount)
- Validates customer is APPROVED before order creation
- Activates order after cost approval

**Database**: `vsms_sales`

**Tables Owned**:
- `trn_order_header`, `trn_order_detail`, `trn_order_others`, `trn_order_code`
- `trn_offer_hdr`, `trn_offer_dtl`, `trn_offer_tc`, `trn_offer_encl`

**Events Published**:
- `SalesOrderActivated` (routing key: `sales.order.activated`)

**Events Consumed**:
- `SalesOrderCostApproved` from cost-service

**Feign Clients**:
- `CustomerServiceClient` → customer-service
- `MasterServiceClient` → master-service

---

### 7. Purchase Service (Port: 8086)
**Purpose**: Vendor procurement lifecycle

**Responsibilities**:
- Purchase indent management
- Purchase order lifecycle
- GRN (Goods Receipt Note) approval
- Vendor master management
- Publishes GrnApproved event on GRN approval

**Database**: `vsms_purchase`

**Tables Owned**:
- `trn_purchase_header`, `trn_purchase_detail`, `trn_purchase_serial`
- `mst_vendor`, `mst_part_number`
- `trn_indent_header`, `trn_indent_detail`
- `trn_po_header`, `trn_po_detail`, `trn_po_terms`, `trn_po_enclosure`, `trn_po_others`

**Events Published**:
- `GrnApproved` (routing key: `grn.approved`)

---

### 8. Inventory Service (Port: 8087)
**Purpose**: Stock ledger and transactions

**Responsibilities**:
- Stock transaction CRUD
- Stock balance queries per item
- Processes GrnApproved events → creates stock-in transactions
- Processes SalesOrderActivated events → creates stock-out transactions

**Database**: `vsms_inventory`

**Tables Owned**:
- `mst_stock`, `mst_stock_type`
- `trn_stock_hdr`, `trn_stock_dtl`, `trn_stock_type`

**Events Consumed**:
- `GrnApproved` from purchase-service
- `SalesOrderActivated` from sales-service

**Feign Clients**:
- `MasterServiceClient` → master-service

---

### 9. Cost Service (Port: 8089)
**Purpose**: Cost estimation and approval workflow

**Responsibilities**:
- Cost header CRUD for sales orders
- Additional cost line items management
- Cost approval workflow: PENDING → APPROVED/REJECTED
- On approval: publish SalesOrderCostApproved event, trigger order activation

**Database**: `vsms_cost`

**Tables Owned**:
- `trn_cost_header`, `trn_cost_additional`, `trn_cost_approve`

**Events Published**:
- `SalesOrderCostApproved` (routing key: `cost.approved`)

**Feign Clients**:
- `SalesServiceClient` → sales-service

---

### 10. Fulfilment Service (Port: 8088)
**Purpose**: Delivery challans and invoices

**Responsibilities**:
- Delivery challan management
- Invoice/bill generation
- Invoice types: CASH, CREDIT, PROFORMA, TAX, EXPORT
- Validates sales order is ACTIVE before invoice creation
- Publishes InvoiceGenerated event on invoice creation

**Database**: `vsms_fulfilment`

**Tables Owned**:
- `trn_delv_chln_hdr`, `trn_delv_chln_dtl`, `trn_delv_chln_srl`
- `trn_bill_header`, `trn_bill_detail`, `trn_bill_serial`, `trn_bill_others`

**Events Published**:
- `InvoiceGenerated` (routing key: `invoice.generated`)

**Events Consumed**:
- `SalesOrderActivated` from sales-service

**Feign Clients**:
- `SalesServiceClient` → sales-service

---

### 11. HR Service (Port: 8084)
**Purpose**: Employee and payroll management

**Responsibilities**:
- Employee master management
- Attendance tracking
- Advance management
- Payroll earning/deduction management

**Database**: `vsms_hr`

**Tables Owned**:
- `mst_employee`, `mst_employee_attendance`
- `mst_employee_earning_deduction`

---

### 12. Report Service (Port: 8091)
**Purpose**: Cross-service analytics and reporting

**Responsibilities**:
- Aggregate data from multiple services
- Generate business reports
- Dashboard analytics

**Technology**: Go

**Feign Clients**:
- `SalesClient` → sales-service
- `FulfilmentClient` → fulfilment-service
- `HrClient` → hr-service

---

### 13. Notification Service (Port: 3001)
**Purpose**: Event-driven notifications

**Responsibilities**:
- Consume domain events from RabbitMQ
- Send email/SMS notifications
- Notification templates management

**Technology**: Node.js

---

### 14. DRS Service (Port: 3002)
**Purpose**: Document Request Service

**Responsibilities**:
- Document generation
- PDF creation
- Document storage

**Technology**: Node.js with Prisma ORM

---

## 🏗️ Infrastructure

### Docker Compose Services

| Service | Port | Purpose |
|---------|------|---------|
| eureka-server | 8761 | Service discovery |
| rabbitmq | 5672, 15672 | Message broker |
| redis | 6379 | Caching |
| zipkin | 9411 | Distributed tracing |
| mysql | 3306 | Primary database |
| api-gateway | 8080 | API gateway |
| auth-service | 8090 | Authentication |
| master-service | 8083 | Reference data |
| customer-service | 8082 | Customer management |
| sales-service | 8085 | Sales orders |
| purchase-service | 8086 | Purchase orders |
| inventory-service | 8087 | Inventory management |
| fulfilment-service | 8088 | Invoicing |
| cost-service | 8089 | Cost estimation |
| hr-service | 8084 | HR management |
| report-service | 8091 | Reporting |
| notification-service | 3001 | Notifications |
| drs-service | 3002 | Document service |

### Database Architecture

Each service has its own database schema:

```
vsms_auth          - Authentication & authorization
vsms_master        - Reference/master data
vsms_customer      - Customer management
vsms_sales         - Sales orders & offers
vsms_purchase      - Purchase orders & GRN
vsms_inventory     - Stock management
vsms_fulfilment    - Invoices & delivery challans
vsms_cost          - Cost estimation
vsms_hr            - HR & payroll
vsms_drs           - Document service
```

---

## 🚀 Getting Started

#### Quick Start with Application Scripts
```bash
# Start the entire application stack
./start-application.sh

# Stop the entire application stack
./stop-application.sh

# Manage individual services (recommended for development)
./manage-service.sh start customer-service
./manage-service.sh restart api-gateway
./manage-service.sh status
```

> 📖 **See [APPLICATION-MANAGEMENT.md](APPLICATION-MANAGEMENT.md)** for detailed script usage and development workflows.

### Prerequisites
- Docker & Docker Compose
- Java 21
- Gradle 9.3.0+
- Node.js 18+ (for notification-service, drs-service)
- Go 1.21+ (for report-service)

### Environment Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd vsms-microservices
```

2. **Configure environment variables**
```bash
cp .env.example .env
# Edit .env with your configuration
```

3. **Start all services**
```bash
docker-compose up -d
```

4. **Verify services are running**
```bash
docker-compose ps
```

### Access Points

| Service | URL | Purpose |
|---------|-----|---------|
| API Gateway | http://localhost:8080 | Main entry point |
| Eureka Dashboard | http://localhost:8761 | Service registry |
| RabbitMQ Management | http://localhost:15672 | Message broker UI |
| Zipkin | http://localhost:9411 | Distributed tracing |

---

## 💻 Development Guide

### Project Structure

```
vsms-microservices/
├── api-gateway/              # Spring Cloud Gateway
├── eureka-server/            # Service discovery
├── services/
│   ├── auth-service/         # Authentication
│   ├── master-service/       # Reference data
│   ├── customer-service/     # Customer management
│   ├── sales-service/        # Sales orders
│   ├── purchase-service/     # Purchase orders
│   ├── inventory-service/    # Inventory management
│   ├── fulfilment-service/   # Invoicing
│   ├── cost-service/         # Cost estimation
│   ├── hr-service/           # HR management
│   ├── report-service/       # Reporting (Go)
│   ├── notification-service/ # Notifications (Node.js)
│   └── drs-service/          # Document service (Node.js)
├── shared/
│   ├── vsms-common/          # Shared Java library
│   └── vsms-events/          # Event definitions
├── infra/
│   └── mysql/init/           # Database initialization
├── docker-compose.yml        # Docker orchestration
└── .env.example              # Environment template
```

### Building a Service

```bash
cd services/<service-name>
./gradlew build
```

### Running a Service Locally

```bash
cd services/<service-name>
./gradlew bootRun
```

### Running Tests

```bash
cd services/<service-name>
./gradlew test
```

### Code Style
- Use Lombok for boilerplate reduction
- Use MapStruct for object mapping
- Follow Spring Boot best practices
- Use Swagger/OpenAPI for API documentation

---

## 📚 API Documentation

### Swagger/OpenAPI
Each service exposes Swagger UI at:
```
http://localhost:<port>/swagger-ui.html
```

### API Endpoints

#### Auth Service (8090)
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/users` - Create user
- `GET /api/v1/auth/users/{id}` - Get user by ID
- `GET /api/v1/auth/users` - Get all users
- `PUT /api/v1/auth/users/{id}` - Update user
- `DELETE /api/v1/auth/users/{id}` - Delete user
- `GET /api/v1/auth/jwks` - Get JWKS

#### Customer Service (8082)
- `POST /api/v1/customers` - Create customer
- `GET /api/v1/customers/{id}` - Get customer by ID
- `GET /api/v1/customers` - Get all customers
- `PUT /api/v1/customers/{id}` - Update customer
- `DELETE /api/v1/customers/{id}` - Delete customer
- `PATCH /api/v1/customers/{id}/approve` - Approve customer
- `PATCH /api/v1/customers/{id}/reject` - Reject customer

#### Sales Service (8085)
- `POST /api/v1/sales-orders` - Create sales order
- `GET /api/v1/sales-orders/{id}` - Get sales order by ID
- `GET /api/v1/sales-orders` - Get all sales orders
- `PUT /api/v1/sales-orders/{id}` - Update sales order
- `DELETE /api/v1/sales-orders/{id}` - Delete sales order
- `PATCH /api/v1/sales-orders/{id}/activate` - Activate order

#### Purchase Service (8086)
- `POST /api/v1/purchase/orders` - Create purchase order
- `GET /api/v1/purchase/orders/{id}` - Get purchase order by ID
- `GET /api/v1/purchase/orders` - Get all purchase orders
- `PUT /api/v1/purchase/orders/{id}` - Update purchase order
- `DELETE /api/v1/purchase/orders/{id}` - Delete purchase order
- `PATCH /api/v1/purchase/orders/{id}/approve-grn` - Approve GRN

#### Inventory Service (8087)
- `POST /api/v1/stock/transactions` - Create stock transaction
- `GET /api/v1/stock/transactions/{id}` - Get stock transaction by ID
- `GET /api/v1/stock/transactions` - Get all stock transactions
- `PUT /api/v1/stock/transactions/{id}` - Update stock transaction
- `DELETE /api/v1/stock/transactions/{id}` - Delete stock transaction
- `GET /api/v1/stock/balance/{itemId}` - Get stock balance

#### Cost Service (8089)
- `POST /api/v1/cost` - Create cost header
- `GET /api/v1/cost/{id}` - Get cost header by ID
- `GET /api/v1/cost` - Get all cost headers
- `PUT /api/v1/cost/{id}` - Update cost header
- `DELETE /api/v1/cost/{id}` - Delete cost header
- `PATCH /api/v1/cost/{id}/approve` - Approve cost
- `PATCH /api/v1/cost/{id}/reject` - Reject cost

#### Fulfilment Service (8088)
- `POST /api/v1/invoices` - Create invoice
- `GET /api/v1/invoices/{id}` - Get invoice by ID
- `GET /api/v1/invoices` - Get all invoices
- `PUT /api/v1/invoices/{id}` - Update invoice
- `DELETE /api/v1/invoices/{id}` - Delete invoice

#### HR Service (8084)
- `POST /api/v1/hr/employees` - Create employee
- `GET /api/v1/hr/employees/{id}` - Get employee by ID
- `GET /api/v1/hr/employees` - Get all employees
- `PUT /api/v1/hr/employees/{id}` - Update employee
- `DELETE /api/v1/hr/employees/{id}` - Delete employee
- `POST /api/v1/hr/attendance` - Record attendance
- `GET /api/v1/hr/attendance` - Get attendance
- `POST /api/v1/hr/earning-deductions` - Create earning/deduction
- `GET /api/v1/hr/payroll` - Get payroll data

---

## 🔄 Event-Driven Architecture

### RabbitMQ Exchanges

| Exchange | Type | Purpose |
|----------|------|---------|
| `vsms.customer` | Topic | Customer domain events |
| `vsms.sales` | Topic | Sales domain events |
| `vsms.purchase` | Topic | Purchase domain events |
| `vsms.cost` | Topic | Cost domain events |
| `vsms.fulfilment` | Topic | Fulfilment domain events |
| `vsms.inventory` | Topic | Inventory domain events |

### Event Flow

#### Order Activation Saga
```
1. Customer Created (PENDING)
2. Customer Approved → CustomerApproved event
3. Sales Order Created (DRAFT)
4. Cost Header Created (PENDING)
5. Cost Approved → SalesOrderCostApproved event
6. Sales Order Activated → SalesOrderActivated event
7. Inventory Reserved (stock-out)
8. Invoice Created → InvoiceGenerated event
```

#### Purchase Order Flow
```
1. Purchase Order Created
2. GRN Approved → GrnApproved event
3. Inventory Updated (stock-in)
```

### Dead Letter Queues
Each service has a dead letter queue for failed messages:
- `vsms.customer.dlq`
- `vsms.sales.dlq`
- `vsms.purchase.dlq`
- `vsms.cost.dlq`
- `vsms.fulfilment.dlq`
- `vsms.inventory.dlq`

---

## 🗄️ Database Schema

### Authentication (vsms_auth)
- `adm_users` - User accounts
- `adm_roles` - User roles
- `adm_permissions` - Role permissions
- `adm_menus` - Menu structure
- `adm_sub_menus` - Sub-menu items
- `adm_modules` - Application modules
- `adm_login_history` - Login audit trail
- `adm_user_menu_permission` - User-menu permissions
- `adm_user_type` - User types
- `adm_user_type_menu_detail` - User type menu details
- `adm_document` - Document types
- `adm_doc_serial_number` - Document serial numbers
- `adm_setup` - System setup
- `adm_status` - Status definitions
- `adm_supply_type` - Supply types
- `adm_year` - Financial years
- `adm_year_code` - Year codes

### Master Data (vsms_master)
- `mst_company` - Company master
- `mst_item` - Item/product catalog
- `mst_uom` - Unit of measure
- `mst_category` - Item categories
- `mst_subcategory` - Item subcategories
- `mst_location` - Locations/branches
- `mst_state` - States
- `mst_country` - Countries
- `mst_currency` - Currencies
- `mst_consignee` - Consignees
- `mst_term` - Terms and conditions
- `mst_industry` - Industries
- `mst_signature` - Signatures
- `mst_call_type` - Call types

### Customer (vsms_customer)
- `customers` - Customer master

### Sales (vsms_sales)
- `trn_order_header` - Sales order headers
- `trn_order_detail` - Sales order line items
- `trn_order_others` - Sales order additional info
- `trn_order_code` - Order codes
- `trn_offer_hdr` - Offer headers
- `trn_offer_dtl` - Offer details
- `trn_offer_tc` - Offer terms
- `trn_offer_encl` - Offer enclosures

### Purchase (vsms_purchase)
- `trn_purchase_header` - Purchase order headers
- `trn_purchase_detail` - Purchase order details
- `trn_purchase_serial` - Purchase serial numbers
- `mst_vendor` - Vendor master
- `mst_part_number` - Part numbers
- `trn_indent_header` - Indent headers
- `trn_indent_detail` - Indent details
- `trn_po_header` - PO headers
- `trn_po_detail` - PO details
- `trn_po_terms` - PO terms
- `trn_po_enclosure` - PO enclosures
- `trn_po_others` - PO additional info

### Inventory (vsms_inventory)
- `mst_stock` - Stock master
- `mst_stock_type` - Stock types
- `trn_stock_hdr` - Stock transaction headers
- `trn_stock_dtl` - Stock transaction details
- `trn_stock_type` - Stock transaction types

### Fulfilment (vsms_fulfilment)
- `trn_delv_chln_hdr` - Delivery challan headers
- `trn_delv_chln_dtl` - Delivery challan details
- `trn_delv_chln_srl` - Delivery challan serial numbers
- `trn_bill_header` - Invoice headers
- `trn_bill_detail` - Invoice details
- `trn_bill_serial` - Invoice serial numbers
- `trn_bill_others` - Invoice additional info

### Cost (vsms_cost)
- `trn_cost_header` - Cost headers
- `trn_cost_additional` - Additional cost items
- `trn_cost_approve` - Cost approval records

### HR (vsms_hr)
- `mst_employee` - Employee master
- `mst_employee_attendance` - Attendance records
- `mst_employee_earning_deduction` - Payroll components

---

## 🚢 Deployment

### Docker Build

```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build <service-name>
```

### Docker Run

```bash
# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d <service-name>

# View logs
docker-compose logs -f <service-name>
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `changeme` |
| `RABBITMQ_USER` | RabbitMQ username | `vsms` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `changeme` |
| `EUREKA_URL` | Eureka server URL | `http://eureka-server:8761/eureka/` |
| `JWT_JWKS_URI` | JWT JWKS endpoint | `http://auth-service:8090/api/v1/auth/jwks` |

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Service Not Registering with Eureka
**Symptoms**: Service starts but doesn't appear in Eureka dashboard

**Solution**:
- Check Eureka URL in application.yml
- Verify network connectivity
- Check service logs for registration errors

#### 2. Database Connection Failed
**Symptoms**: Service fails to start with database error

**Solution**:
- Verify MySQL is running
- Check database credentials in .env
- Ensure database exists (check init script)

#### 3. RabbitMQ Connection Failed
**Symptoms**: Service fails to start with RabbitMQ error

**Solution**:
- Verify RabbitMQ is running
- Check RabbitMQ credentials in .env
- Ensure RabbitMQ health check passes

#### 4. JWT Validation Failed
**Symptoms**: 401 Unauthorized errors

**Solution**:
- Verify auth-service is running
- Check JWT_JWKS_URI configuration
- Ensure JWKS endpoint is accessible

#### 5. Circuit Breaker Open
**Symptoms**: 503 Service Unavailable errors

**Solution**:
- Check downstream service health
- Review circuit breaker configuration
- Check Resilience4j metrics

### Logs

```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs <service-name>

# Follow logs in real-time
docker-compose logs -f <service-name>
```

### Health Checks

```bash
# Check all services
curl http://localhost:8080/actuator/health

# Check specific service
curl http://localhost:<port>/actuator/health
```

---

## 📝 Development Status

### Completed
- ✅ Project structure and architecture
- ✅ Docker Compose configuration
- ✅ API Gateway with routing
- ✅ Eureka Server for service discovery
- ✅ Database initialization scripts
- ✅ Service skeletons with controllers
- ✅ RabbitMQ configuration
- ✅ Circuit breaker setup
- ✅ Distributed tracing setup

### In Progress
- 🔄 Entity field migration from monolith
- 🔄 Service implementation
- 🔄 Event publishing and consuming
- 🔄 Flyway migrations

### TODO
- ⏳ Spring Authorization Server configuration
- ⏳ JWT token issuance
- ⏳ Password hashing (BCrypt)
- ⏳ Role-based authorization
- ⏳ Caching with Redis
- ⏳ Unit and integration tests
- ⏳ API documentation completion

---

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Write tests
4. Submit a pull request

### Code Standards
- Follow Spring Boot best practices
- Use Lombok for boilerplate
- Use MapStruct for mapping
- Write meaningful commit messages
- Add Swagger annotations to controllers

---

## 📄 License

[Your License Here]

---

## 👥 Team

[Your Team Information Here]

---

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check the troubleshooting section

---

**Last Updated**: 2026-03-28
**Version**: 0.0.1-SNAPSHOT

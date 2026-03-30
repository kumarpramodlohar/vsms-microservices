# VSMS Microservices - Authentication Service & API Gateway Documentation

## Overview

This document provides a comprehensive guide to the VSMS (Vehicle Sales Management System) microservices architecture, focusing on the Authentication Service and API Gateway. The system is built using Spring Boot, Spring Cloud, and follows microservices best practices.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Authentication Service](#authentication-service)
3. [API Gateway](#api-gateway)
4. [Setup and Installation](#setup-and-installation)
5. [Configuration](#configuration)
6. [API Endpoints](#api-endpoints)
7. [Security](#security)
8. [Monitoring & Observability](#monitoring--observability)
9. [Troubleshooting](#troubleshooting)

## Architecture Overview

The VSMS system uses a microservices architecture with:

- **Eureka Server**: Service discovery and registration
- **API Gateway**: Centralized entry point with routing, security, and cross-cutting concerns
- **Authentication Service**: User management, JWT token generation, and authorization
- **Other Services**: Customer, Sales, Inventory, etc. (not covered in this doc)

### Technology Stack

- **Framework**: Spring Boot 3.4.3
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: MySQL 8.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Security**: JWT + OAuth2 Resource Server
- **Circuit Breaker**: Resilience4J
- **Rate Limiting**: Resilience4J Rate Limiter
- **Monitoring**: Spring Boot Actuator + Micrometer + Prometheus

## Authentication Service

The Authentication Service handles user management, authentication, and authorization for the entire system.

### Features

- User registration and management
- JWT token-based authentication
- Role-based access control (RBAC)
- Login history tracking
- Password encryption (BCrypt)
- Menu and permission management

### Database Schema

The service uses MySQL with the following key tables:

- `adm_users`: User accounts
- `adm_user_type`: User roles/types
- `adm_roles`: System roles
- `adm_permissions`: System permissions
- `adm_role_permission`: Role-permission mappings
- `adm_user_role`: User-role mappings
- `adm_menu`: System menus
- `adm_submenu`: Submenus
- `adm_login_history`: User login tracking

### Key Components

#### Controllers
- `UserController`: User management and authentication endpoints
- `RoleController`: Role management
- `PermissionController`: Permission management
- `MenuController`: Menu management
- `LoginHistoryController`: Login history tracking

#### Services
- `UserService`: Business logic for user operations
- `RoleService`: Role management logic
- `PermissionService`: Permission management logic
- `MenuService`: Menu management logic
- `LoginHistoryService`: Login tracking logic

#### Configuration
- `SecurityConfig`: Spring Security configuration with JWT validation
- `ServiceConfig`: General service configuration

## API Gateway

The API Gateway serves as the single entry point for all client requests, providing:

- **Routing**: Intelligent routing to appropriate microservices
- **Security**: JWT authentication and authorization
- **Rate Limiting**: Prevents abuse with configurable limits
- **Circuit Breaker**: Fault tolerance and resilience
- **Logging**: Request/response logging for debugging
- **CORS**: Cross-origin resource sharing configuration
- **Security Headers**: OWASP recommended security headers
- **Error Handling**: Centralized error handling and responses

### Key Features

#### Security
- JWT token validation for protected endpoints
- Public endpoints for login and JWKS
- Security headers (HSTS, XSS protection, etc.)

#### Resilience
- Circuit breaker for each service route
- Rate limiting per IP address
- Automatic fallback handling

#### Monitoring
- Actuator endpoints for health checks
- Metrics collection with Prometheus
- Resilience4J health indicators

## Setup and Installation

### Prerequisites

- Java 21
- MySQL 8.0
- Gradle 7+

### Database Setup

1. Create MySQL databases:
```sql
CREATE DATABASE vsms_auth;
CREATE DATABASE vsms_master;
-- Create other databases as needed
```

2. Run Flyway migrations (automatic on startup) or execute SQL scripts from `infra/mysql/init/`

### Service Startup Order

1. **Eureka Server** (Port 8761)
2. **Authentication Service** (Port 8090)
3. **API Gateway** (Port 8080)
4. **Other Services**

### Running Services

#### Using Gradle (Development)

```bash
# Terminal 1: Eureka Server
cd eureka-server && ./gradlew bootRun

# Terminal 2: Auth Service
cd services/auth-service && ./gradlew bootRun

# Terminal 3: API Gateway
cd api-gateway && ./gradlew bootRun
```

#### Using Docker Compose (Production-like)

```bash
docker-compose up -d mysql eureka-server
docker-compose up -d auth-service api-gateway
```

## Configuration

### Environment Variables

```bash
# Database
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=root
MYSQL_PASSWORD=root

# Eureka
EUREKA_URL=http://localhost:8761/eureka/

# JWT
JWT_JWKS_URI=http://auth-service:8090/api/v1/auth/jwks
```

### Application Properties

#### Auth Service (`application.yml`)
```yaml
server:
  port: 8090

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vsms_auth
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL}
```

#### API Gateway (`application.yml`)
```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origin-patterns: "*"
            allowed-methods: "*"
            allowed-headers: "*"
            allow-credentials: true

resilience4j:
  circuitbreaker:
    instances:
      authCircuitBreaker:
        base-config: default
  ratelimiter:
    instances:
      gatewayRateLimiter:
        base-config: default

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL}

management:
  endpoints:
    web:
      exposure:
        include: health, gateway, prometheus, resilience4j
```

## API Endpoints

### Authentication Service

#### Public Endpoints

- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/jwks` - JSON Web Key Set

#### Protected Endpoints (Require JWT)

- `POST /api/v1/auth/users` - Create user
- `GET /api/v1/auth/users` - Get all users
- `GET /api/v1/auth/users/{id}` - Get user by ID
- `GET /api/v1/auth/users/userid/{userId}` - Get user by userId
- `PUT /api/v1/auth/users/{id}` - Update user
- `DELETE /api/v1/auth/users/{id}` - Delete user
- `GET /api/v1/auth/users/count` - Count active users

#### Menu & Permission Endpoints

- `GET /api/v1/auth/menus` - Get all menus
- `POST /api/v1/auth/menus` - Create menu
- `GET /api/v1/auth/roles` - Get all roles
- `POST /api/v1/auth/roles` - Create role

### API Gateway Routes

All requests to `/api/v1/**` are routed through the gateway:

- `/api/v1/auth/**` → Authentication Service
- `/api/v1/master/**` → Master Service
- `/api/v1/customers/**` → Customer Service
- `/api/v1/sales-orders/**` → Sales Service
- etc.

## Security

### JWT Authentication Flow

1. User logs in via `POST /api/v1/auth/login`
2. Receives JWT access token
3. Includes token in `Authorization: Bearer <token>` header
4. Gateway validates token and routes request
5. Services can extract user info from token

### Security Headers

The gateway adds security headers to all responses:

- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`

### Rate Limiting

- **Global Limit**: 10 requests per minute per IP
- **Burst Capacity**: 20 requests
- Applied at gateway level for all routes

### Circuit Breaker

Each service route has a circuit breaker:

- **Failure Threshold**: 50% failure rate
- **Sliding Window**: 10 calls
- **Minimum Calls**: 5
- **Open State Duration**: 5 seconds

## Monitoring & Observability

### Health Checks

- `GET /actuator/health` - Overall health
- `GET /actuator/health/{component}` - Component-specific health

### Metrics

- `GET /actuator/metrics` - Available metrics
- `GET /actuator/metrics/{metric}` - Specific metric
- Prometheus format available at `/actuator/prometheus`

### Gateway-Specific Endpoints

- `GET /actuator/gateway/routes` - Active routes
- `GET /actuator/gateway/globalfilters` - Global filters
- `GET /actuator/gateway/routefilters` - Route filters

### Resilience4J Endpoints

- `GET /actuator/resilience4j/circuitbreakers` - Circuit breaker status
- `GET /actuator/resilience4j/ratelimiters` - Rate limiter status

## Troubleshooting

### Common Issues

#### Service Won't Start

1. **Database Connection**: Ensure MySQL is running and credentials are correct
2. **Port Conflicts**: Check if ports 8761, 8090, 8080 are available
3. **Dependencies**: Ensure all required services are started in order

#### Authentication Fails

1. **Invalid Credentials**: Verify user exists in database
2. **JWT Token Expired**: Tokens expire after configured time
3. **Wrong JWKS URI**: Ensure gateway can reach auth service

#### Gateway Routing Issues

1. **Service Discovery**: Check Eureka dashboard at `http://localhost:8761`
2. **Service Registration**: Verify services are registered with Eureka
3. **Circuit Breaker Open**: Check `/actuator/resilience4j/circuitbreakers`

#### Rate Limiting

1. **Too Many Requests**: Wait for rate limit reset (60 seconds)
2. **IP Detection**: Ensure gateway can detect client IP

### Logs

Enable debug logging in `application.yml`:

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    com.vsms: DEBUG
    io.github.resilience4j: DEBUG
```

### Database Issues

If Flyway migrations fail:

```bash
# Clean and migrate
./gradlew flywayClean flywayMigrate
```

### Testing APIs

Use curl or Postman:

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userId":"admin","password":"password"}'

# Access protected endpoint
curl -X GET http://localhost:8080/api/v1/auth/users/count \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Development Guidelines

### Code Structure

```
api-gateway/
├── src/main/java/com/vsms/gateway/
│   ├── ApiGatewayApplication.java
│   └── config/
│       ├── GatewayConfig.java          # Routes and filters
│       ├── SecurityConfig.java         # Security configuration
│       └── GlobalErrorHandler.java     # Error handling
└── src/main/resources/
    └── application.yml

services/auth-service/
├── src/main/java/com/vsms/auth/
│   ├── api/controller/                  # REST controllers
│   ├── application/service/             # Business logic
│   ├── domain/                          # Entities and repositories
│   ├── config/                          # Configuration classes
│   └── exception/                       # Exception handlers
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/                    # Flyway scripts
└── src/test/                            # Unit and integration tests
```

### Best Practices

1. **Security First**: Always validate inputs and use parameterized queries
2. **Error Handling**: Use global exception handlers for consistent error responses
3. **Logging**: Log important events and errors with appropriate levels
4. **Monitoring**: Expose metrics and health checks
5. **Testing**: Write comprehensive unit and integration tests
6. **Documentation**: Keep API documentation updated (Swagger)

### Contributing

1. Follow existing code style and patterns
2. Add tests for new features
3. Update documentation as needed
4. Ensure all tests pass before committing

---

This documentation should enable any developer to understand and work with the VSMS microservices system within 30 minutes. For detailed API specifications, refer to the Swagger documentation at `/swagger-ui.html` when services are running.
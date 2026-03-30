# VSMS Microservices - Run Guide

This guide provides step-by-step instructions to set up and run the VSMS (Vehicle Sales Management System) microservices locally.

## 📋 Prerequisites

Before running the system, ensure you have the following installed:

- **Java 21** or higher
- **Docker** and **Docker Compose**
- **Git** (for cloning the repository)
- **MySQL** (optional, if not using Docker)

## 🏗️ System Architecture

The VSMS microservices system consists of:

- **Eureka Server** (Port: 8761) - Service discovery
- **API Gateway** (Port: 8080) - Entry point with routing, security, and rate limiting
- **Auth Service** (Port: 8090) - Authentication and authorization
- **Customer Service** (Port: 8100) - Customer management
- **Master Service** (Port: 8110) - Master data management
- **Sales Service** (Port: 8120) - Sales order management
- **Purchase Service** (Port: 8130) - Purchase order management
- **Inventory Service** (Port: 8140) - Stock management
- **Fulfilment Service** (Port: 8150) - Invoice and delivery management
- **Cost Service** (Port: 8160) - Cost tracking
- **HR Service** (Port: 8170) - Human resources
- **DRS Service** (Port: 8180) - Daily reporting system

## 🚀 Quick Start with Docker Compose

The easiest way to run the entire system is using Docker Compose.

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd vsms-microservices
```

### Step 2: Start Infrastructure Services

```bash
# Start MySQL and other infrastructure
docker-compose up -d mysql
```

Wait for MySQL to be ready (check logs with `docker-compose logs mysql`).

### Step 3: Run All Services

```bash
# Start all microservices
docker-compose up -d
```

### Step 4: Verify Services

```bash
# Check service health
docker-compose ps

# View logs
docker-compose logs -f [service-name]
```

## 🔧 Manual Setup (Development)

If you prefer to run services individually for development:

### Step 1: Start Infrastructure

```bash
# Start MySQL
docker-compose up -d mysql

# Or use local MySQL
mysql -u root -p < infra/mysql/init/01_create_databases.sql
mysql -u root -p vsms_auth < infra/mysql/init/02_create_tables_auth.sql
# ... run other SQL files as needed
```

### Step 2: Start Eureka Server

```bash
cd eureka-server
./gradlew bootRun
```

Eureka will be available at: http://localhost:8761

### Step 3: Start API Gateway

```bash
cd api-gateway
./gradlew bootRun
```

API Gateway will be available at: http://localhost:8080

### Step 4: Start Auth Service

```bash
cd services/auth-service
./gradlew bootRun
```

Auth Service will be available at: http://localhost:8090

### Step 5: Start Other Services (Optional)

Repeat the process for other services as needed:

```bash
cd services/[service-name]
./gradlew bootRun
```

## 📊 Service URLs and Endpoints

### API Gateway (Main Entry Point)
- **Base URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus

### Auth Service Endpoints (via Gateway)
- **Login**: `POST /api/v1/auth/login`
- **Register**: `POST /api/v1/auth/register`
- **Users**: `GET /api/v1/auth/users`

### Eureka Server
- **Dashboard**: http://localhost:8761
- **Status**: http://localhost:8761/eureka/apps

## 🔐 Authentication

The system uses JWT-based authentication:

1. **Login** to get JWT token:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password"}'
   ```

2. **Use token** in subsequent requests:
   ```bash
   curl -H "Authorization: Bearer <your-jwt-token>" \
        http://localhost:8080/api/v1/auth/users
   ```

## 📈 Monitoring and Observability

### Health Checks
```bash
# Individual service health
curl http://localhost:8080/actuator/health

# Gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

### Metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Health Indicators**: http://localhost:8080/actuator/health

### Logging
All services use structured logging. View logs:

```bash
# Docker logs
docker-compose logs -f api-gateway

# Application logs (when running locally)
tail -f services/auth-service/logs/application.log
```

## 🧪 Testing

### Unit Tests
```bash
# Run tests for specific service
cd services/auth-service
./gradlew test

# Run all tests
./gradlew test --parallel
```

### Integration Tests
```bash
# Run with test profile
./gradlew bootRun --args='--spring.profiles.active=test'
```

### API Testing
```bash
# Test auth endpoint
curl -X GET http://localhost:8080/api/v1/auth/health

# Test with authentication
curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/api/v1/auth/users
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the root directory:

```env
# Database
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=vsms

# JWT
JWT_SECRET=your-jwt-secret-key
JWT_JWKS_URI=http://localhost:8090/api/v1/auth/jwks

# Eureka
EUREKA_URL=http://localhost:8761/eureka/

# Redis (for rate limiting)
REDIS_HOST=localhost
REDIS_PORT=6379
```

### Application Profiles

- **default**: Local development
- **test**: Testing environment
- **prod**: Production environment

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check MySQL container
   docker-compose logs mysql

   # Verify database exists
   docker exec -it vsms-mysql mysql -u root -p -e "SHOW DATABASES;"
   ```

2. **Service Discovery Issues**
   ```bash
   # Check Eureka dashboard
   open http://localhost:8761

   # Verify service registration
   curl http://localhost:8761/eureka/apps
   ```

3. **Port Conflicts**
   ```bash
   # Check what's using ports
   lsof -i :8080

   # Change ports in application.yml
   server:
     port: 8081
   ```

4. **Rate Limiting Issues**
   ```bash
   # Check Redis connection
   docker-compose logs redis

   # Disable rate limiting temporarily
   # Comment out rate limiter configuration in GatewayConfig.java
   ```

### Logs and Debugging

```bash
# Enable debug logging
logging:
  level:
    com.vsms: DEBUG

# View detailed logs
docker-compose logs --tail=100 api-gateway
```

## 📚 Additional Resources

- [System Architecture Documentation](./VSMS-Microservices-Documentation.md)
- [Local Development Isolation Guide](./VSMS-Local-Dev-Isolation-Guide.md)
- [API Documentation](./api-docs/)
- [Database Schema](./infra/mysql/init/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.</content>
</xai:function_call name="attempt_completion">
<parameter name="result">I've created a comprehensive "VSMS-Run-Guide.md" file that provides complete instructions for setting up and running the VSMS microservices system. The guide includes:

## Key Sections:

1. **Prerequisites** - Required software and tools
2. **System Architecture** - Overview of all services and ports
3. **Quick Start with Docker** - Easiest way to run everything
4. **Manual Setup** - For development environments
5. **Service URLs** - All endpoints and access points
6. **Authentication** - JWT token usage examples
7. **Monitoring** - Health checks, metrics, and logging
8. **Testing** - Unit tests, integration tests, API testing
9. **Configuration** - Environment variables and profiles
10. **Troubleshooting** - Common issues and solutions
11. **Additional Resources** - Links to other documentation

The guide provides both Docker Compose (recommended) and manual setup options, making it accessible for different development scenarios. It includes practical examples with curl commands, configuration examples, and troubleshooting tips.

This documentation will help developers quickly understand and run the entire VSMS microservices system! 🚀
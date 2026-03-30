# VSMS Application Management Scripts

This document describes the scripts used to manage the VSMS (Vehicle Sales Management System) microservices application.

## 🚀 Quick Start

### Start the Entire Application
```bash
./start-application.sh
```

### Stop the Entire Application
```bash
./stop-application.sh
```

## 📋 Scripts Overview

### `manage-service.sh` - Individual Service Management 🆕
**Purpose**: Manage individual microservices without affecting the entire application stack.

**Perfect for Development**:
- ✅ **Rapid Development**: Restart only the service you're working on
- ✅ **Bug Fixes**: Stop, fix, and restart individual services quickly
- ✅ **Testing**: Test service changes without full stack restart
- ✅ **Resource Efficient**: Keep other services running during development

**Commands**:
```bash
# Start individual service
./manage-service.sh start customer-service

# Stop individual service
./manage-service.sh stop customer-service

# Restart individual service
./manage-service.sh restart customer-service

# Check service status
./manage-service.sh status customer-service

# List all available services
./manage-service.sh list

# Show help
./manage-service.sh help
```

**Features**:
- ✅ **Automatic Building**: Rebuilds service if source code changed
- ✅ **Health Monitoring**: Waits for service to be fully ready
- ✅ **PID Management**: Tracks background processes safely
- ✅ **Log Integration**: Individual service logs in `logs/` directory
- ✅ **Infrastructure Check**: Verifies Docker containers are running

### `start-application.sh`
**Purpose**: Starts the complete VSMS microservices stack in the correct order.

**What it does:**
1. ✅ **Infrastructure**: Starts Docker containers (MySQL + Eureka)
2. ✅ **Shared Libraries**: Builds `vsms-common` and `vsms-events`
3. ✅ **Services**: Starts all microservices in dependency order
4. ✅ **Health Checks**: Verifies all services are running
5. ✅ **Status Report**: Shows URLs and ports for access

**Service Startup Order:**
```
1. auth-service (8090) - JWT token generation
2. api-gateway (8080) - API routing & security
3. customer-service (8082)
4. master-service (8091)
5. cost-service (8083)
6. sales-service (8085)
7. hr-service (8086)
8. inventory-service (8087)
9. purchase-service (8088)
10. fulfilment-service (8089)
11. drs-service (8084)
```

### `stop-application.sh`
**Purpose**: Gracefully stops the entire application stack.

**What it does:**
1. ✅ **Services**: Stops all microservices in reverse dependency order
2. ✅ **Infrastructure**: Stops Docker containers
3. ✅ **Cleanup**: Removes PID files and old logs
4. ✅ **Status Report**: Confirms everything is stopped

**Force Stop Option:**
```bash
./stop-application.sh --force
```
Forcefully kills all processes and containers (use as last resort).

## 🔍 Monitoring & Troubleshooting

### Check Application Status
```bash
# View running containers
docker-compose ps

# View service logs
tail -f logs/auth-service.log
tail -f logs/customer-service.log

# Check Eureka dashboard
open http://localhost:8761
```

### Common Issues

#### Port Conflicts
**Symptoms**: Services fail to start with "port already in use" errors
**Solution**:
```bash
# Find process using the port
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use force stop
./stop-application.sh --force
```

#### Docker Issues
**Symptoms**: "Docker is not running" error
**Solution**:
```bash
# Start Docker service (macOS)
# Linux/Windows: adjust accordingly
sudo systemctl start docker  # Linux
# Windows/macOS: Start Docker Desktop
```

#### Build Failures
**Symptoms**: Gradle build fails
**Solution**:
```bash
# Clean and rebuild shared libraries
cd shared/vsms-common
./gradlew clean build

# Clean individual service
cd ../../services/customer-service
./gradlew clean
```

## 🏗️ Architecture

### Infrastructure Layer
```
Docker Compose
├── MySQL (3306) - Database
└── Eureka (8761) - Service Discovery
```

### Application Layer
```
Spring Boot Microservices
├── Shared Libraries (vsms-common, vsms-events)
├── API Gateway (8080) - Routing & Security
├── Auth Service (8090) - JWT Generation
└── Business Services (8082-8089) - Domain Logic
```

### Security Layer
```
Shared Security Configuration
├── JWT Authentication (OAuth2 Resource Server)
├── CORS Support (Configurable Origins)
├── HSTS Headers (Conditional)
├── Custom Error Responses
└── Stateless Session Management
```

## 📊 Service Ports

| Service | Port | Purpose |
|---------|------|---------|
| **API Gateway** | 8080 | Main entry point, routing |
| **Eureka Server** | 8761 | Service discovery dashboard |
| **Auth Service** | 8090 | JWT token generation |
| **Customer Service** | 8082 | Customer management |
| **Master Service** | 8091 | Reference data (items, categories) |
| **Cost Service** | 8083 | Cost estimation & approval |
| **Sales Service** | 8085 | Sales orders & transactions |
| **HR Service** | 8086 | Employee & payroll management |
| **Inventory Service** | 8087 | Stock management |
| **Purchase Service** | 8088 | Purchase orders & procurement |
| **Fulfilment Service** | 8089 | Invoices & delivery |
| **DRS Service** | 8084 | Delivery route management |

## 🔧 Configuration

### Environment Variables
```bash
# Database
MYSQL_ROOT_PASSWORD=root

# Service Discovery
EUREKA_URL=http://eureka-server:8761/eureka/

# JWT
JWT_JWKS_URI=http://auth-service:8090/api/v1/auth/jwks
```

### Security Configuration
```yaml
# In each service's application.yml
app:
  security:
    cors:
      allowed-origins: "http://localhost:8080,http://localhost:3000"
    headers:
      hsts:
        enabled: false  # Set to true in production
```

## 🚀 Development Workflow

### Full Application Development
1. **Start**: `./start-application.sh`
2. **Develop**: Make code changes across services
3. **Test**: Use API endpoints or run integration tests
4. **Stop**: `./stop-application.sh`

### Individual Service Development (Recommended) ⚡
1. **Start Infrastructure**: `./start-application.sh` (for MySQL/Eureka)
2. **Start Target Service**: `./manage-service.sh start customer-service`
3. **Develop**: Make changes to customer-service
4. **Restart Service**: `./manage-service.sh restart customer-service`
5. **Test**: Test your changes quickly
6. **Repeat**: Continue development without affecting other services

### Bug Fix Workflow
```bash
# Quick bug fix cycle
./manage-service.sh stop api-gateway
# Make your fix...
./manage-service.sh start api-gateway
# Test the fix immediately
```

### Multi-Service Development
```bash
# Work on auth + customer services together
./start-application.sh                    # Start infrastructure + all services
./manage-service.sh restart auth-service  # When auth changes
./manage-service.sh restart customer-service  # When customer changes
```

## 📝 Best Practices

### Choose the Right Approach
- **🔄 Full Stack**: Use `start-application.sh` for integration testing
- **⚡ Individual Services**: Use `manage-service.sh` for focused development
- **🐛 Bug Fixes**: Individual service management for rapid iteration
- **🔧 Infrastructure**: Always ensure Docker containers are running

### Performance Tips
- Keep infrastructure running between sessions
- Use individual service restarts for faster development cycles
- Monitor logs: `tail -f logs/service-name.log`
- Check service health: `./manage-service.sh status`

### Production Deployment
- Use production Docker images
- Configure environment variables properly
- Enable HSTS headers
- Set up proper logging and monitoring
- Use container orchestration (Kubernetes/Docker Swarm)

### Troubleshooting Tips
- Always check logs in the `logs/` directory
- Use `docker-compose logs` for infrastructure issues
- Verify port availability before starting
- Clean builds when encountering compilation issues

## 🆘 Support

If you encounter issues:

1. Check the logs in the `logs/` directory
2. Verify Docker is running
3. Ensure ports 8080-8091 are available
4. Try force stopping: `./stop-application.sh --force`
5. Restart Docker containers if needed

---

**🎉 Happy coding with VSMS Microservices!**
#!/bin/bash

# VSMS Microservices Application Startup Script
# This script starts the entire microservices application stack

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INFRA_WAIT_TIME=30
SERVICE_WAIT_TIME=10
MAX_RETRIES=3

# Logging functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if a port is available
check_port() {
    local port=$1
    local service=$2
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        log_success "$service is running on port $port"
        return 0
    else
        log_warning "$service is not yet available on port $port"
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local port=$1
    local service=$2
    local max_attempts=$3
    local attempt=1

    log_info "Waiting for $service to be ready on port $port..."

    while [ $attempt -le $max_attempts ]; do
        if check_port $port "$service"; then
            return 0
        fi

        log_info "Attempt $attempt/$max_attempts: $service not ready yet, waiting..."
        sleep $SERVICE_WAIT_TIME
        ((attempt++))
    done

    log_error "$service failed to start after $max_attempts attempts"
    return 1
}

# Function to check if a port is available
check_port_available() {
    local port=$1
    local service_name=$2

    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 1  # Port is in use
    else
        return 0  # Port is available
    fi
}

# Function to get process using a port
get_process_using_port() {
    local port=$1
    lsof -Pi :$port -sTCP:LISTEN -t 2>/dev/null
}

# Function to kill process using a port
kill_process_on_port() {
    local port=$1
    local pid=$(get_process_using_port $port)

    if [ -n "$pid" ]; then
        log_warning "Killing process $pid using port $port"
        kill -9 $pid 2>/dev/null
        sleep 2
        return 0
    fi
    return 1
}

# Function to clean up previous application state
cleanup_previous_run() {
    log_info "Cleaning up previous application state..."

    # Note: Docker containers are now properly stopped by stop-application.sh
    # Only clean up any leftover PID files and kill associated processes that might still be running

    if [ -d "logs" ]; then
        for pid_file in logs/*.pid; do
            if [ -f "$pid_file" ]; then
                local pid=$(cat "$pid_file" 2>/dev/null)
                if [ -n "$pid" ] && kill -0 $pid 2>/dev/null; then
                    local service_name=$(basename "$pid_file" .pid)
                    log_info "Stopping previous $service_name process (PID: $pid)"
                    kill -9 $pid 2>/dev/null
                fi
                rm -f "$pid_file"
            fi
        done
    fi

    log_info "Cleanup completed"
}

# Function to check and resolve port conflicts
resolve_port_conflicts() {
    log_info "Checking for port conflicts..."

    # Infrastructure ports (Docker containers - MySQL may or may not be included)
    local infra_ports=("8761:Eureka Server" "5672:RabbitMQ" "15672:RabbitMQ Management")
    # Note: MySQL port 3306 will be checked separately based on which MySQL we're using

    # Application ports (Java services that run as background processes)
    local app_ports=("8080:API Gateway" "8090:Auth Service" "8082:Customer Service" "8091:Master Service" "8083:Cost Service" "8085:Sales Service" "8086:HR Service" "8087:Inventory Service" "8088:Purchase Service" "8089:Fulfilment Service" "8084:DRS Service")

    local all_ports=("${infra_ports[@]}" "${app_ports[@]}")
    local conflicts_found=0

    for port_info in "${all_ports[@]}"; do
        IFS=':' read -r port service_name <<< "$port_info"

        if ! check_port_available $port; then
            log_warning "Port $port ($service_name) is already in use"
            conflicts_found=1

            # First try to stop any existing VSMS Docker containers
            if docker-compose ps -q 2>/dev/null | xargs docker inspect --format '{{.Name}}' 2>/dev/null | grep -q "vsms-microservices"; then
                log_info "Stopping existing VSMS Docker containers..."
                docker-compose down 2>/dev/null
                sleep 3
            fi

            # Check if it's a Java service process (from previous runs)
            local pid_file="logs/${service_name// /-}.pid"
            if [ -f "$pid_file" ]; then
                local old_pid=$(cat "$pid_file" 2>/dev/null)
                if [ -n "$old_pid" ] && kill -0 $old_pid 2>/dev/null; then
                    log_info "Stopping previous $service_name process (PID: $old_pid)"
                    kill -9 $old_pid 2>/dev/null
                    rm -f "$pid_file"
                    sleep 2
                fi
            fi

            # Check again after cleanup
            if ! check_port_available $port; then
                # Special handling for common system services
                if [ "$port" = "3306" ]; then
                    log_warning "Port 3306 is used by system MySQL. Attempting to stop it..."
                    # Try common MySQL stop commands
                    if command -v brew >/dev/null 2>&1; then
                        brew services stop mysql 2>/dev/null || true
                    fi
                    sudo launchctl unload /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist 2>/dev/null || true
                    sudo systemctl stop mysql 2>/dev/null || true
                    sudo systemctl stop mysqld 2>/dev/null || true
                    sleep 3
                fi

                # Check again after system service handling
                if ! check_port_available $port; then
                    # Try to kill any remaining process using the port
                    if kill_process_on_port $port; then
                        log_info "Successfully killed process using port $port"
                    else
                        log_error "Port $port is still in use by an external service."
                        log_info "🔧 Troubleshooting steps:"
                        log_info "  1. Find the process: lsof -ti:$port"
                        log_info "  2. Check service details: lsof -i :$port"
                        if [ "$port" = "3306" ]; then
                            log_info "  3. Stop MySQL service:"
                            log_info "    • macOS/Homebrew: brew services stop mysql"
                            log_info "    • Linux: sudo systemctl stop mysql"
                            log_info "    • Or: sudo launchctl unload /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist"
                        fi
                        log_info "  4. Alternative: Modify docker-compose.yml to use different ports"
                        log_info "  5. Or use individual service management: ./manage-service.sh"
                        log_info "  6. For development, consider using alternative ports automatically"
                        return 1
                    fi
                fi
            fi
        fi
    done

    if [ $conflicts_found -eq 1 ]; then
        log_info "Port conflicts resolved"
    else
        log_info "No port conflicts detected"
    fi

    return 0
}

# Function to wait for MySQL to be ready
wait_for_mysql() {
    log_info "Waiting for MySQL to be ready..."

    local max_attempts=12  # 2 minutes with 10 second intervals
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        log_info "Checking MySQL connectivity (attempt $attempt/$max_attempts)..."

        # Try to connect to MySQL and check if databases exist
        if docker-compose exec -T mysql mysql -u root -p${MYSQL_PASSWORD:-root} -e "SELECT 1;" >/dev/null 2>&1; then
            # Check if vsms_auth database exists
            if docker-compose exec -T mysql mysql -u root -p${MYSQL_PASSWORD:-root} -e "USE vsms_auth; SELECT 1;" >/dev/null 2>&1; then
                log_success "MySQL is ready and databases are initialized"
                return 0
            else
                log_info "MySQL is up but databases not yet initialized, waiting..."
            fi
        else
            log_info "MySQL not yet ready, waiting..."
        fi

        sleep 10
        ((attempt++))
    done

    log_error "MySQL failed to become ready after $max_attempts attempts"
    log_info "MySQL logs:"
    docker-compose logs mysql
    return 1
}

# Function to start Docker infrastructure
start_infrastructure() {
    log_info "Starting Docker infrastructure (Eureka Server only - using local MySQL)..."

    cd "$PROJECT_ROOT"

    # Check if Docker is running
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi

    # Check MySQL availability (prefer local, fallback to Docker)
    log_info "Checking MySQL connectivity..."

    local mysql_available=false
    local using_local_mysql=false

    # First, try to start local MySQL if it's installed
    if command -v mysql >/dev/null 2>&1; then
        log_info "Attempting to start local MySQL..."

        # Try to start MySQL service
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS with Homebrew
            if command -v brew >/dev/null 2>&1; then
                brew services start mysql 2>/dev/null || true
                sleep 3
            fi
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux with systemd
            sudo systemctl start mysql 2>/dev/null || sudo systemctl start mysqld 2>/dev/null || true
            sleep 3
        fi

        # Check if local MySQL is now accessible
        if mysql -u root -p${MYSQL_PASSWORD:-root} -e "SELECT 1;" >/dev/null 2>&1; then
            log_success "Local MySQL started and accessible"
            mysql_available=true
            using_local_mysql=true
        fi
    fi

    # If local MySQL is not available, fall back to Docker MySQL
    if ! $mysql_available; then
        log_warning "Local MySQL not available, starting Docker MySQL container..."

        # Start Docker MySQL container
        if ! docker-compose up -d mysql; then
            log_error "Failed to start Docker MySQL container"
            docker-compose logs mysql
            exit 1
        fi

        # Wait for Docker MySQL to be ready
        if ! wait_for_mysql; then
            log_error "Docker MySQL failed to start. Please check Docker and MySQL configuration."
            exit 1
        fi

        log_success "Docker MySQL container is ready"
        mysql_available=true
    fi

    if ! $mysql_available; then
        log_error "No MySQL instance available. Please ensure MySQL is installed and can be started."
        exit 1
    fi

    # Initialize databases if they don't exist
    log_info "Initializing databases..."
    if $using_local_mysql; then
        mysql -u root -p${MYSQL_PASSWORD:-root} < infra/mysql/init/01_create_databases.sql 2>/dev/null || log_warning "Database initialization may have failed or already initialized"
    else
        docker-compose exec -T mysql mysql -u root -p${MYSQL_PASSWORD:-root} < infra/mysql/init/01_create_databases.sql 2>/dev/null || log_warning "Database initialization may have failed or already initialized"
    fi
    log_success "Databases initialized"

    # Resolve port conflicts (excluding MySQL port 3306 since we're using local)
    if ! resolve_port_conflicts; then
        log_error "Failed to resolve port conflicts. Please free up the required ports and try again."
        exit 1
    fi

    # Start only Eureka server (not MySQL since we're using local)
    log_info "Starting Eureka Server Docker container..."
    if ! docker-compose up -d eureka-server; then
        log_error "Failed to start Eureka Server container"
        docker-compose logs eureka-server
        exit 1
    fi

    # Wait for Eureka container to be up
    log_info "Waiting for Eureka Server container to be ready..."
    sleep 10

    # Verify Eureka container is running
    if ! docker-compose ps eureka-server | grep -q "Up"; then
        log_error "Eureka Server container failed to start"
        docker-compose logs eureka-server
        exit 1
    fi

    log_success "Infrastructure services started successfully (using local MySQL + Docker Eureka)"
}

# Function to build shared libraries
build_shared_libs() {
    log_info "Building shared libraries..."

    cd "$PROJECT_ROOT"

    # Build vsms-common
    if [ -d "shared/vsms-common" ]; then
        log_info "Building vsms-common..."
        cd shared/vsms-common
        ./gradlew clean build -x test --quiet
        cd "$PROJECT_ROOT"
        log_success "vsms-common built successfully"
    fi

    # Build vsms-events if it exists
    if [ -d "shared/vsms-events" ]; then
        log_info "Building vsms-events..."
        cd shared/vsms-events
        ./gradlew clean build -x test --quiet
        cd "$PROJECT_ROOT"
        log_success "vsms-events built successfully"
    fi
}

# Function to build Docker services (JARs needed for containers)
build_docker_services() {
    log_info "Building Docker services (JARs for containers)..."

    cd "$PROJECT_ROOT"

    # Build eureka-server
    if [ -d "eureka-server" ]; then
        log_info "Building eureka-server..."
        cd eureka-server
        ./gradlew clean build -x test --quiet
        cd "$PROJECT_ROOT"
        log_success "eureka-server JAR built successfully"
    fi

    # Build api-gateway
    if [ -d "api-gateway" ]; then
        log_info "Building api-gateway..."
        cd api-gateway
        ./gradlew clean build -x test --quiet
        cd "$PROJECT_ROOT"
        log_success "api-gateway JAR built successfully"
    fi

    # Add other Docker-based services here if needed
    # For now, only eureka-server and api-gateway use Docker containers
}

# Function to diagnose service failure and provide recommendations
diagnose_service_failure() {
    local service_name=$1
    local port=$2
    local service_dir=$3
    local log_file="$PROJECT_ROOT/logs/${service_name}.log"
    local pid_file="$PROJECT_ROOT/logs/${service_name}.pid"

    log_info "🔍 Diagnosing $service_name failure..."

    # Check 1: Process status
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            log_info "✅ Service process is running (PID: $pid)"
        else
            log_error "❌ Service process is not running (PID: $pid was recorded but process died)"
        fi
    else
        log_error "❌ No PID file found - service may not have started"
    fi

    # Check 2: Port availability
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        log_info "✅ Port $port is in use (service may be starting slowly)"
    else
        log_error "❌ Port $port is not in use"
    fi

    # Check 3: Log analysis
    if [ -f "$log_file" ]; then
        log_info "📋 Analyzing service logs..."

        # Database connection issues
        if grep -q "Communications link failure\|Connection refused\|Unknown database" "$log_file"; then
            log_error "❌ DATABASE ISSUE: Service cannot connect to MySQL database"
            log_info "💡 SOLUTION: Ensure MySQL is running and database '${SPRING_DATASOURCE_URL}' exists"
            if [[ "$OSTYPE" == "darwin"* ]]; then
                log_info "   • Check MySQL: brew services list | grep mysql"
                log_info "   • Start MySQL: brew services start mysql"
            elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
                log_info "   • Check MySQL: sudo systemctl status mysql"
                log_info "   • Start MySQL: sudo systemctl start mysql"
            fi
        fi

        # Eureka connection issues
        if grep -q "Connection refused.*8761\|Eureka server connection refused" "$log_file"; then
            log_error "❌ EUREKA ISSUE: Service cannot connect to Eureka server"
            log_info "💡 SOLUTION: Ensure Eureka server is running on port 8761"
            log_info "   • Check Eureka: docker-compose ps eureka-server"
            log_info "   • Restart Eureka: docker-compose restart eureka-server"
        fi

        # Port already in use
        if grep -q "Port.*already in use\|Address already in use" "$log_file"; then
            log_error "❌ PORT CONFLICT: Port $port is already in use by another process"
            log_info "💡 SOLUTION: Find and kill the process using port $port"
            log_info "   • Find process: lsof -ti:$port"
            log_info "   • Kill process: kill -9 \$(lsof -ti:$port)"
        fi

        # Configuration issues
        if grep -q "ConfigurationProperties.*not found\|Property.*not found" "$log_file"; then
            log_error "❌ CONFIGURATION ISSUE: Missing required configuration properties"
            log_info "💡 SOLUTION: Check application.yml for missing properties"
        fi

        # Dependency issues
        if grep -q "ClassNotFoundException\|NoClassDefFoundError" "$log_file"; then
            log_error "❌ DEPENDENCY ISSUE: Missing JAR dependencies"
            log_info "💡 SOLUTION: Clean and rebuild the service"
            log_info "   • Clean: cd $service_dir && ./gradlew clean"
            log_info "   • Rebuild: cd $service_dir && ./gradlew build"
        fi

        # Show recent log entries
        log_info "📄 Recent log entries from $log_file:"
        tail -20 "$log_file" | while IFS= read -r line; do
            echo "   $line"
        done

    else
        log_error "❌ No log file found at $log_file"
    fi

    # Check 4: Database connectivity
    local db_name="vsms_${service_name//-/_}"
    if [ "$service_name" = "auth-service" ]; then
        db_name="vsms_auth"
    fi

    if ! mysql -u root -p${MYSQL_PASSWORD:-root} -e "USE $db_name; SELECT 1;" >/dev/null 2>&1; then
        log_error "❌ DATABASE ACCESS: Cannot access database '$db_name'"
        log_info "💡 SOLUTION: Database may not exist or credentials are incorrect"
        log_info "   • Create database: mysql -u root -p -e \"CREATE DATABASE $db_name;\""
    else
        log_info "✅ Database '$db_name' is accessible"
    fi

    # Check 5: Docker containers
    log_info "🐳 Docker container status:"
    if docker-compose ps | grep -q "eureka-server"; then
        log_info "   ✅ Eureka server container is running"
    else
        log_error "   ❌ Eureka server container is not running"
        log_info "   💡 SOLUTION: docker-compose up -d eureka-server"
    fi

    log_info "🔧 Manual troubleshooting steps:"
    log_info "   1. Check full logs: tail -f $log_file"
    log_info "   2. Restart service manually: cd $service_dir && ./gradlew bootRun"
    log_info "   3. Check system resources: df -h && free -h"
    log_info "   4. Verify all prerequisites are running"
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3

    log_info "Starting $service_name..."

    # Check if service directory exists
    if [ ! -d "$service_dir" ]; then
        log_warning "Service directory $service_dir not found, skipping $service_name"
        return 0
    fi

    cd "$service_dir"

    # Set environment variables for local development (connecting to Docker containers)
    export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/vsms_${service_name//-/_}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    export SPRING_DATASOURCE_USERNAME="${MYSQL_USER:-root}"
    export SPRING_DATASOURCE_PASSWORD="${MYSQL_PASSWORD:-root}"
    export EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE="${EUREKA_URL:-http://localhost:8761/eureka/}"
    export SPRING_RABBITMQ_HOST="${RABBITMQ_HOST:-localhost}"
    export SPRING_RABBITMQ_USERNAME="${RABBITMQ_USER:-guest}"
    export SPRING_RABBITMQ_PASSWORD="${RABBITMQ_PASSWORD:-guest}"

    # Special handling for auth-service database name
    if [ "$service_name" = "auth-service" ]; then
        export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/vsms_auth?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    fi

    log_info "Using database: ${SPRING_DATASOURCE_URL}"

    # Start service in background with environment variables
    nohup env SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
             SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
             SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
             EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE="$EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE" \
             SPRING_RABBITMQ_HOST="$SPRING_RABBITMQ_HOST" \
             SPRING_RABBITMQ_USERNAME="$SPRING_RABBITMQ_USERNAME" \
             SPRING_RABBITMQ_PASSWORD="$SPRING_RABBITMQ_PASSWORD" \
             ./gradlew bootRun --quiet > "$PROJECT_ROOT/logs/${service_name}.log" 2>&1 &
    local pid=$!

    # Store PID for potential cleanup
    echo $pid > "$PROJECT_ROOT/logs/${service_name}.pid"

    cd "$PROJECT_ROOT"

    # Wait for service to be ready
    if wait_for_service $port "$service_name" $MAX_RETRIES; then
        log_success "$service_name started successfully on port $port"
    else
        log_error "Failed to start $service_name after $MAX_RETRIES attempts"
        diagnose_service_failure "$service_name" "$port" "$service_dir"
        return 1
    fi
}

# Function to start all services
start_services() {
    local fast_mode=${1:-false}
    log_info "Starting microservices..."

    # Create logs directory
    mkdir -p logs

    if $fast_mode; then
        log_info "Fast mode: Starting only essential services (auth-service, api-gateway)"
        # Start only essential services in fast mode
        local services=(
            "auth-service:services/auth-service:8090"
            "api-gateway:api-gateway:8080"
        )
    else
        # Start all services in normal mode
        local services=(
            "auth-service:services/auth-service:8090"
            "api-gateway:api-gateway:8080"
            "customer-service:services/customer-service:8082"
            "master-service:services/master-service:8091"
            "cost-service:services/cost-service:8083"
            "sales-service:services/sales-service:8085"
            "hr-service:services/hr-service:8086"
            "inventory-service:services/inventory-service:8087"
            "purchase-service:services/purchase-service:8088"
            "fulfilment-service:services/fulfilment-service:8089"
            "drs-service:services/drs-service:8084"
        )
    fi

    local failed_services=()

    for service_info in "${services[@]}"; do
        IFS=':' read -r service_name service_dir port <<< "$service_info"

        if ! start_service "$service_name" "$service_dir" "$port"; then
            failed_services+=("$service_name")
        fi
    done

    # Report results
    if [ ${#failed_services[@]} -eq 0 ]; then
        log_success "All services started successfully!"
    else
        log_error "Some services failed to start: ${failed_services[*]}"
        log_info "🔍 Detailed diagnostics have been provided above for each failed service"
        log_info "📋 Summary of failed services and potential solutions:"
        for service in "${failed_services[@]}"; do
            log_info "   • $service: Check the diagnostic output above for specific issues"
        done
        return 1
    fi
}

# Function to check overall application health
check_application_health() {
    log_info "Checking application health..."

    local services=(
        "Eureka Server:8761"
        "Auth Service:8090"
        "API Gateway:8080"
        "Customer Service:8082"
        "Master Service:8091"
        "Cost Service:8083"
        "Sales Service:8085"
        "HR Service:8086"
        "Inventory Service:8087"
        "Purchase Service:8088"
        "Fulfilment Service:8089"
        "DRS Service:8084"
    )

    local healthy_services=0
    local total_services=${#services[@]}

    for service_info in "${services[@]}"; do
        IFS=':' read -r service_name port <<< "$service_info"
        if check_port $port "$service_name"; then
            ((healthy_services++))
        fi
    done

    log_info "Health Check Results: $healthy_services/$total_services services healthy"

    if [ $healthy_services -eq $total_services ]; then
        log_success "🎉 All services are running and healthy!"
        log_info ""
        log_info "Application URLs:"
        log_info "  - Eureka Dashboard: http://localhost:8761"
        log_info "  - API Gateway: http://localhost:8080"
        log_info "  - Auth Service: http://localhost:8090"
        log_info ""
        log_info "To stop the application, run: ./stop-application.sh"
    else
        log_warning "Some services may not be fully ready yet"
    fi
}

# Function to cleanup on failure
cleanup() {
    log_warning "Cleaning up due to error..."

    # Stop any background processes
    if [ -d "logs" ]; then
        for pid_file in logs/*.pid; do
            if [ -f "$pid_file" ]; then
                local pid=$(cat "$pid_file")
                if kill -0 $pid 2>/dev/null; then
                    log_info "Stopping process $pid"
                    kill $pid
                fi
                rm -f "$pid_file"
            fi
        done
    fi

    # Stop Docker containers
    log_info "Stopping Docker containers..."
    docker-compose down
}

# Function to check prerequisites early
check_prerequisites() {
    log_info "Checking prerequisites..."

    # Check if Docker is running (fail fast)
    log_info "Checking Docker connectivity..."

    # Try multiple ways to check Docker status
    local docker_check_passed=false

    # Method 1: docker info
    if docker info >/dev/null 2>&1; then
        log_info "✅ Docker info check passed"
        docker_check_passed=true
    else
        log_warning "⚠️  Docker info check failed, trying alternative methods..."
    fi

    # Method 2: Check if docker daemon is responding
    if ! $docker_check_passed && docker ps >/dev/null 2>&1; then
        log_info "✅ Docker ps check passed"
        docker_check_passed=true
    fi

    # Method 3: Check Docker socket exists and is accessible
    if ! $docker_check_passed && [ -S "/var/run/docker.sock" ] && docker version >/dev/null 2>&1; then
        log_info "✅ Docker socket check passed"
        docker_check_passed=true
    fi

    # If all checks failed, provide comprehensive troubleshooting
    if ! $docker_check_passed; then
        log_error "🐳 Docker is not accessible!"
        log_info ""
        log_info "🔍 Troubleshooting steps:"
        log_info ""

        # Check if Docker command exists
        if ! command -v docker >/dev/null 2>&1; then
            log_info "❌ Docker command not found in PATH"
            if [[ "$OSTYPE" == "darwin"* ]]; then
                log_info "  → Install Docker Desktop for Mac from https://docker.com"
            fi
        else
            log_info "✅ Docker command found"
        fi

        # Check Docker Desktop status (macOS)
        if [[ "$OSTYPE" == "darwin"* ]]; then
            log_info ""
            log_info "🍎 macOS Docker Desktop checks:"
            log_info "  • Open Docker Desktop application"
            log_info "  • Check if Docker Desktop is running in menu bar"
            log_info "  • Try: open -a Docker"
            log_info "  • Wait 30 seconds after starting Docker Desktop"
        fi

        # General Docker daemon checks
        log_info ""
        log_info "🐳 General Docker checks:"
        log_info "  • Run: docker version"
        log_info "  • Run: docker info"
        log_info "  • Check Docker daemon status"

        # Permission checks
        log_info ""
        log_info "👤 Permission checks:"
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            log_info "  • Run: sudo usermod -aG docker $USER (then logout/login)"
            log_info "  • Or run script with: sudo $0"
        fi

        log_info ""
        log_info "🔄 After fixing Docker, run this script again."
        log_info "💡 If Docker Desktop is running, try waiting 30 seconds and running again."

        return 1
    fi

    log_success "Prerequisites check passed - Docker is ready!"
    return 0
}

# Main execution
main() {
    log_info "🚀 Starting VSMS Microservices Application..."
    log_info "Project Root: $PROJECT_ROOT"

    # Set up cleanup trap
    trap cleanup ERR

    # Check prerequisites first (fail fast)
    if ! check_prerequisites; then
        exit 1
    fi

    # Parse arguments
    local fast_mode=true
    while [[ $# -gt 0 ]]; do
        case $1 in
            --all|--full)
                fast_mode=false
                shift
                ;;
            *)
                log_error "Unknown argument: $1"
                log_info "Usage: $0 [--all|--full]"
                log_info "  --all, --full: Start all services (default: essential services only)"
                exit 1
                ;;
        esac
    done

    # Clean up any previous application state
    cleanup_previous_run

    # Build shared libraries first
    build_shared_libs

    # Build Docker services (JARs needed for containers)
    build_docker_services

    # Start infrastructure (now JARs are ready)
    start_infrastructure

    # Start services
    if $fast_mode; then
        log_info "Starting in fast mode (essential services only)..."
    else
        log_info "Starting in full mode (all services)..."
    fi
    start_services $fast_mode

    # Check application health
    check_application_health

    log_success "VSMS Microservices Application startup complete!"
}

# Run main function
main "$@"
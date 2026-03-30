#!/bin/bash

# VSMS Individual Service Management Script
# Manage individual microservices without affecting the entire stack

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Service definitions with their ports and directories
declare -A SERVICES=(
    ["auth-service"]="services/auth-service:8090"
    ["api-gateway"]="api-gateway:8080"
    ["customer-service"]="services/customer-service:8082"
    ["master-service"]="services/master-service:8091"
    ["cost-service"]="services/cost-service:8083"
    ["sales-service"]="services/sales-service:8085"
    ["hr-service"]="services/hr-service:8086"
    ["inventory-service"]="services/inventory-service:8087"
    ["purchase-service"]="services/purchase-service:8088"
    ["fulfilment-service"]="services/fulfilment-service:8089"
    ["drs-service"]="services/drs-service:8084"
)

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

log_command() {
    echo -e "${CYAN}[COMMAND]${NC} $1"
}

# Function to check if a port is available
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local port=$1
    local service=$2
    local max_attempts=30  # 30 seconds timeout
    local attempt=1

    log_info "Waiting for $service to be ready on port $port..."

    while [ $attempt -le $max_attempts ]; do
        if check_port $port; then
            log_success "$service is ready on port $port"
            return 0
        fi

        if [ $((attempt % 10)) -eq 0 ]; then
            log_info "Still waiting for $service... ($attempt/$max_attempts)"
        fi

        sleep 1
        ((attempt++))
    done

    log_error "$service failed to start after $max_attempts seconds"
    return 1
}

# Function to check if infrastructure is running
check_infrastructure() {
    log_info "Checking infrastructure status..."

    # Check MySQL
    if docker-compose ps mysql | grep -q "Up"; then
        log_success "MySQL is running"
    else
        log_warning "MySQL is not running - some services may fail to connect"
    fi

    # Check Eureka
    if docker-compose ps eureka-server | grep -q "Up"; then
        log_success "Eureka Server is running"
    else
        log_warning "Eureka Server is not running - service discovery may fail"
    fi
}

# Function to build service if needed
build_service() {
    local service_name=$1
    local service_dir=$2

    if [ ! -d "$service_dir" ]; then
        log_error "Service directory $service_dir not found"
        return 1
    fi

    cd "$service_dir"

    # Check if build is needed (no jar file or source changes)
    if [ ! -f "build/libs/${service_name}-0.0.1-SNAPSHOT.jar" ] || [ "src" -nt "build/libs/${service_name}-0.0.1-SNAPSHOT.jar" ]; then
        log_info "Building $service_name..."
        ./gradlew clean build -x test --quiet
        log_success "$service_name built successfully"
    else
        log_info "$service_name is already built"
    fi

    cd "$PROJECT_ROOT"
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_info=${SERVICES[$service_name]}

    if [ -z "$service_info" ]; then
        log_error "Unknown service: $service_name"
        list_services
        return 1
    fi

    IFS=':' read -r service_dir port <<< "$service_info"

    log_info "Starting $service_name..."

    # Check if already running
    if check_port $port; then
        log_warning "$service_name is already running on port $port"
        return 0
    fi

    # Check infrastructure
    check_infrastructure

    # Build service if needed
    build_service "$service_name" "$service_dir"

    # Start service in background
    cd "$service_dir"
    log_command "Starting $service_name in background..."
    nohup ./gradlew bootRun --quiet > "$PROJECT_ROOT/logs/${service_name}.log" 2>&1 &
    local pid=$!

    # Store PID for management
    echo $pid > "$PROJECT_ROOT/logs/${service_name}.pid"

    cd "$PROJECT_ROOT"

    log_info "$service_name started with PID $pid"

    # Wait for service to be ready
    if wait_for_service $port "$service_name"; then
        log_success "$service_name is now running and ready!"
        log_info "  📊 Logs: tail -f logs/${service_name}.log"
        log_info "  🌐 URL: http://localhost:$port"
        if [ "$service_name" = "api-gateway" ]; then
            log_info "  🔍 Eureka Dashboard: http://localhost:8761"
        fi
    else
        log_error "Failed to start $service_name"
        return 1
    fi
}

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="$PROJECT_ROOT/logs/${service_name}.pid"

    log_info "Stopping $service_name..."

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 $pid 2>/dev/null; then
            log_info "Terminating $service_name (PID: $pid)..."
            kill $pid

            # Wait for process to stop
            local count=0
            while kill -0 $pid 2>/dev/null && [ $count -lt 10 ]; do
                sleep 1
                ((count++))
            done

            if kill -0 $pid 2>/dev/null; then
                log_warning "Force terminating $service_name..."
                kill -9 $pid 2>/dev/null || true
            fi

            log_success "$service_name stopped"
        else
            log_info "$service_name is not running"
        fi
        rm -f "$pid_file"
    else
        log_info "No PID file found for $service_name (may not be managed by this script)"
    fi
}

# Function to restart a service
restart_service() {
    local service_name=$1

    log_info "Restarting $service_name..."
    stop_service "$service_name"
    sleep 2  # Brief pause
    start_service "$service_name"
}

# Function to check service status
check_service_status() {
    local service_name=$1
    local service_info=${SERVICES[$service_name]}

    if [ -z "$service_info" ]; then
        log_error "Unknown service: $service_name"
        return 1
    fi

    IFS=':' read -r service_dir port <<< "$service_info"

    echo -e "${PURPLE}$service_name${NC}"
    echo "  Directory: $service_dir"
    echo "  Port: $port"

    if check_port $port; then
        echo -e "  Status: ${GREEN}RUNNING${NC}"
        echo "  URL: http://localhost:$port"

        # Check PID file
        local pid_file="$PROJECT_ROOT/logs/${service_name}.pid"
        if [ -f "$pid_file" ]; then
            local pid=$(cat "$pid_file")
            if kill -0 $pid 2>/dev/null; then
                echo "  Process ID: $pid"
            else
                echo -e "  Process ID: ${YELLOW}$pid (stale)${NC}"
            fi
        fi

        # Show recent log entries
        local log_file="$PROJECT_ROOT/logs/${service_name}.log"
        if [ -f "$log_file" ]; then
            echo "  Recent Logs:"
            tail -3 "$log_file" | sed 's/^/    /'
        fi
    else
        echo -e "  Status: ${RED}STOPPED${NC}"
    fi
    echo
}

# Function to list all services
list_services() {
    echo -e "${PURPLE}Available Services:${NC}"
    echo

    for service_name in "${!SERVICES[@]}"; do
        IFS=':' read -r service_dir port <<< "${SERVICES[$service_name]}"
        printf "  %-20s %s:%s\n" "$service_name" "$service_dir" "$port"
    done
    echo
    log_info "Use: ./manage-service.sh status <service-name> to check individual service status"
}

# Function to show usage
show_usage() {
    echo -e "${PURPLE}VSMS Service Management Script${NC}"
    echo
    echo "USAGE:"
    echo "  ./manage-service.sh <command> [service-name]"
    echo
    echo "COMMANDS:"
    echo "  start <service>     Start a specific service"
    echo "  stop <service>      Stop a specific service"
    echo "  restart <service>   Restart a specific service"
    echo "  status [service]    Show status of service(s)"
    echo "  list                List all available services"
    echo "  help                Show this help message"
    echo
    echo "EXAMPLES:"
    echo "  ./manage-service.sh start customer-service"
    echo "  ./manage-service.sh stop api-gateway"
    echo "  ./manage-service.sh restart auth-service"
    echo "  ./manage-service.sh status customer-service"
    echo "  ./manage-service.sh status              # All services"
    echo
    list_services
}

# Main execution
main() {
    # Create logs directory
    mkdir -p logs

    local command=$1
    local service_name=$2

    case $command in
        start)
            if [ -z "$service_name" ]; then
                log_error "Service name required"
                echo "Usage: ./manage-service.sh start <service-name>"
                list_services
                exit 1
            fi
            start_service "$service_name"
            ;;

        stop)
            if [ -z "$service_name" ]; then
                log_error "Service name required"
                echo "Usage: ./manage-service.sh stop <service-name>"
                list_services
                exit 1
            fi
            stop_service "$service_name"
            ;;

        restart)
            if [ -z "$service_name" ]; then
                log_error "Service name required"
                echo "Usage: ./manage-service.sh restart <service-name>"
                list_services
                exit 1
            fi
            restart_service "$service_name"
            ;;

        status)
            if [ -z "$service_name" ]; then
                # Show all services status
                for svc in "${!SERVICES[@]}"; do
                    check_service_status "$svc"
                done
            else
                check_service_status "$service_name"
            fi
            ;;

        list)
            list_services
            ;;

        help|--help|-h)
            show_usage
            ;;

        *)
            log_error "Unknown command: $command"
            echo
            show_usage
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
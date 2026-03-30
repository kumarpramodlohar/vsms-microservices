#!/bin/bash

# VSMS Microservices Application Stop Script
# This script stops Java services but preserves Docker containers for quick restarts
# Use --force to stop everything including Docker containers

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

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

# Function to stop a service by PID
stop_service() {
    local service_name=$1
    local pid_file="$PROJECT_ROOT/logs/${service_name}.pid"

    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 $pid 2>/dev/null; then
            log_info "Stopping $service_name (PID: $pid)..."
            kill $pid

            # Wait for process to stop
            local count=0
            while kill -0 $pid 2>/dev/null && [ $count -lt 10 ]; do
                sleep 1
                ((count++))
            done

            if kill -0 $pid 2>/dev/null; then
                log_warning "Force killing $service_name..."
                kill -9 $pid
            fi

            log_success "$service_name stopped"
        else
            log_info "$service_name is not running"
        fi
        rm -f "$pid_file"
    else
        log_info "No PID file found for $service_name"
    fi
}

# Function to stop all services
stop_services() {
    log_info "Stopping all microservices..."

    # List of services in reverse dependency order
    local services=(
        "drs-service"
        "fulfilment-service"
        "purchase-service"
        "inventory-service"
        "hr-service"
        "sales-service"
        "cost-service"
        "master-service"
        "customer-service"
        "api-gateway"
        "auth-service"
    )

    for service_name in "${services[@]}"; do
        stop_service "$service_name"
    done

    log_success "All services stopped"
}

# Function to stop infrastructure
stop_infrastructure() {
    log_info "Stopping Docker infrastructure..."

    cd "$PROJECT_ROOT"

    # Stop all Docker containers by default to ensure clean restart
    # This prevents issues when running start-application.sh after stop
    if docker-compose ps | grep -q "Up"; then
        log_info "Stopping all Docker containers for clean restart..."
        docker-compose down 2>/dev/null
        log_success "Docker containers stopped"
    else
        log_info "No Docker containers currently running"
    fi
}

# Function to cleanup logs
cleanup_logs() {
    log_info "Cleaning up log files..."

    if [ -d "$PROJECT_ROOT/logs" ]; then
        # Remove old log files (keep only recent ones)
        find "$PROJECT_ROOT/logs" -name "*.log" -type f -mtime +7 -delete 2>/dev/null || true
        log_info "Old log files cleaned up"
    fi
}

# Function to show final status
show_status() {
    log_info "Final status check..."

    # Check if any services are still running
    local running_services=()
    if [ -d "$PROJECT_ROOT/logs" ]; then
        for pid_file in "$PROJECT_ROOT/logs"/*.pid; do
            if [ -f "$pid_file" ]; then
                local pid=$(cat "$pid_file")
                local service_name=$(basename "$pid_file" .pid)
                if kill -0 $pid 2>/dev/null; then
                    running_services+=("$service_name")
                fi
            fi
        done
    fi

    # Check Docker containers
    local running_containers=$(docker-compose ps 2>/dev/null | grep -c "Up" 2>/dev/null || echo "0")

    if [ ${#running_services[@]} -eq 0 ] && [ "$running_containers" -eq 0 ]; then
        log_success "✅ All services and containers stopped successfully"
    else
        log_warning "⚠️  Some components may still be running:"
        if [ ${#running_services[@]} -gt 0 ]; then
            log_warning "  Services: ${running_services[*]}"
        fi
        if [ "$running_containers" -gt 0 ]; then
            log_warning "  Docker containers: $running_containers still running"
        fi
        log_info "You may need to manually stop remaining processes"
    fi
}

# Function to force cleanup if needed
force_cleanup() {
    log_warning "Performing force cleanup..."

    # Kill any remaining Java processes related to the application
    local java_pids=$(pgrep -f "vsms" || true)
    if [ -n "$java_pids" ]; then
        log_warning "Force killing remaining VSMS Java processes..."
        echo "$java_pids" | xargs kill -9 2>/dev/null || true
    fi

    # Force stop Docker containers completely
    docker-compose down --volumes --remove-orphans 2>/dev/null || true
    log_info "Docker containers force stopped"

    # Clean up PID files
    if [ -d "$PROJECT_ROOT/logs" ]; then
        rm -f "$PROJECT_ROOT/logs"/*.pid
    fi

    log_info "Force cleanup completed"
}

# Main execution
main() {
    log_info "🛑 Stopping VSMS Microservices Application..."
    log_info "Project Root: $PROJECT_ROOT"

    # Parse command line arguments
    local force_cleanup=false
    local preserve_containers=false
    while [[ $# -gt 0 ]]; do
        case $1 in
            --force)
                force_cleanup=true
                shift
                ;;
            --preserve-containers)
                preserve_containers=true
                shift
                ;;
            *)
                log_error "Unknown option: $1"
                echo "Usage: $0 [--force] [--preserve-containers]"
                echo "  --force: Stop all Docker containers and force cleanup"
                echo "  --preserve-containers: Preserve Docker containers for quick restart"
                echo "  Normal stop: Stops Java services and Docker containers for clean restart"
                exit 1
                ;;
        esac
    done

    if [ "$force_cleanup" = true ]; then
        log_warning "Force cleanup requested"
        force_cleanup
        show_status
        exit 0
    fi

    # Normal shutdown sequence
    stop_services
    if [ "$preserve_containers" = false ]; then
        stop_infrastructure
    else
        log_info "Preserving Docker containers (--preserve-containers flag used)"
    fi
    cleanup_logs
    show_status

    log_success "VSMS Microservices Application stopped successfully!"
}

# Run main function
main "$@"
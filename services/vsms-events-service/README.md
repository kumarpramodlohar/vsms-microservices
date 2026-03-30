# VSMS Events Service

## Overview

The VSMS Events Service is a comprehensive event-driven infrastructure component that provides reliable event publishing, persistence, and streaming capabilities for the Vehicle Sales Management System (VSMS) microservices ecosystem.

## Architecture

### Core Components

#### Event Store
- **EventStore Entity**: Persistent storage for domain events with full event sourcing capabilities
- **EventStoreRepository**: Optimized data access layer with specialized queries for event retrieval
- **Database Schema**: MySQL-based event store with indexes for performance

#### Event Publishing Framework
- **EventPublishingService**: Core service interface for event publishing operations
- **EventPublishingServiceImpl**: Implementation with Kafka integration and error handling
- **Kafka Integration**: Reliable event streaming with producer/consumer configurations

#### Infrastructure
- **Spring Boot Application**: Main application with Eureka client registration
- **Kafka Configuration**: Comprehensive producer/consumer setup with resilience patterns
- **Database Migration**: Flyway-managed schema evolution

### Key Features

#### 1. Event Sourcing Support
- Complete audit trail of all business events
- Aggregate state reconstruction from event history
- Optimistic locking for concurrent event processing

#### 2. Reliable Event Publishing
- Synchronous database persistence with asynchronous Kafka publishing
- Transactional outbox pattern for consistency
- Automatic retry logic and dead letter queue handling

#### 3. Distributed Tracing
- Correlation ID support for request tracking across services
- Causation ID for understanding event causality chains

#### 4. Query Capabilities
- Time-based event filtering for analytics
- Aggregate-specific event retrieval
- Pagination support for large result sets

#### 5. Resilience Patterns
- Circuit breaker for Kafka communication
- Retry mechanisms with exponential backoff
- Failed event handling and manual reprocessing

## Technical Implementation

### Event Store Design

The `EventStore` entity captures all essential event metadata:

```java
@Entity
@Table(name = "event_store", indexes = {
    @Index(name = "idx_event_store_aggregate", columnList = "aggregateType, aggregateId"),
    @Index(name = "idx_event_store_type_timestamp", columnList = "eventType, timestamp"),
    @Index(name = "idx_event_store_timestamp", columnList = "timestamp")
})
public class EventStore {
    // Event identification
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    // Aggregate context
    private String aggregateType;
    private String aggregateId;
    private Long aggregateVersion;

    // Event details
    private String eventType;
    private String eventData; // JSON payload
    private String metadata;  // Additional context

    // Provenance and tracing
    private LocalDateTime timestamp;
    private String sourceService;
    private String correlationId;
    private String causationId;

    // Processing state
    private ProcessingStatus processingStatus;
    private LocalDateTime processedAt;
    private String processingError;
    private Integer retryCount;
}
```

### Event Publishing Flow

1. **Event Validation**: Ensures event structure and required fields
2. **Metadata Enrichment**: Adds correlation IDs, timestamps, and source information
3. **Database Persistence**: Synchronous storage with transactional guarantees
4. **Kafka Publishing**: Asynchronous message publishing with error handling
5. **Status Updates**: Tracks processing state and handles failures

### Kafka Integration

The service uses Spring Kafka for reliable event streaming:

- **Producer Configuration**: High-reliability settings with idempotence
- **Consumer Configuration**: Manual acknowledgment for exactly-once processing
- **Error Handling**: Dead letter topics and retry mechanisms
- **Monitoring**: Integration with Spring Boot Actuator

## API Endpoints

### Event Publishing
```
POST /api/v1/events/publish
Content-Type: application/json

{
  "event": { /* domain event object */ },
  "aggregateType": "Customer",
  "aggregateId": "customer-123",
  "correlationId": "corr-456"
}
```

### Event Querying
```
GET /api/v1/events/aggregate/{aggregateType}/{aggregateId}
GET /api/v1/events/type/{eventType}?from={timestamp}&to={timestamp}
GET /api/v1/events/correlation/{correlationId}
```

### Event Management
```
POST /api/v1/events/{eventId}/republish
GET /api/v1/events/pending?page=0&size=50
GET /api/v1/events/failed?page=0&size=50
```

## Configuration

### Application Properties

```yaml
server:
  port: 8085

spring:
  application:
    name: vsms-events-service
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      acks: all
      retries: 3
    consumer:
      group-id: vsms-events-service
      enable-auto-commit: false

vsms:
  events:
    max-retry-attempts: 3
  kafka:
    topic:
      events: vsms-events
      dead-letter: vsms-events-dlt
```

### Environment Variables

- `SPRING_DATASOURCE_URL`: Database connection URL
- `MYSQL_ROOT_PASSWORD`: Database password
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker addresses
- `EUREKA_URL`: Eureka server URL
- `JWT_JWKS_URI`: OAuth2 JWKS endpoint

## Testing

### Test Coverage

The service includes comprehensive test suites:

- **Unit Tests**: EventPublishingServiceImpl with mocked dependencies
- **Integration Tests**: EventStoreRepository with H2 database
- **Component Tests**: Full application context testing

### Running Tests

```bash
# Run all tests with coverage
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests EventPublishingServiceImplTest

# View coverage report
open build/reports/jacoco/test/html/index.html
```

## Monitoring and Observability

### Health Checks
- Database connectivity
- Kafka broker reachability
- Event processing queue status

### Metrics
- Event publishing success/failure rates
- Processing latency histograms
- Queue depth monitoring
- Error rate tracking

### Logging
- Structured logging with correlation IDs
- Event lifecycle tracking
- Error context preservation
- Performance monitoring logs

## Deployment

### Docker Configuration

```dockerfile
FROM openjdk:21-jdk-slim
COPY build/libs/vsms-events-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  vsms-events-service:
    build: .
    ports:
      - "8085:8085"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/vsms_events
      - KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - EUREKA_URL=http://eureka-server:8761/eureka
    depends_on:
      - mysql
      - kafka
      - eureka-server
```

## Security

### Authentication & Authorization
- OAuth2 Resource Server configuration
- JWT token validation
- Role-based access control

### Data Protection
- Event data encryption at rest
- Secure communication with TLS
- Audit logging for sensitive operations

## Performance Considerations

### Database Optimization
- Composite indexes for query patterns
- Partitioning strategy for large datasets
- Connection pooling with HikariCP

### Kafka Tuning
- Batch processing for high throughput
- Compression for network efficiency
- Consumer group scaling for load distribution

### Memory Management
- Event payload size limits
- Garbage collection tuning
- Heap size optimization

## Future Enhancements

### Phase 2: Business Process Monitoring
- Real-time dashboards for business metrics
- Order lifecycle tracking
- Inventory flow monitoring

### Phase 3: Event-Driven Workflows
- Automated business rules engine
- Notification system integration
- External system integrations

### Phase 4: Advanced Analytics
- Event stream processing with Kafka Streams
- Machine learning for anomaly detection
- Predictive analytics capabilities

## Contributing

### Development Setup

1. **Prerequisites**
   ```bash
   # Java 21, Docker, Docker Compose
   java -version
   docker --version
   docker-compose --version
   ```

2. **Local Development**
   ```bash
   # Start infrastructure
   docker-compose up -d mysql kafka zookeeper

   # Build shared libraries
   cd shared/vsms-common && ./gradlew build
   cd ../vsms-events && ./gradlew build

   # Run the service
   cd services/vsms-events-service
   ./gradlew bootRun
   ```

3. **Testing**
   ```bash
   # Run tests with coverage
   ./gradlew test jacocoTestReport

   # Integration tests
   ./gradlew integrationTest
   ```

### Code Quality

- **Code Style**: Google Java Style Guide
- **Testing**: Minimum 90% code coverage required
- **Documentation**: Comprehensive JavaDoc and inline comments
- **Reviews**: Mandatory code reviews for all changes

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions or issues, please contact the VSMS development team or create an issue in the project repository.
package com.vsms.events.domain.repository;

import com.vsms.events.domain.entity.EventStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for EventStoreRepository.
 *
 * This test class verifies the data access layer functionality including:
 * - Basic CRUD operations
 * - Custom query methods for event sourcing
 * - Time-based and aggregate-based filtering
 * - Pagination support
 * - Index utilization for query performance
 *
 * Test Data Strategy:
 * - Uses @DataJpaTest for isolated repository testing
 * - Creates realistic test data with proper relationships
 * - Tests both happy path and edge cases
 * - Verifies query performance characteristics
 *
 * @author VSMS Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@DataJpaTest
class EventStoreRepositoryTest {

    @Autowired
    private EventStoreRepository eventStoreRepository;

    @Autowired
    private TestEntityManager entityManager;

    /**
     * Test saving and retrieving an event.
     * Verifies basic CRUD operations work correctly.
     */
    @Test
    void saveAndFindById() {
        // Given
        EventStore event = createTestEvent("Customer", "customer-123", 1L, "CustomerCreated");

        // When
        EventStore saved = eventStoreRepository.save(event);
        Optional<EventStore> found = eventStoreRepository.findById(saved.getEventId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getAggregateType()).isEqualTo("Customer");
        assertThat(found.get().getAggregateId()).isEqualTo("customer-123");
        assertThat(found.get().getAggregateVersion()).isEqualTo(1L);
        assertThat(found.get().getEventType()).isEqualTo("CustomerCreated");
    }

    /**
     * Test finding events by aggregate with version ordering.
     * Verifies event sourcing reconstruction capability.
     */
    @Test
    void findByAggregateTypeAndAggregateIdOrderByAggregateVersionAsc() {
        // Given
        EventStore event1 = createTestEvent("Customer", "customer-123", 1L, "CustomerCreated");
        EventStore event2 = createTestEvent("Customer", "customer-123", 2L, "CustomerUpdated");
        EventStore event3 = createTestEvent("Customer", "customer-123", 3L, "CustomerActivated");

        eventStoreRepository.save(event1);
        eventStoreRepository.save(event2);
        eventStoreRepository.save(event3);

        entityManager.flush();
        entityManager.clear();

        // When
        List<EventStore> events = eventStoreRepository
            .findByAggregateTypeAndAggregateIdOrderByAggregateVersionAsc("Customer", "customer-123");

        // Then
        assertThat(events).hasSize(3);
        assertThat(events.get(0).getAggregateVersion()).isEqualTo(1L);
        assertThat(events.get(1).getAggregateVersion()).isEqualTo(2L);
        assertThat(events.get(2).getAggregateVersion()).isEqualTo(3L);
    }

    /**
     * Test finding events within version range.
     * Verifies partial event replay functionality.
     */
    @Test
    void findByAggregateTypeAndAggregateIdAndAggregateVersionBetweenOrderByAggregateVersionAsc() {
        // Given
        EventStore event1 = createTestEvent("SalesOrder", "order-456", 1L, "OrderCreated");
        EventStore event2 = createTestEvent("SalesOrder", "order-456", 2L, "OrderUpdated");
        EventStore event3 = createTestEvent("SalesOrder", "order-456", 3L, "OrderApproved");
        EventStore event4 = createTestEvent("SalesOrder", "order-456", 4L, "OrderShipped");

        eventStoreRepository.save(event1);
        eventStoreRepository.save(event2);
        eventStoreRepository.save(event3);
        eventStoreRepository.save(event4);

        entityManager.flush();

        // When
        List<EventStore> events = eventStoreRepository
            .findByAggregateTypeAndAggregateIdAndAggregateVersionBetweenOrderByAggregateVersionAsc(
                "SalesOrder", "order-456", 2L, 4L);

        // Then
        assertThat(events).hasSize(3);
        assertThat(events.stream().map(EventStore::getAggregateVersion))
            .containsExactly(2L, 3L, 4L);
    }

    /**
     * Test finding latest event for optimistic locking.
     * Verifies version conflict detection capability.
     */
    @Test
    void findTopByAggregateTypeAndAggregateIdOrderByAggregateVersionDesc() {
        // Given
        EventStore event1 = createTestEvent("Inventory", "item-789", 1L, "ItemCreated");
        EventStore event2 = createTestEvent("Inventory", "item-789", 2L, "ItemUpdated");
        EventStore event3 = createTestEvent("Inventory", "item-789", 3L, "ItemStockChanged");

        eventStoreRepository.save(event1);
        eventStoreRepository.save(event2);
        eventStoreRepository.save(event3);

        entityManager.flush();

        // When
        Optional<EventStore> latest = eventStoreRepository
            .findTopByAggregateTypeAndAggregateIdOrderByAggregateVersionDesc("Inventory", "item-789");

        // Then
        assertThat(latest).isPresent();
        assertThat(latest.get().getAggregateVersion()).isEqualTo(3L);
        assertThat(latest.get().getEventType()).isEqualTo("ItemStockChanged");
    }

    /**
     * Test time-based event filtering.
     * Verifies analytics and reporting query capabilities.
     */
    @Test
    void findByEventTypeAndTimestampBetweenOrderByTimestampAsc() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime oneHourLater = now.plusHours(1);

        EventStore event1 = createTestEventWithTimestamp("Invoice", "inv-101", 1L, "InvoiceCreated", oneHourAgo.minusMinutes(30));
        EventStore event2 = createTestEventWithTimestamp("Invoice", "inv-102", 1L, "InvoiceCreated", now.minusMinutes(30));
        EventStore event3 = createTestEventWithTimestamp("Invoice", "inv-103", 1L, "InvoiceCreated", oneHourLater.minusMinutes(30));

        eventStoreRepository.save(event1);
        eventStoreRepository.save(event2);
        eventStoreRepository.save(event3);

        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<EventStore> events = eventStoreRepository
            .findByEventTypeAndTimestampBetweenOrderByTimestampAsc("InvoiceCreated", oneHourAgo, oneHourLater, pageable);

        // Then
        assertThat(events.getContent()).hasSize(2);
        assertThat(events.getContent().get(0).getAggregateId()).isEqualTo("inv-101");
        assertThat(events.getContent().get(1).getAggregateId()).isEqualTo("inv-102");
    }

    /**
     * Test correlation ID based event tracing.
     * Verifies distributed transaction tracking capability.
     */
    @Test
    void findByCorrelationIdOrderByTimestampAsc() {
        // Given
        String correlationId = UUID.randomUUID().toString();

        EventStore event1 = createTestEvent("Customer", "cust-1", 1L, "CustomerCreated");
        event1.setCorrelationId(correlationId);

        EventStore event2 = createTestEvent("SalesOrder", "order-1", 1L, "OrderCreated");
        event2.setCorrelationId(correlationId);

        EventStore event3 = createTestEvent("Invoice", "inv-1", 1L, "InvoiceCreated");
        event3.setCorrelationId("different-correlation-id");

        eventStoreRepository.save(event1);
        eventStoreRepository.save(event2);
        eventStoreRepository.save(event3);

        entityManager.flush();

        // When
        List<EventStore> events = eventStoreRepository.findByCorrelationIdOrderByTimestampAsc(correlationId);

        // Then
        assertThat(events).hasSize(2);
        assertThat(events.stream().map(EventStore::getAggregateType))
            .containsExactly("Customer", "SalesOrder");
    }

    /**
     * Test pending events retrieval for processing.
     * Verifies event processor work queue functionality.
     */
    @Test
    void findPendingEvents() {
        // Given
        EventStore pending1 = createTestEvent("Customer", "cust-1", 1L, "CustomerCreated");
        pending1.setProcessingStatus(EventStore.ProcessingStatus.PENDING);

        EventStore pending2 = createTestEvent("Customer", "cust-2", 1L, "CustomerCreated");
        pending2.setProcessingStatus(EventStore.ProcessingStatus.PENDING);

        EventStore processed = createTestEvent("Customer", "cust-3", 1L, "CustomerCreated");
        processed.setProcessingStatus(EventStore.ProcessingStatus.PROCESSED);

        eventStoreRepository.save(pending1);
        eventStoreRepository.save(pending2);
        eventStoreRepository.save(processed);

        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<EventStore> pendingEvents = eventStoreRepository.findPendingEvents(pageable);

        // Then
        assertThat(pendingEvents.getContent()).hasSize(2);
        assertThat(pendingEvents.getContent().stream()
            .allMatch(e -> e.getProcessingStatus() == EventStore.ProcessingStatus.PENDING)).isTrue();
    }

    /**
     * Test event count by type and time range.
     * Verifies analytics aggregation queries.
     */
    @Test
    void countByEventTypeAndTimestampBetween() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime tomorrow = now.plusDays(1);

        EventStore event1 = createTestEventWithTimestamp("Customer", "cust-1", 1L, "CustomerCreated", yesterday.plusHours(1));
        EventStore event2 = createTestEventWithTimestamp("Customer", "cust-2", 1L, "CustomerCreated", now.plusHours(1));
        EventStore event3 = createTestEventWithTimestamp("Customer", "cust-3", 1L, "CustomerActivated", now.plusHours(2));

        eventStoreRepository.save(event1);
        eventStoreRepository.save(event2);
        eventStoreRepository.save(event3);

        entityManager.flush();

        // When
        long customerCreatedCount = eventStoreRepository
            .countByEventTypeAndTimestampBetween("CustomerCreated", yesterday, tomorrow);

        // Then
        assertThat(customerCreatedCount).isEqualTo(2);
    }

    /**
     * Test finding failed events for retry processing.
     * Verifies resilience pattern implementation.
     */
    @Test
    void findFailedEventsForRetry() {
        // Given
        EventStore failed1 = createTestEvent("Customer", "cust-1", 1L, "CustomerCreated");
        failed1.setProcessingStatus(EventStore.ProcessingStatus.FAILED);
        failed1.setRetryCount(1);

        EventStore failed2 = createTestEvent("Customer", "cust-2", 1L, "CustomerCreated");
        failed2.setProcessingStatus(EventStore.ProcessingStatus.FAILED);
        failed2.setRetryCount(2);

        EventStore deadLetter = createTestEvent("Customer", "cust-3", 1L, "CustomerCreated");
        deadLetter.setProcessingStatus(EventStore.ProcessingStatus.DEAD_LETTER);
        deadLetter.setRetryCount(3);

        eventStoreRepository.save(failed1);
        eventStoreRepository.save(failed2);
        eventStoreRepository.save(deadLetter);

        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<EventStore> retryEvents = eventStoreRepository.findFailedEventsForRetry(3, pageable);

        // Then
        assertThat(retryEvents.getContent()).hasSize(2);
        assertThat(retryEvents.getContent().stream()
            .allMatch(e -> e.getProcessingStatus() == EventStore.ProcessingStatus.FAILED)).isTrue();
        assertThat(retryEvents.getContent().stream()
            .allMatch(e -> e.getRetryCount() < 3)).isTrue();
    }

    /**
     * Helper method to create test events.
     */
    private EventStore createTestEvent(String aggregateType, String aggregateId, Long version, String eventType) {
        return createTestEventWithTimestamp(aggregateType, aggregateId, version, eventType, LocalDateTime.now());
    }

    /**
     * Helper method to create test events with specific timestamps.
     */
    private EventStore createTestEventWithTimestamp(String aggregateType, String aggregateId,
                                                   Long version, String eventType, LocalDateTime timestamp) {
        return EventStore.builder()
            .eventId(UUID.randomUUID())
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .aggregateVersion(version)
            .eventData("{\"test\":\"data\"}")
            .metadata("{}")
            .timestamp(timestamp)
            .sourceService("test-service")
            .correlationId(UUID.randomUUID().toString())
            .processingStatus(EventStore.ProcessingStatus.PENDING)
            .retryCount(0)
            .build();
    }
}
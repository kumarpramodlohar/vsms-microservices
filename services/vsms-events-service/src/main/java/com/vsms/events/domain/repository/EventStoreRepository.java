package com.vsms.events.domain.repository;

import com.vsms.events.domain.entity.EventStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for EventStore entity operations.
 *
 * This repository provides data access methods for the event store, supporting event sourcing
 * patterns with optimized queries for common use cases like event replay, analytics, and
 * real-time event streaming.
 *
 * Key Query Patterns:
 * - Aggregate event retrieval for event sourcing
 * - Time-based event filtering for analytics
 * - Event type filtering for specific business logic
 * - Pagination support for large result sets
 * - Correlation ID queries for distributed tracing
 *
 * Performance Considerations:
 * - Uses composite indexes for efficient querying
 * - Supports pagination to prevent memory issues
 * - Optimized for time-range and aggregate-based queries
 *
 * @author VSMS Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@Repository
public interface EventStoreRepository extends JpaRepository<EventStore, UUID> {

    /**
     * Finds all events for a specific aggregate ordered by version.
     * Used for event sourcing to reconstruct aggregate state.
     *
     * @param aggregateType The type of aggregate (e.g., "Customer", "SalesOrder")
     * @param aggregateId The unique identifier of the aggregate
     * @return List of events in version order for reconstructing aggregate state
     */
    List<EventStore> findByAggregateTypeAndAggregateIdOrderByAggregateVersionAsc(
        String aggregateType,
        String aggregateId
    );

    /**
     * Finds events by aggregate type and ID within a specific version range.
     * Useful for partial event replay or conflict resolution.
     *
     * @param aggregateType The type of aggregate
     * @param aggregateId The aggregate identifier
     * @param fromVersion Minimum version (inclusive)
     * @param toVersion Maximum version (inclusive)
     * @return List of events within the version range
     */
    List<EventStore> findByAggregateTypeAndAggregateIdAndAggregateVersionBetweenOrderByAggregateVersionAsc(
        String aggregateType,
        String aggregateId,
        Long fromVersion,
        Long toVersion
    );

    /**
     * Retrieves events of a specific type within a time range.
     * Used for analytics and business intelligence queries.
     *
     * @param eventType The type of event to filter
     * @param fromTime Start of time range (inclusive)
     * @param toTime End of time range (inclusive)
     * @param pageable Pagination information
     * @return Page of events matching the criteria
     */
    Page<EventStore> findByEventTypeAndTimestampBetweenOrderByTimestampAsc(
        String eventType,
        LocalDateTime fromTime,
        LocalDateTime toTime,
        Pageable pageable
    );

    /**
     * Finds events by aggregate type within a time range.
     * Useful for monitoring business processes by domain.
     *
     * @param aggregateType The aggregate type to filter
     * @param fromTime Start timestamp
     * @param toTime End timestamp
     * @param pageable Pagination for large result sets
     * @return Page of events for the specified aggregate type and time range
     */
    Page<EventStore> findByAggregateTypeAndTimestampBetweenOrderByTimestampAsc(
        String aggregateType,
        LocalDateTime fromTime,
        LocalDateTime toTime,
        Pageable pageable
    );

    /**
     * Retrieves events by correlation ID for distributed tracing.
     * Links all events that are part of the same business transaction.
     *
     * @param correlationId The correlation identifier
     * @return List of related events in chronological order
     */
    List<EventStore> findByCorrelationIdOrderByTimestampAsc(String correlationId);

    /**
     * Finds events that are still pending processing.
     * Used by event processors to identify work to be done.
     *
     * @param pageable Pagination for processing batches
     * @return Page of unprocessed events
     */
    @Query("SELECT e FROM EventStore e WHERE e.processingStatus = 'PENDING' ORDER BY e.timestamp ASC")
    Page<EventStore> findPendingEvents(Pageable pageable);

    /**
     * Counts events by type and time range for analytics.
     * Provides aggregated statistics without loading full event data.
     *
     * @param eventType The event type to count
     * @param fromTime Start of time range
     * @param toTime End of time range
     * @return Count of events matching the criteria
     */
    @Query("SELECT COUNT(e) FROM EventStore e WHERE e.eventType = :eventType AND e.timestamp BETWEEN :fromTime AND :toTime")
    long countByEventTypeAndTimestampBetween(
        @Param("eventType") String eventType,
        @Param("fromTime") LocalDateTime fromTime,
        @Param("toTime") LocalDateTime toTime
    );

    /**
     * Finds the latest event for a specific aggregate.
     * Used to determine current aggregate version for optimistic locking.
     *
     * @param aggregateType The aggregate type
     * @param aggregateId The aggregate identifier
     * @return Optional containing the latest event, or empty if no events exist
     */
    Optional<EventStore> findTopByAggregateTypeAndAggregateIdOrderByAggregateVersionDesc(
        String aggregateType,
        String aggregateId
    );

    /**
     * Retrieves events that have failed processing and may need retry.
     * Supports resilience patterns like circuit breakers and retry logic.
     *
     * @param maxRetries Maximum number of retry attempts
     * @param pageable Pagination for processing failed events
     * @return Page of failed events eligible for retry
     */
    @Query("SELECT e FROM EventStore e WHERE e.processingStatus = 'FAILED' AND e.retryCount < :maxRetries ORDER BY e.timestamp ASC")
    Page<EventStore> findFailedEventsForRetry(@Param("maxRetries") int maxRetries, Pageable pageable);

    /**
     * Finds events by source service for monitoring and debugging.
     * Helps track which services are publishing events and identify issues.
     *
     * @param sourceService The name of the source service
     * @param fromTime Start timestamp
     * @param toTime End timestamp
     * @param pageable Pagination
     * @return Page of events from the specified service
     */
    Page<EventStore> findBySourceServiceAndTimestampBetweenOrderByTimestampAsc(
        String sourceService,
        LocalDateTime fromTime,
        LocalDateTime toTime,
        Pageable pageable
    );
}
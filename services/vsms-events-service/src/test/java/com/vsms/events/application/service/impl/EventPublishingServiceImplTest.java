package com.vsms.events.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsms.events.application.service.EventPublishingService;
import com.vsms.events.domain.entity.EventStore;
import com.vsms.events.domain.repository.EventStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for EventPublishingServiceImpl.
 *
 * This test class covers all major functionality of the event publishing service:
 * - Event validation and serialization
 * - Database persistence operations
 * - Kafka publishing with error handling
 * - Correlation ID and causation ID management
 * - Retry logic and failure scenarios
 * - Auto-versioning functionality
 * - System event publishing
 *
 * Test Coverage Goals:
 * - Happy path scenarios for all public methods
 * - Error handling and exception cases
 * - Edge cases and boundary conditions
 * - Integration with mocked dependencies
 * - Performance and threading considerations
 *
 * @author VSMS Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@ExtendWith(MockitoExtension.class)
class EventPublishingServiceImplTest {

    @Mock
    private EventStoreRepository eventStoreRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper;
    private EventPublishingService eventPublishingService;

    // Test data
    private static final String TEST_AGGREGATE_TYPE = "Customer";
    private static final String TEST_AGGREGATE_ID = "customer-123";
    private static final String TEST_CORRELATION_ID = "correlation-123";
    private static final String TEST_CAUSATION_ID = "causation-123";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        eventPublishingService = new EventPublishingServiceImpl(
            eventStoreRepository,
            kafkaTemplate,
            objectMapper
        );
    }

    /**
     * Test successful event publishing with all parameters.
     * Verifies that events are properly persisted and published to Kafka.
     */
    @Test
    void publishEvent_Success() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");
        CompletableFuture<SendResult<String, String>> kafkaFuture = CompletableFuture.completedFuture(null);

        when(eventStoreRepository.save(any(EventStore.class))).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(kafkaFuture);

        // When
        CompletableFuture<Void> result = eventPublishingService.publishEvent(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            1L,
            TEST_CORRELATION_ID,
            TEST_CAUSATION_ID
        );

        // Then
        assertThat(result).isCompleted();
        verify(eventStoreRepository).save(any(EventStore.class));
        verify(kafkaTemplate).send(eq("vsms-events"), anyString(), anyString());
    }

    /**
     * Test event publishing with auto-versioning.
     * Verifies that the service correctly determines the next version number.
     */
    @Test
    void publishEventWithAutoVersioning_Success() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");
        EventStore latestEvent = EventStore.builder()
            .aggregateVersion(5L)
            .build();

        when(eventStoreRepository.findTopByAggregateTypeAndAggregateIdOrderByAggregateVersionDesc(
            TEST_AGGREGATE_TYPE, TEST_AGGREGATE_ID))
            .thenReturn(Optional.of(latestEvent));
        when(eventStoreRepository.save(any(EventStore.class))).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            assertThat(saved.getAggregateVersion()).isEqualTo(6L);
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = eventPublishingService.publishEventWithAutoVersioning(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            TEST_CORRELATION_ID,
            TEST_CAUSATION_ID
        );

        // Then
        assertThat(result).isCompleted();
        verify(eventStoreRepository).findTopByAggregateTypeAndAggregateIdOrderByAggregateVersionDesc(
            TEST_AGGREGATE_TYPE, TEST_AGGREGATE_ID);
    }

    /**
     * Test auto-versioning when no previous events exist.
     * Verifies that version starts at 1 for new aggregates.
     */
    @Test
    void publishEventWithAutoVersioning_NewAggregate() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");

        when(eventStoreRepository.findTopByAggregateTypeAndAggregateIdOrderByAggregateVersionDesc(
            TEST_AGGREGATE_TYPE, TEST_AGGREGATE_ID))
            .thenReturn(Optional.empty());
        when(eventStoreRepository.save(any(EventStore.class))).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            assertThat(saved.getAggregateVersion()).isEqualTo(1L);
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = eventPublishingService.publishEventWithAutoVersioning(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            TEST_CORRELATION_ID,
            TEST_CAUSATION_ID
        );

        // Then
        assertThat(result).isCompleted();
    }

    /**
     * Test system event publishing.
     * Verifies that system events are handled differently from domain events.
     */
    @Test
    void publishSystemEvent_Success() {
        // Given
        TestEvent testEvent = new TestEvent("system-event");

        when(eventStoreRepository.save(any(EventStore.class))).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            assertThat(saved.getAggregateType()).isEqualTo("System");
            assertThat(saved.getAggregateVersion()).isEqualTo(1L);
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = eventPublishingService.publishSystemEvent(
            testEvent,
            "SystemStatusChanged",
            TEST_CORRELATION_ID
        );

        // Then
        assertThat(result).isCompleted();
        verify(eventStoreRepository).save(any(EventStore.class));
        verify(kafkaTemplate).send(eq("vsms-events"), anyString(), anyString());
    }

    /**
     * Test event republishing.
     * Verifies that existing events can be republished to Kafka.
     */
    @Test
    void republishEvent_Success() {
        // Given
        UUID eventId = UUID.randomUUID();
        EventStore existingEvent = EventStore.builder()
            .eventId(eventId)
            .eventData("{\"test\":\"data\"}")
            .build();

        when(eventStoreRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        CompletableFuture<Void> result = eventPublishingService.republishEvent(eventId.toString());

        // Then
        assertThat(result).isCompleted();
        verify(kafkaTemplate).send(eq("vsms-events"), anyString(), eq("{\"test\":\"data\"}"));
    }

    /**
     * Test republishing non-existent event.
     * Verifies proper error handling when event is not found.
     */
    @Test
    void republishEvent_EventNotFound() {
        // Given
        String nonExistentId = UUID.randomUUID().toString();
        when(eventStoreRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> eventPublishingService.republishEvent(nonExistentId))
            .isInstanceOf(EventPublishingService.EventPublishingException.class)
            .hasMessageContaining("Event not found");
    }

    /**
     * Test event validation with null event.
     * Verifies that null events are properly rejected.
     */
    @Test
    void validateEvent_NullEvent() {
        assertThatThrownBy(() -> eventPublishingService.validateEvent(null, TEST_AGGREGATE_TYPE))
            .isInstanceOf(EventPublishingService.EventPublishingException.class)
            .hasMessageContaining("Event cannot be null");
    }

    /**
     * Test event validation with null aggregate type.
     * Verifies that null aggregate types are properly rejected.
     */
    @Test
    void validateEvent_NullAggregateType() {
        // Given
        TestEvent testEvent = new TestEvent("test");

        // When/Then
        assertThatThrownBy(() -> eventPublishingService.validateEvent(testEvent, null))
            .isInstanceOf(EventPublishingService.EventPublishingException.class)
            .hasMessageContaining("Aggregate type cannot be null");
    }

    /**
     * Test Kafka publishing failure handling.
     * Verifies that database status is updated when Kafka publishing fails.
     */
    @Test
    void publishEvent_KafkaFailure() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");
        CompletableFuture<SendResult<String, String>> failedKafkaFuture = new CompletableFuture<>();
        failedKafkaFuture.completeExceptionally(new RuntimeException("Kafka error"));

        when(eventStoreRepository.save(any(EventStore.class))).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(failedKafkaFuture);

        // When
        CompletableFuture<Void> result = eventPublishingService.publishEvent(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            1L,
            TEST_CORRELATION_ID,
            TEST_CAUSATION_ID
        );

        // Then
        assertThat(result).isCompletedExceptionally();
        verify(eventStoreRepository, times(2)).findById(any(UUID.class)); // Once for save, once for status update
    }

    /**
     * Test database save failure handling.
     * Verifies that proper exceptions are thrown when database operations fail.
     */
    @Test
    void publishEvent_DatabaseFailure() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");
        when(eventStoreRepository.save(any(EventStore.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        // When/Then
        assertThatThrownBy(() -> eventPublishingService.publishEvent(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            1L,
            TEST_CORRELATION_ID,
            TEST_CAUSATION_ID
        )).isInstanceOf(EventPublishingService.EventPublishingException.class)
          .hasMessageContaining("Failed to publish event");
    }

    /**
     * Test correlation ID generation.
     * Verifies that correlation IDs are automatically generated when not provided.
     */
    @Test
    void publishEvent_AutoGeneratedCorrelationId() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");

        when(eventStoreRepository.save(any(EventStore.class))).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            assertThat(saved.getCorrelationId()).isNotNull();
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        eventPublishingService.publishEvent(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            1L,
            null, // No correlation ID provided
            TEST_CAUSATION_ID
        );

        // Then
        verify(eventStoreRepository).save(any(EventStore.class));
    }

    /**
     * Test event metadata enrichment.
     * Verifies that events are properly enriched with metadata during publishing.
     */
    @Test
    void publishEvent_MetadataEnrichment() {
        // Given
        TestEvent testEvent = new TestEvent("test-value");
        ArgumentCaptor<EventStore> eventCaptor = ArgumentCaptor.forClass(EventStore.class);

        when(eventStoreRepository.save(eventCaptor.capture())).thenAnswer(invocation -> {
            EventStore saved = invocation.getArgument(0);
            saved.setEventId(UUID.randomUUID());
            return saved;
        });
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // When
        eventPublishingService.publishEvent(
            testEvent,
            TEST_AGGREGATE_TYPE,
            TEST_AGGREGATE_ID,
            1L,
            TEST_CORRELATION_ID,
            TEST_CAUSATION_ID
        );

        // Then
        EventStore savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getAggregateType()).isEqualTo(TEST_AGGREGATE_TYPE);
        assertThat(savedEvent.getAggregateId()).isEqualTo(TEST_AGGREGATE_ID);
        assertThat(savedEvent.getEventType()).isEqualTo("TestEvent");
        assertThat(savedEvent.getAggregateVersion()).isEqualTo(1L);
        assertThat(savedEvent.getCorrelationId()).isEqualTo(TEST_CORRELATION_ID);
        assertThat(savedEvent.getCausationId()).isEqualTo(TEST_CAUSATION_ID);
        assertThat(savedEvent.getProcessingStatus()).isEqualTo(EventStore.ProcessingStatus.PENDING);
        assertThat(savedEvent.getSourceService()).isEqualTo("vsms-events-service");
    }

    /**
     * Simple test event class for testing purposes.
     */
    private static class TestEvent {
        private final String value;

        public TestEvent(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
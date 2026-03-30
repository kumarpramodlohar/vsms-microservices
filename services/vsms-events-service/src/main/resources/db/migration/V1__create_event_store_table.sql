-- VSMS Events Service - Event Store Table Creation
-- Migration: V1__create_event_store_table
-- Date: 2026-03-30
-- Description: Creates the event_store table for event sourcing and audit trails

-- Create event_store table with optimized structure for event sourcing
CREATE TABLE event_store (
    -- Primary key using UUID for global uniqueness
    event_id VARCHAR(36) PRIMARY KEY,

    -- Aggregate identification for event sourcing
    aggregate_type VARCHAR(100) NOT NULL COMMENT 'Type of aggregate (Customer, SalesOrder, etc.)',
    aggregate_id VARCHAR(255) NOT NULL COMMENT 'Unique identifier of the aggregate instance',
    aggregate_version BIGINT NOT NULL COMMENT 'Version of the aggregate for optimistic locking',

    -- Event metadata
    event_type VARCHAR(100) NOT NULL COMMENT 'Type of the event (CustomerApproved, etc.)',
    event_data TEXT NOT NULL COMMENT 'JSON payload containing event data',
    metadata TEXT COMMENT 'Additional metadata as JSON',

    -- Temporal information
    timestamp TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'When the event was created',
    processed_at TIMESTAMP(6) NULL COMMENT 'When the event was processed',

    -- Provenance tracking
    source_service VARCHAR(100) COMMENT 'Service that published the event',
    correlation_id VARCHAR(255) COMMENT 'Correlation ID for distributed tracing',
    causation_id VARCHAR(255) COMMENT 'ID of the event that caused this event',

    -- Processing state management
    processing_status ENUM('PENDING', 'PROCESSED', 'FAILED', 'DEAD_LETTER') NOT NULL DEFAULT 'PENDING',
    processing_error TEXT COMMENT 'Error message if processing failed',
    retry_count INT NOT NULL DEFAULT 0 COMMENT 'Number of processing attempts',

    -- Indexes for query performance
    INDEX idx_event_store_aggregate (aggregate_type, aggregate_id),
    INDEX idx_event_store_type_timestamp (event_type, timestamp),
    INDEX idx_event_store_timestamp (timestamp),
    INDEX idx_event_store_correlation (correlation_id),
    INDEX idx_event_store_processing_status (processing_status),
    INDEX idx_event_store_source_service (source_service)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT 'Event store table for event sourcing and audit trails in VSMS';

-- Add check constraints for data integrity
ALTER TABLE event_store
    ADD CONSTRAINT chk_aggregate_version_positive CHECK (aggregate_version >= 0),
    ADD CONSTRAINT chk_retry_count_non_negative CHECK (retry_count >= 0);

-- Create partition by month for better performance on large datasets
-- Note: Partitioning can be added later when data volume justifies it
-- PARTITION BY RANGE (YEAR(timestamp)) (
--     PARTITION p2026 VALUES LESS THAN (2027),
--     PARTITION p2027 VALUES LESS THAN (2028),
--     PARTITION future VALUES LESS THAN MAXVALUE
-- );
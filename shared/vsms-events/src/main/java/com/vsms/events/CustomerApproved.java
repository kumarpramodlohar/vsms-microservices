package com.vsms.events;

// AUTO-GENERATED: event schema shared via this library JAR
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerApproved(
    UUID customerId,
    String customerName,
    String gstNumber,
    String contactEmail,
    LocalDateTime approvedAt
) {}
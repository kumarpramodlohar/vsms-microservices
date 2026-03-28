package com.vsms.events;

// AUTO-GENERATED: event schema
import java.time.LocalDateTime;

public record SalesOrderCostApproved(
    String orderCode,
    String approvedBy,
    String remarks,
    LocalDateTime approvedAt
) {}
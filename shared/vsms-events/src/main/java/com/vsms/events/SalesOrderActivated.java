package com.vsms.events;

// AUTO-GENERATED: event schema shared via this library JAR
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SalesOrderActivated(
    String orderCode,
    UUID customerId,
    Long companyId,
    BigDecimal totalAmount,
    LocalDateTime activatedAt
) {}
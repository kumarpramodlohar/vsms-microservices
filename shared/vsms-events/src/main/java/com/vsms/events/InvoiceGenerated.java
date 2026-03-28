package com.vsms.events;

// AUTO-GENERATED: event schema
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InvoiceGenerated(
    Long invoiceId,
    String invoiceNumber,
    String orderCode,
    UUID customerId,
    BigDecimal totalAmount,
    LocalDateTime generatedAt
) {}
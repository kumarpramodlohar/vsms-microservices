package com.vsms.events;

// AUTO-GENERATED: event schema
import java.time.LocalDateTime;

public record GrnApproved(
    Long purchaseHeaderId,
    Long itemId,
    Double quantity,
    LocalDateTime approvedAt
) {}
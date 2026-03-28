package com.vsms.inventory.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Stock transaction header entity.
 * TODO: add fields from monolith com.vsms.stock.domain.entity.StockHeader (table: trn_stock_hdr)
 * Key fields to migrate: transactionType, referenceNumber, transactionDate,
 *   locationId, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "trn_stock_hdr")
@Getter
@Setter
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith trn_stock_hdr table
}

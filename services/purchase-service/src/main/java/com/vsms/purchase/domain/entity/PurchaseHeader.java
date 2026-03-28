package com.vsms.purchase.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Purchase bill header entity.
 * TODO: add fields from monolith com.vsms.purchase.domain.entity.PurchaseHeader (table: trn_purchase_header)
 * Key fields to migrate: billNumber, billDate, vendorId, companyId, totalAmount,
 *   gstAmount, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "trn_purchase_header")
@Getter
@Setter
public class PurchaseHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith trn_purchase_header table
}

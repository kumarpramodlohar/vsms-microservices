package com.vsms.sales.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Sales order header entity.
 * TODO: add fields from monolith com.vsms.sales.domain.entity.SalesOrderHeader (table: trn_order_header)
 * Key fields to migrate: orderCode, customerId, companyId, orderDate, basicAmount, taxableAmount,
 *   gstAmount, totalAmount, cgstAmount, sgstAmount, igstAmount, discountAmount,
 *   status (DRAFT/ACTIVE/CANCELLED), isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "trn_order_header")
@Getter
@Setter
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith trn_order_header table
}

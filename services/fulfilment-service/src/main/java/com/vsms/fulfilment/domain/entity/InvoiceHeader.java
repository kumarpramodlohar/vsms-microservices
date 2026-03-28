package com.vsms.fulfilment.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Invoice/bill header entity.
 * TODO: add fields from monolith com.vsms.invoicing.domain.entity.BillHeader (table: trn_bill_header)
 * Key fields to migrate: invoiceNumber, invoiceType, invoiceDate, orderCode, customerId,
 *   basicAmount, gstAmount, totalAmount, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "trn_bill_header")
@Getter
@Setter
public class InvoiceHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith trn_bill_header table
}

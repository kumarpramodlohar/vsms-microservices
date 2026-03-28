package com.vsms.cost.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Cost header entity.
 * TODO: add fields from monolith com.vsms.cost.domain.entity.CostHeader (table: trn_cost_header)
 * Key fields to migrate: orderCode, costDate, totalCost, approvalStatus (PENDING/APPROVED/REJECTED),
 *   approvedBy, approvedAt, remarks, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "trn_cost_header")
@Getter
@Setter
public class CostHeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith trn_cost_header table
}

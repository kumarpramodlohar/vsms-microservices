package com.vsms.customer.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Customer entity.
 * TODO: add fields from monolith com.vsms.customer.domain.entity.Customer (table: customers)
 * Key fields to migrate: customerName, gstNumber, panNumber, email, phone, address,
 *   city, state, country, pincode, status (PENDING/APPROVED/REJECTED), isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    // TODO: add remaining fields from monolith customers table
}

package com.vsms.master.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Item master entity.
 * TODO: add fields from monolith com.vsms.master.domain.entity.Item (table: tbl_master_item)
 * Key fields to migrate: itemCode, itemName, description, categoryId, subcategoryId,
 *   uomId, hsnCode, taxPercentage, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "tbl_master_item")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith tbl_master_item table
}
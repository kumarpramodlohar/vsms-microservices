package com.vsms.auth.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * User entity.
 * TODO: add fields from monolith com.vsms.admin.domain.entity.User (table: adm_users)
 * Key fields to migrate: username, password (hashed), email, userType, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "adm_users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith adm_users table
}
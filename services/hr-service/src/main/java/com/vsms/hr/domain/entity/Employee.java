package com.vsms.hr.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Employee master entity.
 * TODO: add fields from monolith com.vsms.hr.domain.entity.Employee (table: mst_employee)
 * Key fields to migrate: employeeCode, firstName, lastName, email, phone, dateOfBirth,
 *   dateOfJoining, designationId, departmentId, locationId, salary, isActive, createdAt, updatedAt
 */
@Entity
@Table(name = "mst_employee")
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: add remaining fields from monolith mst_employee table
}

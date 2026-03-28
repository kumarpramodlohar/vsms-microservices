package com.vsms.customer.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for customer lifecycle management.
 * TODO: migrate logic from com.vsms.customer.api.controller.CustomerController in monolith
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Customer lifecycle management — CRUD and approval workflow (PENDING → APPROVED/REJECTED)")
public class CustomerController {

    // TODO: inject CustomerService

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Object request) {
        // TODO: implement createCustomer — migrate from com.vsms.customer.service.impl.CustomerServiceImpl
        // Business rule: new customers default to PENDING approval status
        // Validate unique: customerName, gstNumber, panNumber
        throw new UnsupportedOperationException("TODO: implement createCustomer");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {
        // TODO: implement getCustomerById — throw ResourceNotFoundException if not found or inactive
        throw new UnsupportedOperationException("TODO: implement getCustomerById");
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        // TODO: implement getAllCustomers with pagination and filters
        throw new UnsupportedOperationException("TODO: implement getAllCustomers");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable UUID id, @RequestBody Object request) {
        // TODO: implement updateCustomer
        throw new UnsupportedOperationException("TODO: implement updateCustomer");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {
        // TODO: implement soft delete (set is_active = false)
        throw new UnsupportedOperationException("TODO: implement deleteCustomer");
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveCustomer(@PathVariable UUID id) {
        // TODO: implement approveCustomer — set status = APPROVED, publish CustomerApproved event
        throw new UnsupportedOperationException("TODO: implement approveCustomer");
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectCustomer(@PathVariable UUID id) {
        // TODO: implement rejectCustomer — set status = REJECTED
        throw new UnsupportedOperationException("TODO: implement rejectCustomer");
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> customerExists(@PathVariable UUID id) {
        // TODO: implement customerExists — used by sales-service Feign client
        throw new UnsupportedOperationException("TODO: implement customerExists");
    }
}

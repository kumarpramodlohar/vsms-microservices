package com.vsms.cost.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for cost estimation and approval.
 * TODO: migrate logic from com.vsms.cost.api.controller.CostController in monolith
 */
@RestController
@RequestMapping("/api/v1/cost")
@RequiredArgsConstructor
@Tag(name = "Cost Management", description = "Cost estimation and approval workflow for sales orders. Part of the order activation saga.")
public class CostController {

    // TODO: inject CostService

    @PostMapping
    public ResponseEntity<?> createCostHeader(@RequestBody Object request) {
        // TODO: implement createCostHeader for a sales order
        // Validate: sales order must exist (via SalesServiceClient Feign call)
        throw new UnsupportedOperationException("TODO: implement createCostHeader");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCostHeaderById(@PathVariable Long id) {
        // TODO: implement getCostHeaderById
        throw new UnsupportedOperationException("TODO: implement getCostHeaderById");
    }

    @GetMapping
    public ResponseEntity<?> getAllCostHeaders() {
        // TODO: implement getAllCostHeaders with pagination
        throw new UnsupportedOperationException("TODO: implement getAllCostHeaders");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCostHeader(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateCostHeader
        throw new UnsupportedOperationException("TODO: implement updateCostHeader");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCostHeader(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteCostHeader");
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveCost(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement approveCost
        // Publish SalesOrderCostApproved event to vsms.cost exchange
        // Then call sales-service via Feign to activate the order
        throw new UnsupportedOperationException("TODO: implement approveCost");
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectCost(@PathVariable Long id) {
        // TODO: implement rejectCost — set status = REJECTED
        throw new UnsupportedOperationException("TODO: implement rejectCost");
    }
}

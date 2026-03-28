package com.vsms.sales.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for sales order management.
 * TODO: migrate logic from com.vsms.sales.api.controller.SalesOrderController in monolith
 */
@RestController
@RequestMapping("/api/v1/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Orders", description = "Offers and sales orders — hub of the order lifecycle. GST calculations live here.")
public class SalesOrderController {

    // TODO: inject SalesOrderService

    @PostMapping
    public ResponseEntity<?> createSalesOrder(@RequestBody Object request) {
        // TODO: implement createSalesOrder
        // Validate: customer must exist and be APPROVED (via CustomerServiceClient Feign call)
        // Initial status: DRAFT
        throw new UnsupportedOperationException("TODO: implement createSalesOrder");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalesOrderById(@PathVariable Long id) {
        // TODO: implement getSalesOrderById
        throw new UnsupportedOperationException("TODO: implement getSalesOrderById");
    }

    @GetMapping("/by-code/{orderCode}")
    public ResponseEntity<?> getSalesOrderByCode(@PathVariable String orderCode) {
        // TODO: implement getSalesOrderByCode — used by fulfilment-service and cost-service Feign clients
        throw new UnsupportedOperationException("TODO: implement getSalesOrderByCode");
    }

    @GetMapping
    public ResponseEntity<?> getAllSalesOrders() {
        // TODO: implement getAllSalesOrders with pagination and filters
        throw new UnsupportedOperationException("TODO: implement getAllSalesOrders");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSalesOrder(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateSalesOrder
        throw new UnsupportedOperationException("TODO: implement updateSalesOrder");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSalesOrder(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteSalesOrder");
    }

    @PatchMapping("/by-code/{orderCode}/activate")
    public ResponseEntity<?> activateSalesOrder(@PathVariable String orderCode) {
        // TODO: implement activateSalesOrder — called by cost-service after cost approval
        // Publish SalesOrderActivated event to vsms.sales exchange
        throw new UnsupportedOperationException("TODO: implement activateSalesOrder");
    }
}

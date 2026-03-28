package com.vsms.purchase.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for purchase management.
 * TODO: migrate logic from com.vsms.purchase.api.controller.PurchaseController in monolith
 */
@RestController
@RequestMapping("/api/v1/purchase")
@RequiredArgsConstructor
@Tag(name = "Purchase Management", description = "Purchase indents, purchase orders, GRN — vendor procurement lifecycle")
public class PurchaseController {

    // TODO: inject PurchaseService

    @PostMapping("/indents")
    public ResponseEntity<?> createIndent(@RequestBody Object request) {
        // TODO: implement createIndent — migrate from com.vsms.purchase.service.impl.PurchaseServiceImpl
        throw new UnsupportedOperationException("TODO: implement createIndent");
    }

    @GetMapping("/indents/{id}")
    public ResponseEntity<?> getIndentById(@PathVariable Long id) {
        // TODO: implement getIndentById
        throw new UnsupportedOperationException("TODO: implement getIndentById");
    }

    @GetMapping("/indents")
    public ResponseEntity<?> getAllIndents() {
        // TODO: implement getAllIndents with pagination
        throw new UnsupportedOperationException("TODO: implement getAllIndents");
    }

    @PutMapping("/indents/{id}")
    public ResponseEntity<?> updateIndent(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateIndent
        throw new UnsupportedOperationException("TODO: implement updateIndent");
    }

    @DeleteMapping("/indents/{id}")
    public ResponseEntity<?> deleteIndent(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteIndent");
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createPurchaseOrder(@RequestBody Object request) {
        // TODO: implement createPurchaseOrder (PO from indent)
        throw new UnsupportedOperationException("TODO: implement createPurchaseOrder");
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getPurchaseOrderById(@PathVariable Long id) {
        // TODO: implement getPurchaseOrderById
        throw new UnsupportedOperationException("TODO: implement getPurchaseOrderById");
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllPurchaseOrders() {
        // TODO: implement getAllPurchaseOrders
        throw new UnsupportedOperationException("TODO: implement getAllPurchaseOrders");
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<?> updatePurchaseOrder(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updatePurchaseOrder
        throw new UnsupportedOperationException("TODO: implement updatePurchaseOrder");
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> deletePurchaseOrder(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deletePurchaseOrder");
    }

    @PatchMapping("/orders/{id}/approve-grn")
    public ResponseEntity<?> approveGrn(@PathVariable Long id) {
        // TODO: implement approveGrn — publish GrnApproved event to vsms.purchase exchange
        throw new UnsupportedOperationException("TODO: implement approveGrn");
    }
}

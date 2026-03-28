package com.vsms.fulfilment.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for invoice and delivery challan management.
 * TODO: migrate logic from com.vsms.invoicing.api.controller.InvoiceController in monolith
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Fulfilment — Invoices & Delivery Challans", description = "Delivery challans and invoice generation. Downstream of sales-service.")
public class InvoiceController {

    // TODO: inject InvoiceService

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody Object request) {
        // TODO: implement createInvoice
        // Invoice types: CASH, CREDIT, PROFORMA, TAX, EXPORT
        // Validate: sales order must be ACTIVE (via SalesServiceClient Feign call)
        throw new UnsupportedOperationException("TODO: implement createInvoice");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
        // TODO: implement getInvoiceById
        throw new UnsupportedOperationException("TODO: implement getInvoiceById");
    }

    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        // TODO: implement getAllInvoices with pagination
        throw new UnsupportedOperationException("TODO: implement getAllInvoices");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateInvoice
        throw new UnsupportedOperationException("TODO: implement updateInvoice");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteInvoice");
    }

    @PostMapping("/delivery-challans")
    public ResponseEntity<?> createDeliveryChallan(@RequestBody Object request) {
        // TODO: implement createDeliveryChallan
        throw new UnsupportedOperationException("TODO: implement createDeliveryChallan");
    }

    @GetMapping("/delivery-challans/{id}")
    public ResponseEntity<?> getDeliveryChallanById(@PathVariable Long id) {
        // TODO: implement getDeliveryChallanById
        throw new UnsupportedOperationException("TODO: implement getDeliveryChallanById");
    }

    @GetMapping("/delivery-challans")
    public ResponseEntity<?> getAllDeliveryChallans() {
        // TODO: implement getAllDeliveryChallans
        throw new UnsupportedOperationException("TODO: implement getAllDeliveryChallans");
    }
}

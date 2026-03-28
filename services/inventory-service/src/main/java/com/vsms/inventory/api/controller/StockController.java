package com.vsms.inventory.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for stock and inventory management.
 * TODO: migrate logic from com.vsms.stock.api.controller.StockController in monolith
 */
@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@Tag(name = "Inventory / Stock", description = "Stock ledger and stock transactions — tracks stock-in (GRN) and stock-out (delivery challan)")
public class StockController {

    // TODO: inject StockService

    @PostMapping("/transactions")
    public ResponseEntity<?> createStockTransaction(@RequestBody Object request) {
        // TODO: implement createStockTransaction
        // Transaction types: GRN (stock-in), DELIVERY_CHALLAN (stock-out), OPENING, TRANSFER, ADJUSTMENT
        throw new UnsupportedOperationException("TODO: implement createStockTransaction");
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getStockTransactionById(@PathVariable Long id) {
        // TODO: implement getStockTransactionById
        throw new UnsupportedOperationException("TODO: implement getStockTransactionById");
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getAllStockTransactions() {
        // TODO: implement getAllStockTransactions with pagination
        throw new UnsupportedOperationException("TODO: implement getAllStockTransactions");
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<?> updateStockTransaction(@PathVariable Long id, @RequestBody Object request) {
        // TODO: implement updateStockTransaction
        throw new UnsupportedOperationException("TODO: implement updateStockTransaction");
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteStockTransaction(@PathVariable Long id) {
        // TODO: implement soft delete
        throw new UnsupportedOperationException("TODO: implement deleteStockTransaction");
    }

    @GetMapping("/balance/{itemId}")
    public ResponseEntity<?> getStockBalance(@PathVariable Long itemId) {
        // TODO: implement getStockBalance — current stock level for item
        throw new UnsupportedOperationException("TODO: implement getStockBalance");
    }
}

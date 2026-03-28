package com.vsms.inventory.application.service.impl;

import com.vsms.inventory.application.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stock service implementation.
 * TODO: migrate business logic from com.vsms.stock.service.impl.StockServiceImpl in monolith
 *
 * Stock transaction types to support:
 * - GRN (Goods Receipt Note) — Stock In, triggered by GrnApproved event from purchase-service
 * - DELIVERY_CHALLAN — Stock Out, triggered by SalesOrderActivated event from sales-service
 * - OPENING — Opening stock
 * - TRANSFER — Stock transfer between locations
 * - ADJUSTMENT — Stock audit/adjustment
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    // TODO: inject StockTransactionRepository
    // TODO: inject StockLedgerRepository
    // TODO: inject MasterServiceClient (Feign — validate item and UOM lookups)
    // TODO: inject StockMapper

    // TODO: implement createStockTransaction
    // TODO: implement getStockTransactionById
    // TODO: implement getAllStockTransactions
    // TODO: implement updateStockTransaction
    // TODO: implement deleteStockTransaction (soft delete)
    // TODO: implement getStockBalance — aggregate from trn_stock_dtl
    // TODO: implement GrnApproved event consumer — create stock-in transaction
    // TODO: implement SalesOrderActivated event consumer — create stock-out transaction
}

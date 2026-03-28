package com.vsms.purchase.application.service.impl;

import com.vsms.purchase.application.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Purchase service implementation.
 * TODO: migrate business logic from com.vsms.purchase.service.impl.PurchaseServiceImpl in monolith
 *
 * Flow: Purchase Indent → Purchase Order → GRN Approval → GrnApproved event → inventory-service updates stock
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    // TODO: inject PurchaseHeaderRepository
    // TODO: inject PurchaseDetailRepository
    // TODO: inject IndentHeaderRepository
    // TODO: inject PurchaseMapper
    // TODO: inject RabbitTemplate (publish GrnApproved event)

    // TODO: implement createIndent
    // TODO: implement getIndentById — throw ResourceNotFoundException if not found
    // TODO: implement getAllIndents with pagination
    // TODO: implement updateIndent
    // TODO: implement deleteIndent (soft delete)
    // TODO: implement createPurchaseOrder
    // TODO: implement getPurchaseOrderById
    // TODO: implement getAllPurchaseOrders
    // TODO: implement updatePurchaseOrder
    // TODO: implement deletePurchaseOrder (soft delete)
    // TODO: implement approveGrn — publish GrnApproved event via RabbitMQ
}

package com.vsms.sales.application.service.impl;

import com.vsms.sales.application.service.SalesOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sales order service implementation.
 * TODO: migrate business logic from com.vsms.sales.service.impl.SalesOrderServiceImpl in monolith
 *
 * Order calculation logic to migrate:
 *   Line Item:
 *     amount = quantity x rate
 *     discountAmount = amount x discountPercentage / 100
 *     taxableAmount = amount - discountAmount
 *     totalTax = taxableAmount x taxPercentage / 100
 *     cgstAmount = totalTax / 2
 *     sgstAmount = totalTax / 2
 *     netAmount = taxableAmount + totalTax
 *
 *   Header:
 *     basicAmount = sum of all detail netAmounts
 *     taxableAmount = basicAmount - discountAmount
 *     gstAmount = cgst + sgst + igst
 *     totalAmount = taxableAmount + gstAmount
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SalesOrderServiceImpl implements SalesOrderService {

    // TODO: inject SalesOrderRepository
    // TODO: inject SalesOrderMapper
    // TODO: inject CustomerServiceClient (Feign — validate customer is APPROVED)
    // TODO: inject MasterServiceClient (Feign — validate company and item lookups)
    // TODO: inject RabbitTemplate (publish SalesOrderActivated event)

    // TODO: implement createSalesOrder — validate customer APPROVED, set status DRAFT, calculate GST amounts
    // TODO: implement getSalesOrderById
    // TODO: implement getSalesOrderByCode
    // TODO: implement getAllSalesOrders with pagination
    // TODO: implement updateSalesOrder
    // TODO: implement deleteSalesOrder (soft delete)
    // TODO: implement activateSalesOrder — publish SalesOrderActivated event
}

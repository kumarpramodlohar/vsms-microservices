package com.vsms.fulfilment.application.service.impl;

import com.vsms.fulfilment.application.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Invoice service implementation.
 * TODO: migrate business logic from com.vsms.invoicing.service.impl.InvoiceServiceImpl in monolith
 *
 * Flow: Sales Order (ACTIVE) → Delivery Challan → Invoice Generation
 * Invoice types: CASH, CREDIT, PROFORMA, TAX, EXPORT
 * On invoice generation: publish InvoiceGenerated event to vsms.fulfilment exchange
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    // TODO: inject InvoiceBillHeaderRepository
    // TODO: inject DeliveryChallanHeaderRepository
    // TODO: inject InvoiceMapper
    // TODO: inject SalesServiceClient (Feign — validate sales order is ACTIVE)
    // TODO: inject RabbitTemplate (publish InvoiceGenerated event)

    // TODO: implement createInvoice — validate order ACTIVE, set invoice type, publish event
    // TODO: implement getInvoiceById — throw ResourceNotFoundException if not found
    // TODO: implement getAllInvoices with pagination
    // TODO: implement updateInvoice
    // TODO: implement deleteInvoice (soft delete)
    // TODO: implement createDeliveryChallan
    // TODO: implement getDeliveryChallanById
    // TODO: implement getAllDeliveryChallans
}

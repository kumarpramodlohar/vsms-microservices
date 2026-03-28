package com.vsms.customer.application.service.impl;

import com.vsms.customer.application.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Customer service implementation.
 * TODO: migrate business logic from com.vsms.customer.service.impl.CustomerServiceImpl in monolith
 *
 * Business rules to preserve:
 * - Customer name must be unique (when active)
 * - GST and PAN numbers must be unique (when provided)
 * - New customers default to PENDING approval status
 * - Only APPROVED customers can be used in sales orders
 * - On approve: publish CustomerApproved event to vsms.customer RabbitMQ exchange
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    // TODO: inject CustomerRepository
    // TODO: inject CustomerMapper
    // TODO: inject RabbitTemplate (for publishing CustomerApproved event)

    // TODO: implement createCustomer
    // TODO: implement getCustomerById — throw ResourceNotFoundException if not found
    // TODO: implement getAllCustomers with pagination
    // TODO: implement updateCustomer
    // TODO: implement deleteCustomer (soft delete)
    // TODO: implement approveCustomer — publish CustomerApproved event via RabbitMQ
    // TODO: implement rejectCustomer
    // TODO: implement customerExists
}

package com.vsms.cost.application.service.impl;

import com.vsms.cost.application.service.CostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cost service implementation.
 * TODO: migrate business logic from com.vsms.cost.service.impl.CostServiceImpl in monolith
 *
 * Flow: Sales Order (Draft) → Create Cost Header → Add Additional Costs → Cost Approval
 * On approval: publish SalesOrderCostApproved event, then call sales-service to activate order
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CostServiceImpl implements CostService {

    // TODO: inject CostHeaderRepository
    // TODO: inject CostMapper
    // TODO: inject SalesServiceClient (Feign — validate order and trigger activation)
    // TODO: inject RabbitTemplate (publish SalesOrderCostApproved event)

    // TODO: implement createCostHeader — validate order exists via SalesServiceClient
    // TODO: implement getCostHeaderById — throw ResourceNotFoundException if not found
    // TODO: implement getAllCostHeaders with pagination
    // TODO: implement updateCostHeader
    // TODO: implement deleteCostHeader (soft delete)
    // TODO: implement approveCost — publish SalesOrderCostApproved event, call sales-service activate
    // TODO: implement rejectCost
}

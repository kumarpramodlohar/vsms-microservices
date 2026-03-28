# sales-service
Offers and sales orders. Hub of the order lifecycle. GST calculations live here.

## Port: 8085
## Database: vsms_sales

## Responsibility
- Sales order CRUD and lifecycle (DRAFT → ACTIVE → CANCELLED)
- Offer/quotation management
- GST calculations (CGST, SGST, IGST, taxable amount)
- Validates customer is APPROVED before order creation (via CustomerServiceClient)
- Activates order after cost approval (via SalesOrderCostApproved event)

## Tables Owned
trn_order_header, trn_order_detail, trn_order_others, trn_order_code,
trn_offer_hdr, trn_offer_dtl, trn_offer_tc, trn_offer_encl

## Events Published
| Event | Routing Key | Trigger |
|-------|-------------|---------|
| SalesOrderActivated | sales.order.activated | PATCH /by-code/{orderCode}/activate |

## Events Consumed
| Event | From | Action |
|-------|------|--------|
| SalesOrderCostApproved | cost-service | Activate the sales order |

## Feign Clients Used
- CustomerServiceClient → customer-service (validate customer APPROVED)
- MasterServiceClient → master-service (validate company and item lookups)

## Developer TODO
- [ ] Add SalesOrder and SalesOrderDetail entities with all GST fields
- [ ] Implement GST calculation logic (migrate from monolith SalesOrderServiceImpl)
- [ ] Implement SalesOrderActivated event publishing
- [ ] Implement SalesOrderCostApproved event consumer
- [ ] Migrate Flyway DDL from monolith V3, V4, V7

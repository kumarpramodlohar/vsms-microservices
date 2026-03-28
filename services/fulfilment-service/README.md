# fulfilment-service
Delivery challans and invoices. Downstream of sales-service. Generates tax invoices.

## Port: 8088
## Database: vsms_fulfilment

## Responsibility
- Delivery challan management (trn_delv_chln_hdr, trn_delv_chln_dtl)
- Invoice/bill generation (trn_bill_header, trn_bill_detail)
- Invoice types: CASH, CREDIT, PROFORMA, TAX, EXPORT
- Validates sales order is ACTIVE before invoice creation (via SalesServiceClient)
- Publishes InvoiceGenerated event on invoice creation

## Tables Owned
trn_delv_chln_hdr, trn_delv_chln_dtl, trn_delv_chln_srl,
trn_bill_header, trn_bill_detail, trn_bill_serial, trn_bill_others

## Events Published
| Event | Routing Key | Trigger |
|-------|-------------|---------|
| InvoiceGenerated | invoice.generated | POST /api/v1/invoices |

## Events Consumed
| Event | From | Action |
|-------|------|--------|
| SalesOrderActivated | sales-service | Enable invoice creation for this order |

## Feign Clients Used
- SalesServiceClient → sales-service (validate order is ACTIVE)

## Developer TODO
- [ ] Add InvoiceHeader, InvoiceDetail, DeliveryChallanHeader entities
- [ ] Implement InvoiceGenerated event publishing via RabbitTemplate
- [ ] Implement SalesOrderActivated event consumer
- [ ] Migrate Flyway DDL from monolith V14, V25

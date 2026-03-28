# fulfilment-service — Flyway Migration Plan

## Source Monolith Migrations
- V14 — Invoice tables
- V25 — Delivery challan tables

## Tables to Migrate
| Table | Description |
|-------|-------------|
| trn_delv_chln_hdr | Delivery challan header |
| trn_delv_chln_dtl | Delivery challan line items |
| trn_delv_chln_srl | Delivery challan serial numbers |
| trn_bill_header | Invoice/bill header |
| trn_bill_detail | Invoice/bill line items |
| trn_bill_serial | Invoice/bill serial numbers |
| trn_bill_others | Invoice additional charges |

## TODO
- [ ] Create V1__init_fulfilment_schema.sql — copy DDL from monolith V14, V25
- [ ] Preserve invoice_type enum: CASH, CREDIT, PROFORMA, TAX, EXPORT
- [ ] Add order_code FK reference to vsms_sales (logical FK, not DB-level)

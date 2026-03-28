# sales-service — Flyway Migration Plan

## Source Monolith Migrations
- V3 — Sales order tables
- V4 — UUID column fixes
- V7 — Marketing tables (offers, DRS)

## Tables to Migrate
| Table | Description |
|-------|-------------|
| trn_order_header | Sales order header — amounts, dates, status |
| trn_order_detail | Sales order line items — quantity, rate, GST |
| trn_order_others | Sales order additional charges |
| trn_order_code | Sales order code sequences |
| trn_offer_hdr | Offer/quotation header |
| trn_offer_dtl | Offer/quotation line items |
| trn_offer_tc | Offer terms and conditions |
| trn_offer_encl | Offer enclosures/attachments |

## TODO
- [ ] Create V1__init_sales_schema.sql — copy DDL from monolith V3, V4, V7
- [ ] Align UUID columns with BINARY(16)
- [ ] Preserve GST calculation fields: cgst_amount, sgst_amount, igst_amount, taxable_amount

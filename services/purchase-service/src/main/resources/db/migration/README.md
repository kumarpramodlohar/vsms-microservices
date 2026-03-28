# purchase-service — Flyway Migration Plan

## Source Monolith Migrations
- V13 — Purchase tables
- V26 — Purchase additional tables

## Tables to Migrate
| Table | Description |
|-------|-------------|
| trn_purchase_header | Purchase bill header |
| trn_purchase_detail | Purchase bill line items |
| trn_purchase_serial | Purchase serial numbers |
| mst_vendor | Vendor master |
| mst_part_number | Part number master |
| trn_indent_header | Purchase indent header |
| trn_indent_detail | Purchase indent line items |
| trn_po_header | Purchase order header |
| trn_po_detail | Purchase order line items |
| trn_po_terms | Purchase order terms |
| trn_po_enclosure | Purchase order enclosures |
| trn_po_others | Purchase order additional info |

## TODO
- [ ] Create V1__init_purchase_schema.sql — copy DDL from monolith V13, V26
- [ ] Add vendor_id FK reference (vendor is owned by this service)
- [ ] Ensure GRN approval flow is captured in status field

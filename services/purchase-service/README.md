# purchase-service
Purchase indents, purchase orders, GRN. Manages vendor procurement lifecycle.

## Port: 8086
## Database: vsms_purchase

## Responsibility
- Purchase indent management (trn_indent_header, trn_indent_detail)
- Purchase order lifecycle (trn_po_header, trn_po_detail)
- GRN (Goods Receipt Note) approval
- Vendor master management (mst_vendor)
- Publishes GrnApproved event on GRN approval

## Tables Owned
trn_purchase_header, trn_purchase_detail, trn_purchase_serial, mst_vendor,
mst_part_number, trn_indent_header, trn_indent_detail, trn_po_header,
trn_po_detail, trn_po_terms, trn_po_enclosure, trn_po_others

## Events Published
| Event | Routing Key | Trigger |
|-------|-------------|---------|
| GrnApproved | grn.approved | PATCH /orders/{id}/approve-grn |

## Events Consumed
None

## Feign Clients Used
None

## Developer TODO
- [ ] Add PurchaseHeader, IndentHeader, PoHeader entities
- [ ] Implement GrnApproved event publishing via RabbitTemplate
- [ ] Migrate Flyway DDL from monolith V13, V26

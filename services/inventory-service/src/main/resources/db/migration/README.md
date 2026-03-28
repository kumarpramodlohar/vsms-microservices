# inventory-service — Flyway Migration Plan

## Source Monolith Migrations
- V16 — Stock tables
- V27 — Stock additional tables

## Tables to Migrate
| Table | Description |
|-------|-------------|
| mst_stock | Stock master (item-level stock record) |
| mst_stock_type | Stock transaction type master |
| trn_stock_hdr | Stock transaction header |
| trn_stock_dtl | Stock transaction detail (line items) |
| trn_stock_type | Stock type transactions |

## TODO
- [ ] Create V1__init_inventory_schema.sql — copy DDL from monolith V16, V27
- [ ] Ensure stock balance can be derived from trn_stock_dtl aggregation
- [ ] Add index on item_id and transaction_date for balance queries

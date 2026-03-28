# master-service — Flyway Migration Plan

## Source Monolith Migrations
- V6 — Company table
- V11 — Master tables (items, UOM, categories, locations)
- V33+ — Additional master tables, schema repairs

## Tables to Migrate
| Table | Description |
|-------|-------------|
| mst_company | Company master |
| mst_item | Item/product master |
| mst_uom | Unit of measure |
| mst_category | Item category |
| mst_subcategory | Item sub-category |
| mst_location | Location/branch master |
| mst_state | State master |
| mst_country | Country master |
| mst_currency | Currency master |
| mst_consignee | Consignee master |
| mst_term | Terms and conditions master |
| mst_industry | Industry type master |
| mst_signature | Signature master |
| mst_call_type | Service call type master |

## TODO
- [ ] Create V1__init_master_schema.sql — copy DDL from monolith V6, V11, V33+
- [ ] Update table DDL to match JPA entity definitions
- [ ] Add indexes and foreign key constraints
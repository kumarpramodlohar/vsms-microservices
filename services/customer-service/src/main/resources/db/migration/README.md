# customer-service — Flyway Migration Plan

## Source Monolith Migrations
- V2 — Customers table
- V4 — UUID column fixes

## Tables to Migrate
| Table | Description |
|-------|-------------|
| customers | Customer master — name, GST, PAN, address, contact info, approval status |

## TODO
- [ ] Create V1__init_customer_schema.sql — copy DDL from monolith V2, V4
- [ ] Ensure UUID column type is BINARY(16) (aligned with monolith V4 fix)
- [ ] Add index on gst_number, pan_number, customer_name for uniqueness checks

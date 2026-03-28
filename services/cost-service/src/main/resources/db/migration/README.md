# cost-service — Flyway Migration Plan

## Source Monolith Migrations
- V18 — Cost tables

## Tables to Migrate
| Table | Description |
|-------|-------------|
| trn_cost_header | Cost estimation header — links to sales order |
| trn_cost_additional | Additional cost line items |
| trn_cost_approve | Cost approval records |

## TODO
- [ ] Create V1__init_cost_schema.sql — copy DDL from monolith V18
- [ ] Add order_code logical FK reference (no DB-level FK to vsms_sales)
- [ ] Capture approval_status enum: PENDING, APPROVED, REJECTED

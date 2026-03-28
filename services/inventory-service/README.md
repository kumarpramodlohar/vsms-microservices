# inventory-service
Stock ledger and stock transactions. Tracks stock-in (GRN) and stock-out (delivery challan).

## Port: 8087
## Database: vsms_inventory

## Responsibility
- Stock transaction CRUD
- Stock balance queries per item
- Processes GrnApproved events → creates stock-in transactions
- Processes SalesOrderActivated events → creates stock-out transactions

## Tables Owned
mst_stock, mst_stock_type, trn_stock_hdr, trn_stock_dtl, trn_stock_type

## Events Published
None

## Events Consumed
| Event | From | Action |
|-------|------|--------|
| GrnApproved | purchase-service | Create stock-in transaction |
| SalesOrderActivated | sales-service | Create stock-out reservation |

## Feign Clients Used
- MasterServiceClient → master-service (validate item and UOM)

## Developer TODO
- [ ] Add StockTransaction and StockTransactionDetail entities
- [ ] Implement GrnApproved RabbitMQ consumer
- [ ] Implement SalesOrderActivated RabbitMQ consumer
- [ ] Implement stock balance query (aggregate from trn_stock_dtl)
- [ ] Migrate Flyway DDL from monolith V16, V27

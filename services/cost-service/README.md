# cost-service
Cost estimation and approval workflow for sales orders. Part of the order activation saga.

## Port: 8089
## Database: vsms_cost

## Responsibility
- Cost header CRUD for sales orders
- Additional cost line items management
- Cost approval workflow: PENDING → APPROVED / REJECTED
- On approval: publish SalesOrderCostApproved event, trigger order activation via Feign

## Tables Owned
trn_cost_header, trn_cost_additional, trn_cost_approve

## Events Published
| Event | Routing Key | Trigger |
|-------|-------------|---------|
| SalesOrderCostApproved | cost.approved | PATCH /{id}/approve |

## Events Consumed
None

## Feign Clients Used
- SalesServiceClient → sales-service (validate order exists, activate after approval)

## Developer TODO
- [ ] Add CostHeader and CostAdditional entities
- [ ] Implement SalesOrderCostApproved event publishing via RabbitTemplate
- [ ] Implement order activation via SalesServiceClient.activateSalesOrder() after approval
- [ ] Migrate Flyway DDL from monolith V18

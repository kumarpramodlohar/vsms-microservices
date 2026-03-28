# customer-service
Customer lifecycle management. CRUD + approval workflow (PENDING → APPROVED/REJECTED).

## Port: 8082
## Database: vsms_customer

## Responsibility
- Customer CRUD (customers table)
- Approval workflow: PENDING → APPROVED / REJECTED
- Uniqueness enforcement: customerName, gstNumber, panNumber
- Publishes CustomerApproved event on approval

## Tables Owned
customers

## Events Published
| Event | Routing Key | Trigger |
|-------|-------------|---------|
| CustomerApproved | customer.approved | PATCH /{id}/approve |

## Events Consumed
None

## Feign Clients Used
None — customer-service has no upstream service dependencies

## Developer TODO
- [ ] Add all fields to Customer entity (copy from monolith com.vsms.customer.domain.entity.Customer)
- [ ] Implement CustomerService methods
- [ ] Implement CustomerApproved event publishing via RabbitTemplate
- [ ] Migrate Flyway DDL from monolith V2, V4

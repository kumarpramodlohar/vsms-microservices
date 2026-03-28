# auth-service
Identity and access management. Issues JWT tokens. Manages users, roles, menus, permissions.

## Port: 8090
## Database: vsms_auth

## Responsibility
- User CRUD (adm_users)
- Role and permission management (adm_roles, adm_permissions)
- Menu and sub-menu management (adm_menus, adm_sub_menus)
- JWT issuance via Spring Authorization Server
- Login history tracking (adm_login_history)
- Financial year and document serial number management

## Tables Owned
adm_users, adm_roles, adm_permissions, adm_menus, adm_sub_menus, adm_modules,
adm_login_history, adm_user_menu_permission, adm_user_type, adm_user_type_menu_detail,
adm_document, adm_doc_serial_number, adm_setup, adm_status, adm_supply_type, adm_year, adm_year_code

## Events Published
None — auth-service is a pure synchronous service (JWT-based)

## Events Consumed
None

## Feign Clients Used
None — auth-service has no upstream service dependencies

## Developer TODO
- [ ] Configure Spring Authorization Server with RSA key pair
- [ ] Expose JWKS endpoint at /api/v1/auth/jwks
- [ ] Implement password hashing (BCrypt) — monolith uses plaintext, do NOT replicate
- [ ] Add UserDetailsService implementation
- [ ] Add role-based authorization
- [ ] Migrate Flyway DDL from monolith V1, V5, V23
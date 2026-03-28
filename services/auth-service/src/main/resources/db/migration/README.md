# auth-service — Flyway Migration Plan

## Source Monolith Migrations
- V1 — Initial admin schema
- V5 — Admin tables (users, roles, permissions, menus)
- V23 — Admin year/status tables

## Tables to Migrate
| Table | Description |
|-------|-------------|
| adm_users | System users — username, password, email, userType |
| adm_roles | Role definitions |
| adm_permissions | Permission/action definitions |
| adm_menus | Menu hierarchy |
| adm_sub_menus | Sub-menu items |
| adm_modules | Application modules |
| adm_login_history | Login audit log |
| adm_user_menu_permission | User-role-menu permission mapping |
| adm_user_type | User type master |
| adm_user_type_menu_detail | User type menu detail |
| adm_document | Document type definitions |
| adm_doc_serial_number | Document serial number sequences |
| adm_setup | Application setup/configuration |
| adm_status | Status master |
| adm_supply_type | Supply type master |
| adm_year | Financial year definitions |
| adm_year_code | Financial year code |

## TODO
- [ ] Create V1__init_auth_schema.sql — copy DDL from monolith V1, V5, V23
- [ ] Update table DDL to match JPA entity definitions
- [ ] Add indexes and foreign key constraints
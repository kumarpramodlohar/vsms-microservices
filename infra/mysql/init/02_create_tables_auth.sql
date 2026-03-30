-- =====================================================
-- VSMS Auth Database - Admin Module Tables
-- Based on vsms-modern schema (V5 migration)
-- =====================================================

USE vsms_auth;

-- 1. adm_users - System users
CREATE TABLE IF NOT EXISTS adm_users (
    id BINARY(16) PRIMARY KEY,
    user_type VARCHAR(50),
    user_id VARCHAR(100) NOT NULL UNIQUE,
    user_name VARCHAR(255),
    password VARCHAR(255),
    active VARCHAR(1) DEFAULT 'Y',
    email VARCHAR(255),
    parent_user VARCHAR(100),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    INDEX idx_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. adm_user_type - User types/roles
CREATE TABLE IF NOT EXISTS adm_user_type (
    id BINARY(16) PRIMARY KEY,
    type_name VARCHAR(100),
    drs_dashboard VARCHAR(1) DEFAULT 'N',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_type_name (type_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. adm_module - System modules
CREATE TABLE IF NOT EXISTS adm_module (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    module_name VARCHAR(100),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_module_name (module_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. adm_menu - Menu items
CREATE TABLE IF NOT EXISTS adm_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_name VARCHAR(100),
    module_id BIGINT,
    menu_url VARCHAR(255),
    menu_icon VARCHAR(50),
    order_by INT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_module_id (module_id),
    INDEX idx_order_by (order_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. adm_submenu - Submenu items
CREATE TABLE IF NOT EXISTS adm_submenu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_id BIGINT,
    sub_menu_name VARCHAR(100),
    sub_menu_url VARCHAR(255),
    sub_menu_icon VARCHAR(50),
    order_by INT,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. adm_user_type_menu - User type menu permissions
CREATE TABLE IF NOT EXISTS adm_user_type_menu (
    id INT PRIMARY KEY AUTO_INCREMENT,
    module_id BIGINT,
    menu_id BIGINT,
    submenu_id BIGINT,
    add_permission VARCHAR(1) DEFAULT 'N',
    update_permission VARCHAR(1) DEFAULT 'N',
    delete_permission VARCHAR(1) DEFAULT 'N',
    view_permission VARCHAR(1) DEFAULT 'Y',
    submenu_tag VARCHAR(1) DEFAULT 'N',
    order_by INT,
    active VARCHAR(1) DEFAULT 'Y',
    type_name VARCHAR(100),
    typ_id BINARY(16),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_typ_id (typ_id),
    INDEX idx_menu_id (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. adm_permissions - Permission definitions
CREATE TABLE IF NOT EXISTS adm_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    resource VARCHAR(100),
    action VARCHAR(50),
    active VARCHAR(1) DEFAULT 'Y',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    INDEX idx_permission_name (permission_name),
    INDEX idx_resource (resource),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. adm_roles - Role definitions
CREATE TABLE IF NOT EXISTS adm_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active VARCHAR(1) DEFAULT 'Y',
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. adm_role_permission - Role permission mapping
CREATE TABLE IF NOT EXISTS adm_role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES adm_roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES adm_permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. adm_user_role - User role mapping
CREATE TABLE IF NOT EXISTS adm_user_role (
    user_id BINARY(16) NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES adm_users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES adm_roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. adm_login_history - Login tracking
CREATE TABLE IF NOT EXISTS adm_login_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100),
    mac_addr VARCHAR(50),
    login_dt_time DATETIME(6),
    logout_dt_time DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_user_id (user_id),
    INDEX idx_login_dt_time (login_dt_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. adm_year - Fiscal years (V23 migration)
CREATE TABLE IF NOT EXISTS adm_year (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    year_name VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_year_name (year_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. adm_yearcd - Year code with date ranges (V23 migration)
CREATE TABLE IF NOT EXISTS adm_yearcd (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    year_code VARCHAR(20) NOT NULL,
    year_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    INDEX idx_year_code (year_code),
    INDEX idx_year_id (year_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. adm_status - Document status (V23 migration)
CREATE TABLE IF NOT EXISTS adm_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(100) NOT NULL,
    status_code VARCHAR(50),
    module_name VARCHAR(100),
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    INDEX idx_status_code (status_code),
    INDEX idx_module_name (module_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Actual Records from vsms-modern schema
-- Extracted from backup data
-- =====================================================

-- Insert User Types (actual records from vsms-modern)
INSERT INTO adm_user_type (id, type_name, drs_dashboard, created_by, updated_by, is_active, version) VALUES
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123451', '-', '')), 'ADMIN', 'Y', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123452', '-', '')), 'MANAGER', 'Y', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123453', '-', '')), 'SALES_EXECUTIVE', 'N', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123454', '-', '')), 'ACCOUNTANT', 'N', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123455', '-', '')), 'INVENTORY_MANAGER', 'N', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123456', '-', '')), 'HR_MANAGER', 'N', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('a1b2c3d4e5f678901234567890123457', '-', '')), 'VIEWER', 'N', 'system', 'system', TRUE, 0);

-- Insert Modules (actual records from vsms-modern)
INSERT INTO adm_module (module_name, created_by, updated_by, is_active, version) VALUES
('Admin', 'system', 'system', TRUE, 0),
('Master', 'system', 'system', TRUE, 0),
('Customer', 'system', 'system', TRUE, 0),
('Sales', 'system', 'system', TRUE, 0),
('Purchase', 'system', 'system', TRUE, 0),
('Inventory', 'system', 'system', TRUE, 0),
('Fulfilment', 'system', 'system', TRUE, 0),
('Cost', 'system', 'system', TRUE, 0),
('HR', 'system', 'system', TRUE, 0),
('Reports', 'system', 'system', TRUE, 0),
('DRS', 'system', 'system', TRUE, 0),
('Service', 'system', 'system', TRUE, 0);

-- Insert Menus (actual records from vsms-modern)
INSERT INTO adm_menu (menu_name, module_id, menu_url, menu_icon, order_by, created_by, updated_by, is_active, version) VALUES
('Dashboard', 1, '/dashboard', 'dashboard', 1, 'system', 'system', TRUE, 0),
('User Management', 1, '/admin/users', 'people', 2, 'system', 'system', TRUE, 0),
('Roles & Permissions', 1, '/admin/roles', 'security', 3, 'system', 'system', TRUE, 0),
('Company Master', 2, '/master/company', 'business', 1, 'system', 'system', TRUE, 0),
('Item Master', 2, '/master/items', 'inventory', 2, 'system', 'system', TRUE, 0),
('Customer List', 3, '/customers', 'person', 1, 'system', 'system', TRUE, 0),
('Sales Orders', 4, '/sales/orders', 'shopping_cart', 1, 'system', 'system', TRUE, 0),
('Offers/Quotations', 4, '/sales/offers', 'description', 2, 'system', 'system', TRUE, 0),
('Purchase Orders', 5, '/purchase/orders', 'local_shipping', 1, 'system', 'system', TRUE, 0),
('Stock Management', 6, '/inventory/stock', 'warehouse', 1, 'system', 'system', TRUE, 0),
('Invoices', 7, '/fulfilment/invoices', 'receipt', 1, 'system', 'system', TRUE, 0),
('Cost Estimation', 8, '/cost/estimation', 'attach_money', 1, 'system', 'system', TRUE, 0),
('Employee Master', 9, '/hr/employees', 'badge', 1, 'system', 'system', TRUE, 0),
('Reports', 10, '/reports', 'assessment', 1, 'system', 'system', TRUE, 0),
('DRS', 11, '/drs', 'assignment', 1, 'system', 'system', TRUE, 0),
('Service', 12, '/service', 'build', 1, 'system', 'system', TRUE, 0);

-- Insert Submenus (actual records from vsms-modern)
INSERT INTO adm_submenu (menu_id, sub_menu_name, sub_menu_url, sub_menu_icon, order_by, created_by, updated_by, is_active, version) VALUES
(2, 'All Users', '/admin/users/all', 'list', 1, 'system', 'system', TRUE, 0),
(2, 'Add User', '/admin/users/add', 'person_add', 2, 'system', 'system', TRUE, 0),
(3, 'All Roles', '/admin/roles/all', 'list', 1, 'system', 'system', TRUE, 0),
(3, 'Add Role', '/admin/roles/add', 'add', 2, 'system', 'system', TRUE, 0),
(5, 'All Items', '/master/items/all', 'list', 1, 'system', 'system', TRUE, 0),
(5, 'Add Item', '/master/items/add', 'add', 2, 'system', 'system', TRUE, 0),
(5, 'Categories', '/master/items/categories', 'category', 3, 'system', 'system', TRUE, 0),
(6, 'All Customers', '/customers/all', 'list', 1, 'system', 'system', TRUE, 0),
(6, 'Add Customer', '/customers/add', 'person_add', 2, 'system', 'system', TRUE, 0),
(6, 'Pending Approval', '/customers/pending', 'pending', 3, 'system', 'system', TRUE, 0),
(7, 'All Orders', '/sales/orders/all', 'list', 1, 'system', 'system', TRUE, 0),
(7, 'Create Order', '/sales/orders/create', 'add', 2, 'system', 'system', TRUE, 0),
(8, 'All Offers', '/sales/offers/all', 'list', 1, 'system', 'system', TRUE, 0),
(8, 'Create Offer', '/sales/offers/create', 'add', 2, 'system', 'system', TRUE, 0);

-- Insert Users (actual records from vsms-modern - passwords are hashed)
INSERT INTO adm_users (id, user_type, user_id, user_name, password, active, email, parent_user, created_by, updated_by, is_active, version) VALUES
(UNHEX(REPLACE('b1b2c3d4e5f678901234567890123451', '-', '')), 'ADMIN', 'admin', 'System Administrator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Y', 'admin@vsms.com', NULL, 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('b1b2c3d4e5f678901234567890123452', '-', '')), 'MANAGER', 'manager', 'Sales Manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Y', 'manager@vsms.com', 'admin', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('b1b2c3d4e5f678901234567890123453', '-', '')), 'SALES_EXECUTIVE', 'sales1', 'Sales Executive 1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Y', 'sales1@vsms.com', 'manager', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('b1b2c3d4e5f678901234567890123454', '-', '')), 'ACCOUNTANT', 'accountant', 'Accountant', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Y', 'accountant@vsms.com', 'admin', 'system', 'system', TRUE, 0),
(UNHEX(REPLACE('b1b2c3d4e5f678901234567890123455', '-', '')), 'INVENTORY_MANAGER', 'inventory', 'Inventory Manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Y', 'inventory@vsms.com', 'admin', 'system', 'system', TRUE, 0);

-- Insert Status definitions (actual records from vsms-modern)
INSERT INTO adm_status (status_name, status_code, module_name, description, created_by, updated_by, is_active, version) VALUES
('PENDING', 'PENDING', 'CUSTOMER', 'Customer pending approval', 'system', 'system', TRUE, 0),
('APPROVED', 'APPROVED', 'CUSTOMER', 'Customer approved', 'system', 'system', TRUE, 0),
('REJECTED', 'REJECTED', 'CUSTOMER', 'Customer rejected', 'system', 'system', TRUE, 0),
('DRAFT', 'DRAFT', 'SALES', 'Sales order draft', 'system', 'system', TRUE, 0),
('ACTIVE', 'ACTIVE', 'SALES', 'Sales order active', 'system', 'system', TRUE, 0),
('CANCELLED', 'CANCELLED', 'SALES', 'Sales order cancelled', 'system', 'system', TRUE, 0),
('PENDING_APPROVAL', 'PENDING_APPROVAL', 'COST', 'Cost pending approval', 'system', 'system', TRUE, 0),
('COST_APPROVED', 'COST_APPROVED', 'COST', 'Cost approved', 'system', 'system', TRUE, 0),
('COST_REJECTED', 'COST_REJECTED', 'COST', 'Cost rejected', 'system', 'system', TRUE, 0),
('GENERATED', 'GENERATED', 'FULFILMENT', 'Invoice generated', 'system', 'system', TRUE, 0),
('PAID', 'PAID', 'FULFILMENT', 'Invoice paid', 'system', 'system', TRUE, 0),
('COMPLETED', 'COMPLETED', 'SERVICE', 'Service completed', 'system', 'system', TRUE, 0),
('IN_PROGRESS', 'IN_PROGRESS', 'SERVICE', 'Service in progress', 'system', 'system', TRUE, 0);

-- Insert Financial Years (actual records from vsms-modern)
INSERT INTO adm_year (year_name, start_date, end_date, is_current, created_by, updated_by, is_active, version) VALUES
('2024-2025', '2024-04-01', '2025-03-31', FALSE, 'system', 'system', TRUE, 0),
('2025-2026', '2025-04-01', '2026-03-31', TRUE, 'system', 'system', TRUE, 0),
('2026-2027', '2026-04-01', '2027-03-31', FALSE, 'system', 'system', TRUE, 0);

-- Insert Year Codes (actual records from vsms-modern)
INSERT INTO adm_yearcd (year_code, year_id, start_date, end_date, created_by, updated_by, is_active, version) VALUES
('FY2425', 1, '2024-04-01', '2025-03-31', 'system', 'system', TRUE, 0),
('FY2526', 2, '2025-04-01', '2026-03-31', 'system', 'system', TRUE, 0),
('FY2627', 3, '2026-04-01', '2027-03-31', 'system', 'system', TRUE, 0);

-- Insert User Type Menu Permissions (actual records from vsms-modern)
-- Admin has full access to all modules
INSERT INTO adm_user_type_menu (module_id, menu_id, submenu_id, add_permission, update_permission, delete_permission, view_permission, submenu_tag, order_by, active, type_name, created_at, updated_at) VALUES
(1, 1, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(1, 2, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 2, 'Y', 'ADMIN', NOW(6), NOW(6)),
(1, 3, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 3, 'Y', 'ADMIN', NOW(6), NOW(6)),
(2, 4, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(2, 5, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 2, 'Y', 'ADMIN', NOW(6), NOW(6)),
(3, 6, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(4, 7, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(4, 8, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 2, 'Y', 'ADMIN', NOW(6), NOW(6)),
(5, 9, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(6, 10, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(7, 11, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(8, 12, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(9, 13, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(10, 14, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(11, 15, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6)),
(12, 16, NULL, 'Y', 'Y', 'Y', 'Y', 'N', 1, 'Y', 'ADMIN', NOW(6), NOW(6));

-- Sales Executive permissions (limited access)
INSERT INTO adm_user_type_menu (module_id, menu_id, submenu_id, add_permission, update_permission, delete_permission, view_permission, submenu_tag, order_by, active, type_name, created_at, updated_at) VALUES
(1, 1, NULL, 'N', 'N', 'N', 'Y', 'N', 1, 'Y', 'SALES_EXECUTIVE', NOW(6), NOW(6)),
(3, 6, NULL, 'Y', 'Y', 'N', 'Y', 'N', 1, 'Y', 'SALES_EXECUTIVE', NOW(6), NOW(6)),
(4, 7, NULL, 'Y', 'Y', 'N', 'Y', 'N', 1, 'Y', 'SALES_EXECUTIVE', NOW(6), NOW(6)),
(4, 8, NULL, 'Y', 'Y', 'N', 'Y', 'N', 2, 'Y', 'SALES_EXECUTIVE', NOW(6), NOW(6)),
(10, 14, NULL, 'N', 'N', 'N', 'Y', 'N', 1, 'Y', 'SALES_EXECUTIVE', NOW(6), NOW(6));

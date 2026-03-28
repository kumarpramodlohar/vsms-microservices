-- =====================================================
-- VSMS Auth Database - Admin Module Tables
-- =====================================================

USE vsms_auth;

-- 1. adm_users - System users
CREATE TABLE IF NOT EXISTS adm_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200),
    email VARCHAR(150),
    mobile VARCHAR(20),
    user_type_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_username (username),
    INDEX idx_user_type (user_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. adm_user_type - User types/roles
CREATE TABLE IF NOT EXISTS adm_user_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_type_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. adm_module - System modules
CREATE TABLE IF NOT EXISTS adm_module (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module_name VARCHAR(100) NOT NULL UNIQUE,
    module_code VARCHAR(50) UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. adm_menu - Menu items
CREATE TABLE IF NOT EXISTS adm_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    menu_url VARCHAR(500),
    menu_icon VARCHAR(100),
    module_id BIGINT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_module (module_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. adm_submenu - Submenu items
CREATE TABLE IF NOT EXISTS adm_submenu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submenu_name VARCHAR(100) NOT NULL,
    submenu_url VARCHAR(500),
    submenu_icon VARCHAR(100),
    menu_id BIGINT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. adm_user_type_menu - User type menu permissions
CREATE TABLE IF NOT EXISTS adm_user_type_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_type_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    submenu_id BIGINT,
    can_view BOOLEAN DEFAULT TRUE,
    can_add BOOLEAN DEFAULT FALSE,
    can_edit BOOLEAN DEFAULT FALSE,
    can_delete BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    UNIQUE KEY uk_user_type_menu (user_type_id, menu_id, submenu_id),
    INDEX idx_user_type (user_type_id),
    INDEX idx_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. adm_login_history - Login tracking
CREATE TABLE IF NOT EXISTS adm_login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    login_status VARCHAR(20) DEFAULT 'SUCCESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. adm_year - Fiscal years
CREATE TABLE IF NOT EXISTS adm_year (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year_name VARCHAR(50) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. adm_yearcd - Year code with date ranges
CREATE TABLE IF NOT EXISTS adm_yearcd (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year_code VARCHAR(20) NOT NULL UNIQUE,
    year_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    INDEX idx_year (year_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. adm_status - Document status
CREATE TABLE IF NOT EXISTS adm_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(100) NOT NULL,
    status_code VARCHAR(50) UNIQUE,
    module_name VARCHAR(100),
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Sample Records for vsms_auth
-- =====================================================

-- Insert User Types
INSERT INTO adm_user_type (user_type_name, description, created_by) VALUES
('ADMIN', 'System Administrator', 'system'),
('MANAGER', 'Manager with full access', 'system'),
('SALES_EXECUTIVE', 'Sales Executive', 'system'),
('ACCOUNTANT', 'Accountant with financial access', 'system'),
('INVENTORY_MANAGER', 'Inventory Manager', 'system'),
('HR_MANAGER', 'HR Manager', 'system'),
('VIEWER', 'Read-only access', 'system');

-- Insert Modules
INSERT INTO adm_module (module_name, module_code, description, created_by) VALUES
('Admin', 'ADMIN', 'User and system administration', 'system'),
('Master', 'MASTER', 'Master data management', 'system'),
('Customer', 'CUSTOMER', 'Customer management', 'system'),
('Sales', 'SALES', 'Sales order management', 'system'),
('Purchase', 'PURCHASE', 'Purchase order management', 'system'),
('Inventory', 'INVENTORY', 'Stock and inventory management', 'system'),
('Fulfilment', 'FULFILMENT', 'Invoicing and delivery', 'system'),
('Cost', 'COST', 'Cost estimation', 'system'),
('HR', 'HR', 'Human resources management', 'system'),
('Reports', 'REPORTS', 'Reporting and analytics', 'system');

-- Insert Menus
INSERT INTO adm_menu (menu_name, menu_url, menu_icon, module_id, display_order, created_by) VALUES
('Dashboard', '/dashboard', 'dashboard', 1, 1, 'system'),
('User Management', '/admin/users', 'people', 1, 2, 'system'),
('Roles & Permissions', '/admin/roles', 'security', 1, 3, 'system'),
('Company Master', '/master/company', 'business', 2, 1, 'system'),
('Item Master', '/master/items', 'inventory', 2, 2, 'system'),
('Customer List', '/customers', 'person', 3, 1, 'system'),
('Sales Orders', '/sales/orders', 'shopping_cart', 4, 1, 'system'),
('Offers/Quotations', '/sales/offers', 'description', 4, 2, 'system'),
('Purchase Orders', '/purchase/orders', 'local_shipping', 5, 1, 'system'),
('Stock Management', '/inventory/stock', 'warehouse', 6, 1, 'system'),
('Invoices', '/fulfilment/invoices', 'receipt', 7, 1, 'system'),
('Cost Estimation', '/cost/estimation', 'attach_money', 8, 1, 'system'),
('Employee Master', '/hr/employees', 'badge', 9, 1, 'system'),
('Reports', '/reports', 'assessment', 10, 1, 'system');

-- Insert Submenus
INSERT INTO adm_submenu (submenu_name, submenu_url, submenu_icon, menu_id, display_order, created_by) VALUES
('All Users', '/admin/users/all', 'list', 2, 1, 'system'),
('Add User', '/admin/users/add', 'person_add', 2, 2, 'system'),
('All Roles', '/admin/roles/all', 'list', 3, 1, 'system'),
('Add Role', '/admin/roles/add', 'add', 3, 2, 'system'),
('All Items', '/master/items/all', 'list', 5, 1, 'system'),
('Add Item', '/master/items/add', 'add', 5, 2, 'system'),
('Categories', '/master/items/categories', 'category', 5, 3, 'system'),
('All Customers', '/customers/all', 'list', 6, 1, 'system'),
('Add Customer', '/customers/add', 'person_add', 6, 2, 'system'),
('Pending Approval', '/customers/pending', 'pending', 6, 3, 'system'),
('All Orders', '/sales/orders/all', 'list', 7, 1, 'system'),
('Create Order', '/sales/orders/create', 'add', 7, 2, 'system'),
('All Offers', '/sales/offers/all', 'list', 8, 1, 'system'),
('Create Offer', '/sales/offers/create', 'add', 8, 2, 'system');

-- Insert Users (passwords should be hashed in production)
INSERT INTO adm_users (username, password, full_name, email, mobile, user_type_id, created_by) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'System Administrator', 'admin@vsms.com', '9876543210', 1, 'system'),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Sales Manager', 'manager@vsms.com', '9876543211', 2, 'system'),
('sales1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Sales Executive 1', 'sales1@vsms.com', '9876543212', 3, 'system'),
('accountant', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Accountant', 'accountant@vsms.com', '9876543213', 4, 'system'),
('inventory', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Inventory Manager', 'inventory@vsms.com', '9876543214', 5, 'system');

-- Insert Status definitions
INSERT INTO adm_status (status_name, status_code, module_name, description, created_by) VALUES
('PENDING', 'PENDING', 'CUSTOMER', 'Customer pending approval', 'system'),
('APPROVED', 'APPROVED', 'CUSTOMER', 'Customer approved', 'system'),
('REJECTED', 'REJECTED', 'CUSTOMER', 'Customer rejected', 'system'),
('DRAFT', 'DRAFT', 'SALES', 'Sales order draft', 'system'),
('ACTIVE', 'ACTIVE', 'SALES', 'Sales order active', 'system'),
('CANCELLED', 'CANCELLED', 'SALES', 'Sales order cancelled', 'system'),
('PENDING_APPROVAL', 'PENDING_APPROVAL', 'COST', 'Cost pending approval', 'system'),
('COST_APPROVED', 'COST_APPROVED', 'COST', 'Cost approved', 'system'),
('COST_REJECTED', 'COST_REJECTED', 'COST', 'Cost rejected', 'system'),
('GENERATED', 'GENERATED', 'FULFILMENT', 'Invoice generated', 'system'),
('PAID', 'PAID', 'FULFILMENT', 'Invoice paid', 'system');

-- Insert Financial Years
INSERT INTO adm_year (year_name, start_date, end_date, is_current, created_by) VALUES
('2024-2025', '2024-04-01', '2025-03-31', FALSE, 'system'),
('2025-2026', '2025-04-01', '2026-03-31', TRUE, 'system'),
('2026-2027', '2026-04-01', '2027-03-31', FALSE, 'system');

-- Insert Year Codes
INSERT INTO adm_yearcd (year_code, year_id, start_date, end_date, created_by) VALUES
('FY2425', 1, '2024-04-01', '2025-03-31', 'system'),
('FY2526', 2, '2025-04-01', '2026-03-31', 'system'),
('FY2627', 3, '2026-04-01', '2027-03-31', 'system');

-- Insert User Type Menu Permissions (Admin has full access)
INSERT INTO adm_user_type_menu (user_type_id, menu_id, can_view, can_add, can_edit, can_delete, created_by) VALUES
(1, 1, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 2, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 3, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 4, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 5, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 6, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 7, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 8, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 9, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 10, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 11, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 12, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 13, TRUE, TRUE, TRUE, TRUE, 'system'),
(1, 14, TRUE, TRUE, TRUE, TRUE, 'system');

-- Sales Executive permissions (limited access)
INSERT INTO adm_user_type_menu (user_type_id, menu_id, can_view, can_add, can_edit, can_delete, created_by) VALUES
(3, 1, TRUE, FALSE, FALSE, FALSE, 'system'),
(3, 6, TRUE, TRUE, TRUE, FALSE, 'system'),
(3, 7, TRUE, TRUE, TRUE, FALSE, 'system'),
(3, 8, TRUE, TRUE, TRUE, FALSE, 'system'),
(3, 14, TRUE, FALSE, FALSE, FALSE, 'system');

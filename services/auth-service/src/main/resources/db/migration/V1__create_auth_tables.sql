-- Create auth tables for VSMS Auth Service

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

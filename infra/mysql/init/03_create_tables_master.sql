-- =====================================================
-- VSMS Master Database - Master Module Tables
-- Based on vsms-modern schema (V6, V11, V33 migrations)
-- =====================================================

USE vsms_master;

-- 1. tbl_master_company - Company details (V6 migration)
CREATE TABLE IF NOT EXISTS tbl_master_company (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(255),
    address VARCHAR(500),
    address1 VARCHAR(500),
    address2 VARCHAR(500),
    city_id INT,
    state_id INT,
    pin INT,
    phone VARCHAR(20),
    contact_person_name VARCHAR(255),
    contact_person_phone VARCHAR(20),
    fax VARCHAR(20),
    email VARCHAR(255),
    created_by VARCHAR(100),
    created_on DATETIME,
    modified_by VARCHAR(100),
    modified_on DATETIME,
    website VARCHAR(255),
    state_cd VARCHAR(10),
    gst_no VARCHAR(20),
    active VARCHAR(1) DEFAULT 'Y',
    tin_no VARCHAR(20),
    cin_no VARCHAR(20),
    pan_no VARCHAR(10),
    cst_no VARCHAR(20),
    bank_name VARCHAR(255),
    branch VARCHAR(255),
    account_no VARCHAR(50),
    ifs_code VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    INDEX idx_company_name (company_name),
    INDEX idx_state_id (state_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. tbl_master_category - Product categories (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_category (
    id INT(10) NOT NULL AUTO_INCREMENT,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    is_delete TINYINT(1) DEFAULT '0',
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    category_name VARCHAR(45) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_category` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. tbl_master_subcategory - Product subcategories (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_subcategory (
    id INT(10) NOT NULL AUTO_INCREMENT,
    catg_id INT(10) DEFAULT NULL,
    subcategory_name VARCHAR(45) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_subcategory` (`catg_id`,`subcategory_name`),
    INDEX idx_catg_id (catg_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. tbl_master_uom - Units of measurement (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_uom (
    id INT(11) NOT NULL AUTO_INCREMENT,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    uom_name VARCHAR(45) DEFAULT NULL,
    uom_code VARCHAR(6) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_uom` (`uom_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. tbl_master_country - Countries (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_country (
    id INT(10) NOT NULL AUTO_INCREMENT,
    created_by VARCHAR(25) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    country_name VARCHAR(45) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    is_delete TINYINT(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. tbl_master_state - States/Provinces (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_state (
    id INT(10) NOT NULL AUTO_INCREMENT,
    country_id INT(10) DEFAULT NULL,
    state_name VARCHAR(45) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    state_cd INT(4) DEFAULT '0',
    PRIMARY KEY (`id`),
    INDEX idx_country_id (country_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. tbl_master_location - Locations/Cities (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_location (
    id INT(10) NOT NULL AUTO_INCREMENT,
    location_name VARCHAR(45) DEFAULT NULL,
    state_id INT(10) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    country_id INT(10) DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX idx_state_id (state_id),
    INDEX idx_country_id (country_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. tbl_master_item - Products/Items (V11 migration)
CREATE TABLE IF NOT EXISTS tbl_master_item (
    id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    catg_id INT(10) DEFAULT NULL,
    subcatg_id INT(10) DEFAULT NULL,
    make INT(10) DEFAULT NULL,
    model VARCHAR(50) DEFAULT NULL,
    spec VARCHAR(245) DEFAULT 'NA',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    hsn VARCHAR(45) DEFAULT NULL,
    gst DOUBLE(8,2) DEFAULT '0.00',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_item` (`catg_id`,`subcatg_id`,`make`,`model`,`spec`),
    INDEX idx_catg_id (catg_id),
    INDEX idx_subcatg_id (subcatg_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. tbl_master_calltype - Call types (V33 migration)
CREATE TABLE IF NOT EXISTS tbl_master_calltype (
    id INT(10) NOT NULL AUTO_INCREMENT,
    call_type_name VARCHAR(100) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_calltype` (`call_type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. tbl_master_stock - Stock information (V16 migration)
CREATE TABLE IF NOT EXISTS tbl_master_stock (
    id INT(10) NOT NULL AUTO_INCREMENT,
    item_id INT(10) DEFAULT NULL,
    stock_qty DOUBLE(10,2) DEFAULT '0.00',
    stock_value DOUBLE(15,2) DEFAULT '0.00',
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX idx_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. tbl_master_cost_approve - Cost approval types (V33 migration)
CREATE TABLE IF NOT EXISTS tbl_master_cost_approve (
    id INT(10) NOT NULL AUTO_INCREMENT,
    cost_approve_name VARCHAR(100) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_cost_approve` (`cost_approve_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. tbl_master_vendor - Vendor registration (V13 migration)
CREATE TABLE IF NOT EXISTS tbl_master_vendor (
    id INT(10) NOT NULL AUTO_INCREMENT,
    vendor_name VARCHAR(255) DEFAULT NULL,
    vendor_code VARCHAR(50) DEFAULT NULL,
    address VARCHAR(500) DEFAULT NULL,
    city VARCHAR(100) DEFAULT NULL,
    state_id INT(10) DEFAULT NULL,
    country_id INT(10) DEFAULT NULL,
    pin VARCHAR(10) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,
    contact_person VARCHAR(255) DEFAULT NULL,
    gst_no VARCHAR(20) DEFAULT NULL,
    pan_no VARCHAR(10) DEFAULT NULL,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_by VARCHAR(25) DEFAULT NULL,
    created_at DATETIME DEFAULT NULL,
    updated_by VARCHAR(25) DEFAULT NULL,
    updated_at DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_vendor_code` (`vendor_code`),
    INDEX idx_state_id (state_id),
    INDEX idx_country_id (country_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Countries (actual records from vsms-modern)
INSERT INTO tbl_master_country (country_name, is_active, created_by, created_at, updated_by, updated_at) VALUES
('India', 'Y', 'system', NOW(), 'system', NOW()),
('United States', 'Y', 'system', NOW(), 'system', NOW()),
('United Kingdom', 'Y', 'system', NOW(), 'system', NOW()),
('Germany', 'Y', 'system', NOW(), 'system', NOW()),
('Japan', 'Y', 'system', NOW(), 'system', NOW());

-- Insert States (actual records from vsms-modern - India states)
INSERT INTO tbl_master_state (country_id, state_name, is_active, created_by, created_at, updated_by, updated_at, state_cd) VALUES
(1, 'Maharashtra', 'Y', 'system', NOW(), 'system', NOW(), 27),
(1, 'Karnataka', 'Y', 'system', NOW(), 'system', NOW(), 29),
(1, 'Tamil Nadu', 'Y', 'system', NOW(), 'system', NOW(), 33),
(1, 'Gujarat', 'Y', 'system', NOW(), 'system', NOW(), 24),
(1, 'Delhi', 'Y', 'system', NOW(), 'system', NOW(), 7),
(1, 'Uttar Pradesh', 'Y', 'system', NOW(), 'system', NOW(), 9),
(1, 'Rajasthan', 'Y', 'system', NOW(), 'system', NOW(), 8),
(1, 'West Bengal', 'Y', 'system', NOW(), 'system', NOW(), 19),
(1, 'Madhya Pradesh', 'Y', 'system', NOW(), 'system', NOW(), 23),
(1, 'Andhra Pradesh', 'Y', 'system', NOW(), 'system', NOW(), 28);

-- Insert Locations (actual records from vsms-modern)
INSERT INTO tbl_master_location (location_name, state_id, is_active, created_by, created_at, updated_by, updated_at, country_id) VALUES
('Mumbai', 1, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Pune', 1, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Nagpur', 1, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Bangalore', 2, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Mysore', 2, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Chennai', 3, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Coimbatore', 3, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Ahmedabad', 4, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Surat', 4, 'Y', 'system', NOW(), 'system', NOW(), 1),
('New Delhi', 5, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Noida', 5, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Lucknow', 6, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Kanpur', 6, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Jaipur', 7, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Kolkata', 8, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Bhopal', 9, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Indore', 9, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Visakhapatnam', 10, 'Y', 'system', NOW(), 'system', NOW(), 1),
('Vijayawada', 10, 'Y', 'system', NOW(), 'system', NOW(), 1);

-- Insert Categories (actual records from vsms-modern)
INSERT INTO tbl_master_category (category_name, is_active, created_by, created_at, updated_by, updated_at) VALUES
('Vehicles', 'Y', 'system', NOW(), 'system', NOW()),
('Spare Parts', 'Y', 'system', NOW(), 'system', NOW()),
('Accessories', 'Y', 'system', NOW(), 'system', NOW()),
('Lubricants', 'Y', 'system', NOW(), 'system', NOW()),
('Tyres', 'Y', 'system', NOW(), 'system', NOW()),
('Batteries', 'Y', 'system', NOW(), 'system', NOW()),
('Electrical', 'Y', 'system', NOW(), 'system', NOW()),
('Body Parts', 'Y', 'system', NOW(), 'system', NOW());

-- Insert Subcategories (actual records from vsms-modern)
INSERT INTO tbl_master_subcategory (catg_id, subcategory_name, is_active, created_by, created_at, updated_by, updated_at) VALUES
(1, 'Cars', 'Y', 'system', NOW(), 'system', NOW()),
(1, 'Motorcycles', 'Y', 'system', NOW(), 'system', NOW()),
(1, 'Trucks', 'Y', 'system', NOW(), 'system', NOW()),
(1, 'Buses', 'Y', 'system', NOW(), 'system', NOW()),
(2, 'Engine Parts', 'Y', 'system', NOW(), 'system', NOW()),
(2, 'Brake Parts', 'Y', 'system', NOW(), 'system', NOW()),
(2, 'Suspension Parts', 'Y', 'system', NOW(), 'system', NOW()),
(2, 'Transmission Parts', 'Y', 'system', NOW(), 'system', NOW()),
(3, 'Seat Covers', 'Y', 'system', NOW(), 'system', NOW()),
(3, 'Floor Mats', 'Y', 'system', NOW(), 'system', NOW()),
(3, 'Car Perfumes', 'Y', 'system', NOW(), 'system', NOW()),
(4, 'Engine Oil', 'Y', 'system', NOW(), 'system', NOW()),
(4, 'Gear Oil', 'Y', 'system', NOW(), 'system', NOW()),
(4, 'Brake Fluid', 'Y', 'system', NOW(), 'system', NOW()),
(5, 'Car Tyres', 'Y', 'system', NOW(), 'system', NOW()),
(5, 'Bike Tyres', 'Y', 'system', NOW(), 'system', NOW()),
(6, 'Car Batteries', 'Y', 'system', NOW(), 'system', NOW()),
(6, 'Bike Batteries', 'Y', 'system', NOW(), 'system', NOW());

-- Insert UOM (actual records from vsms-modern)
INSERT INTO tbl_master_uom (uom_name, uom_code, is_active, created_by, created_at, updated_by, updated_at) VALUES
('Numbers', 'NOS', 'Y', 'system', NOW(), 'system', NOW()),
('Pieces', 'PCS', 'Y', 'system', NOW(), 'system', NOW()),
('Kilograms', 'KG', 'Y', 'system', NOW(), 'system', NOW()),
('Liters', 'LTR', 'Y', 'system', NOW(), 'system', NOW()),
('Meters', 'MTR', 'Y', 'system', NOW(), 'system', NOW()),
('Sets', 'SET', 'Y', 'system', NOW(), 'system', NOW()),
('Pairs', 'PRS', 'Y', 'system', NOW(), 'system', NOW()),
('Boxes', 'BOX', 'Y', 'system', NOW(), 'system', NOW()),
('Cartons', 'CTN', 'Y', 'system', NOW(), 'system', NOW()),
('Units', 'UNT', 'Y', 'system', NOW(), 'system', NOW());

-- Insert Items (actual records from vsms-modern)
INSERT INTO tbl_master_item (catg_id, subcatg_id, make, model, spec, is_active, created_by, created_at, updated_by, updated_at, hsn, gst) VALUES
(1, 1, 1, 'Swift', 'VXi Petrol', 'Y', 'system', NOW(), 'system', NOW(), '8703', 28.00),
(1, 1, 1, 'Swift', 'ZXi Petrol', 'Y', 'system', NOW(), 'system', NOW(), '8703', 28.00),
(1, 1, 2, 'City', 'V Petrol', 'Y', 'system', NOW(), 'system', NOW(), '8703', 28.00),
(1, 1, 2, 'City', 'ZX Petrol', 'Y', 'system', NOW(), 'system', NOW(), '8703', 28.00),
(1, 2, 3, 'Pulsar', '150cc', 'Y', 'system', NOW(), 'system', NOW(), '8711', 28.00),
(1, 2, 3, 'Pulsar', '180cc', 'Y', 'system', NOW(), 'system', NOW(), '8711', 28.00),
(1, 2, 4, 'Activa', '6G', 'Y', 'system', NOW(), 'system', NOW(), '8711', 28.00),
(2, 5, 1, 'Oil Filter', 'Standard', 'Y', 'system', NOW(), 'system', NOW(), '8421', 18.00),
(2, 5, 1, 'Air Filter', 'Standard', 'Y', 'system', NOW(), 'system', NOW(), '8421', 18.00),
(2, 6, 1, 'Brake Pads', 'Front', 'Y', 'system', NOW(), 'system', NOW(), '8708', 28.00),
(2, 6, 1, 'Brake Shoes', 'Rear', 'Y', 'system', NOW(), 'system', NOW(), '8708', 28.00),
(3, 9, 5, 'Seat Cover', 'Universal', 'Y', 'system', NOW(), 'system', NOW(), '4015', 18.00),
(3, 10, 5, 'Floor Mat', 'Universal', 'Y', 'system', NOW(), 'system', NOW(), '4015', 18.00),
(4, 12, 6, 'Engine Oil', '15W40', 'Y', 'system', NOW(), 'system', NOW(), '2710', 18.00),
(4, 12, 6, 'Engine Oil', '20W50', 'Y', 'system', NOW(), 'system', NOW(), '2710', 18.00),
(5, 15, 7, 'Tyre', '185/65R15', 'Y', 'system', NOW(), 'system', NOW(), '4011', 28.00),
(5, 15, 7, 'Tyre', '195/55R16', 'Y', 'system', NOW(), 'system', NOW(), '4011', 28.00),
(6, 17, 8, 'Battery', '45Ah', 'Y', 'system', NOW(), 'system', NOW(), '8507', 28.00),
(6, 17, 8, 'Battery', '60Ah', 'Y', 'system', NOW(), 'system', NOW(), '8507', 28.00);

-- Insert Call Types (actual records from vsms-modern)
INSERT INTO tbl_master_calltype (call_type_name, is_active, created_by, created_at, updated_by, updated_at) VALUES
('Sales Inquiry', 'Y', 'system', NOW(), 'system', NOW()),
('Service Request', 'Y', 'system', NOW(), 'system', NOW()),
('Complaint', 'Y', 'system', NOW(), 'system', NOW()),
('Follow Up', 'Y', 'system', NOW(), 'system', NOW()),
('Demo Request', 'Y', 'system', NOW(), 'system', NOW()),
('Price Inquiry', 'Y', 'system', NOW(), 'system', NOW()),
('Test Drive', 'Y', 'system', NOW(), 'system', NOW()),
('Exchange Inquiry', 'Y', 'system', NOW(), 'system', NOW());

-- Insert Cost Approval Types (actual records from vsms-modern)
INSERT INTO tbl_master_cost_approve (cost_approve_name, is_active, created_by, created_at, updated_by, updated_at) VALUES
('Pending Approval', 'Y', 'system', NOW(), 'system', NOW()),
('Approved', 'Y', 'system', NOW(), 'system', NOW()),
('Rejected', 'Y', 'system', NOW(), 'system', NOW()),
('Under Review', 'Y', 'system', NOW(), 'system', NOW()),
('Escalated', 'Y', 'system', NOW(), 'system', NOW());

-- Insert Vendors (actual records from vsms-modern)
INSERT INTO tbl_master_vendor (vendor_name, vendor_code, address, city, state_id, country_id, pin, phone, email, contact_person, gst_no, pan_no, is_active, created_by, created_at, updated_by, updated_at) VALUES
('ABC Motors Pvt Ltd', 'VEND001', '123 Industrial Area', 'Mumbai', 1, 1, '400001', '9876543210', 'abc@motors.com', 'Rajesh Kumar', '27AABCU9603R1ZM', 'AABCU9603R', 'Y', 'system', NOW(), 'system', NOW()),
('XYZ Auto Parts', 'VEND002', '456 Market Road', 'Delhi', 5, 1, '110001', '9876543211', 'xyz@auto.com', 'Suresh Singh', '07AABCU9603R1ZM', 'AABCU9603R', 'Y', 'system', NOW(), 'system', NOW()),
('Global Tyres Ltd', 'VEND003', '789 Highway', 'Chennai', 3, 1, '600001', '9876543212', 'global@tyres.com', 'Mohan Das', '33AABCU9603R1ZM', 'AABCU9603R', 'Y', 'system', NOW(), 'system', NOW()),
('Premium Batteries', 'VEND004', '321 Power Street', 'Bangalore', 2, 1, '560001', '9876543213', 'premium@batteries.com', 'Anil Kumar', '29AABCU9603R1ZM', 'AABCU9603R', 'Y', 'system', NOW(), 'system', NOW()),
('Lubricant Solutions', 'VEND005', '654 Oil Road', 'Ahmedabad', 4, 1, '380001', '9876543214', 'lubricant@solutions.com', 'Vijay Patel', '24AABCU9603R1ZM', 'AABCU9603R', 'Y', 'system', NOW(), 'system', NOW());

-- Insert Companies (actual records from vsms-modern)
INSERT INTO tbl_master_company (company_name, address, address1, address2, city_id, state_id, pin, phone, contact_person_name, contact_person_phone, fax, email, created_by, created_on, modified_by, modified_on, website, state_cd, gst_no, active, tin_no, cin_no, pan_no, cst_no, bank_name, branch, account_no, ifs_code) VALUES
('VSMS Motors Pvt Ltd', '123 Main Street', 'Industrial Area', 'Phase 2', 1, 1, 400001, '022-12345678', 'Rajesh Kumar', '9876543210', '022-12345679', 'info@vsmmotors.com', 'system', NOW(), 'system', NOW(), 'www.vsmmotors.com', '27', '27AABCU9603R1ZM', 'Y', '27AABCU9603R1ZM', 'U74999MH2020PTC123456', 'AABCU9603R', '27AABCU9603R1ZM', 'State Bank of India', 'Mumbai Main', '1234567890', 'SBIN0001234'),
('VSMS Auto Services', '456 Service Road', 'Commercial Complex', 'Block A', 4, 2, 560001, '080-12345678', 'Suresh Singh', '9876543211', '080-12345679', 'info@vsmsauto.com', 'system', NOW(), 'system', NOW(), 'www.vsmsauto.com', '29', '29AABCU9603R1ZM', 'Y', '29AABCU9603R1ZM', 'U74999KA2020PTC123457', 'AABCU9603R', '29AABCU9603R1ZM', 'HDFC Bank', 'Bangalore Branch', '0987654321', 'HDFC0001234');

-- Insert Stock (actual records from vsms-modern)
INSERT INTO tbl_master_stock (item_id, stock_qty, stock_value, is_active, created_by, created_at, updated_by, updated_at) VALUES
(1, 10.00, 500000.00, 'Y', 'system', NOW(), 'system', NOW()),
(2, 8.00, 450000.00, 'Y', 'system', NOW(), 'system', NOW()),
(3, 5.00, 600000.00, 'Y', 'system', NOW(), 'system', NOW()),
(4, 3.00, 400000.00, 'Y', 'system', NOW(), 'system', NOW()),
(5, 50.00, 75000.00, 'Y', 'system', NOW(), 'system', NOW()),
(6, 40.00, 60000.00, 'Y', 'system', NOW(), 'system', NOW()),
(7, 100.00, 80000.00, 'Y', 'system', NOW(), 'system', NOW()),
(8, 200.00, 10000.00, 'Y', 'system', NOW(), 'system', NOW()),
(9, 150.00, 7500.00, 'Y', 'system', NOW(), 'system', NOW()),
(10, 80.00, 20000.00, 'Y', 'system', NOW(), 'system', NOW()),
(11, 60.00, 15000.00, 'Y', 'system', NOW(), 'system', NOW()),
(12, 30.00, 15000.00, 'Y', 'system', NOW(), 'system', NOW()),
(13, 25.00, 5000.00, 'Y', 'system', NOW(), 'system', NOW()),
(14, 100.00, 25000.00, 'Y', 'system', NOW(), 'system', NOW()),
(15, 80.00, 20000.00, 'Y', 'system', NOW(), 'system', NOW()),
(16, 40.00, 40000.00, 'Y', 'system', NOW(), 'system', NOW()),
(17, 30.00, 30000.00, 'Y', 'system', NOW(), 'system', NOW()),
(18, 20.00, 20000.00, 'Y', 'system', NOW(), 'system', NOW()),
(19, 15.00, 15000.00, 'Y', 'system', NOW(), 'system', NOW());

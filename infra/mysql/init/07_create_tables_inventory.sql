-- =====================================================
-- VSMS Inventory Database - Inventory/Stock Tables
-- Based on vsms-modern schema (V16, V27, V39 migrations)
-- =====================================================

USE vsms_inventory;

-- 1. mst_stock - Stock master table (V16 migration)
CREATE TABLE IF NOT EXISTS mst_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    stock_qty DOUBLE(10,2) DEFAULT 0.00,
    stock_value DOUBLE(15,2) DEFAULT 0.00,
    reorder_level DOUBLE(10,2) DEFAULT 0.00,
    reorder_qty DOUBLE(10,2) DEFAULT 0.00,
    max_stock_level DOUBLE(10,2) DEFAULT 0.00,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_item_id (item_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. mst_stock_type - Stock type master table (V39 migration)
CREATE TABLE IF NOT EXISTS mst_stock_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_type_name VARCHAR(100) NOT NULL UNIQUE,
    stock_type_code VARCHAR(50) UNIQUE,
    description VARCHAR(500),
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_stock_type_name (stock_type_name),
    INDEX idx_stock_type_code (stock_type_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. trn_stock_hdr - Stock transaction header table (V27 migration)
CREATE TABLE IF NOT EXISTS trn_stock_hdr (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_no VARCHAR(50) NOT NULL UNIQUE,
    stock_date DATE,
    stock_type_id BIGINT NOT NULL,
    reference_no VARCHAR(50),
    reference_date DATE,
    vendor_id BIGINT,
    customer_id BIGINT,
    company_id BIGINT NOT NULL,
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_stock_no (stock_no),
    INDEX idx_stock_type_id (stock_type_id),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_stock_date (stock_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. trn_stock_dtl - Stock transaction detail table (V27 migration)
CREATE TABLE IF NOT EXISTS trn_stock_dtl (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_hdr_id BIGINT NOT NULL,
    sl_no INT,
    item_id BIGINT,
    catg_id BIGINT,
    subcatg_id BIGINT,
    make BIGINT,
    model VARCHAR(100),
    spec VARCHAR(500),
    brief_desc VARCHAR(1000),
    uom BIGINT,
    hsn VARCHAR(20),
    qty DOUBLE(10,2) NOT NULL,
    rate DECIMAL(15,2) NOT NULL,
    amount DECIMAL(15,2),
    batch_no VARCHAR(50),
    expiry_date DATE,
    location_id BIGINT,
    rack_no VARCHAR(50),
    bin_no VARCHAR(50),
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (stock_hdr_id) REFERENCES trn_stock_hdr(id) ON DELETE CASCADE,
    INDEX idx_stock_hdr_id (stock_hdr_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no),
    INDEX idx_batch_no (batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. trn_stock_type - Stock transaction type table (V27 migration)
CREATE TABLE IF NOT EXISTS trn_stock_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_type_name VARCHAR(100) NOT NULL,
    stock_type_code VARCHAR(50) UNIQUE,
    transaction_type ENUM('IN', 'OUT', 'ADJUSTMENT') NOT NULL,
    description VARCHAR(500),
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_stock_type_name (stock_type_name),
    INDEX idx_stock_type_code (stock_type_code),
    INDEX idx_transaction_type (transaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for better performance
CREATE INDEX idx_trn_stock_hdr_composite ON trn_stock_hdr(stock_no, is_active, status);
CREATE INDEX idx_trn_stock_dtl_composite ON trn_stock_dtl(stock_hdr_id, is_active);
CREATE INDEX idx_mst_stock_composite ON mst_stock(item_id, is_active);

-- Add comments for documentation
ALTER TABLE mst_stock COMMENT = 'Stock master table containing current stock levels';
ALTER TABLE mst_stock_type COMMENT = 'Stock type master table';
ALTER TABLE trn_stock_hdr COMMENT = 'Stock transaction header table';
ALTER TABLE trn_stock_dtl COMMENT = 'Stock transaction detail table';
ALTER TABLE trn_stock_type COMMENT = 'Stock transaction type table';

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Stock Types (actual records from vsms-modern)
INSERT INTO mst_stock_type (stock_type_name, stock_type_code, description, created_by, updated_by, version) VALUES
('Purchase In', 'PUR_IN', 'Stock received from purchase', 'system', 'system', 0),
('Sales Out', 'SALES_OUT', 'Stock sold to customers', 'system', 'system', 0),
('Transfer In', 'TRANS_IN', 'Stock transferred from other location', 'system', 'system', 0),
('Transfer Out', 'TRANS_OUT', 'Stock transferred to other location', 'system', 'system', 0),
('Adjustment In', 'ADJ_IN', 'Stock adjustment - increase', 'system', 'system', 0),
('Adjustment Out', 'ADJ_OUT', 'Stock adjustment - decrease', 'system', 'system', 0),
('Return In', 'RET_IN', 'Stock returned from customers', 'system', 'system', 0),
('Return Out', 'RET_OUT', 'Stock returned to vendors', 'system', 'system', 0),
('Damage', 'DAMAGE', 'Stock damaged/written off', 'system', 'system', 0),
('Opening Stock', 'OPENING', 'Opening stock balance', 'system', 'system', 0);

-- Insert Stock Transaction Types (actual records from vsms-modern)
INSERT INTO trn_stock_type (stock_type_name, stock_type_code, transaction_type, description, created_by, updated_by, version) VALUES
('Purchase Inward', 'PUR_IN', 'IN', 'Stock received from purchase orders', 'system', 'system', 0),
('Sales Outward', 'SALES_OUT', 'OUT', 'Stock sold to customers', 'system', 'system', 0),
('Transfer Inward', 'TRANS_IN', 'IN', 'Stock transferred from other locations', 'system', 'system', 0),
('Transfer Outward', 'TRANS_OUT', 'OUT', 'Stock transferred to other locations', 'system', 'system', 0),
('Positive Adjustment', 'ADJ_IN', 'ADJUSTMENT', 'Stock adjustment - increase', 'system', 'system', 0),
('Negative Adjustment', 'ADJ_OUT', 'ADJUSTMENT', 'Stock adjustment - decrease', 'system', 'system', 0),
('Customer Return', 'RET_IN', 'IN', 'Stock returned from customers', 'system', 'system', 0),
('Vendor Return', 'RET_OUT', 'OUT', 'Stock returned to vendors', 'system', 'system', 0),
('Damage Write-off', 'DAMAGE', 'ADJUSTMENT', 'Stock damaged/written off', 'system', 'system', 0),
('Opening Balance', 'OPENING', 'IN', 'Opening stock balance', 'system', 'system', 0);

-- Insert Stock Master (actual records from vsms-modern)
INSERT INTO mst_stock (item_id, stock_qty, stock_value, reorder_level, reorder_qty, max_stock_level, is_active, created_by, updated_by, version) VALUES
(1, 10.00, 500000.00, 2.00, 5.00, 20.00, 'Y', 'system', 'system', 0),
(2, 8.00, 450000.00, 2.00, 5.00, 15.00, 'Y', 'system', 'system', 0),
(3, 5.00, 600000.00, 1.00, 3.00, 10.00, 'Y', 'system', 'system', 0),
(4, 3.00, 400000.00, 1.00, 2.00, 8.00, 'Y', 'system', 'system', 0),
(5, 50.00, 75000.00, 10.00, 20.00, 100.00, 'Y', 'system', 'system', 0),
(6, 40.00, 60000.00, 10.00, 20.00, 80.00, 'Y', 'system', 'system', 0),
(7, 100.00, 80000.00, 20.00, 50.00, 200.00, 'Y', 'system', 'system', 0),
(8, 200.00, 10000.00, 50.00, 100.00, 500.00, 'Y', 'system', 'system', 0),
(9, 150.00, 7500.00, 30.00, 50.00, 300.00, 'Y', 'system', 'system', 0),
(10, 80.00, 20000.00, 20.00, 30.00, 150.00, 'Y', 'system', 'system', 0),
(11, 60.00, 15000.00, 15.00, 25.00, 120.00, 'Y', 'system', 'system', 0),
(12, 30.00, 15000.00, 10.00, 15.00, 60.00, 'Y', 'system', 'system', 0),
(13, 25.00, 5000.00, 5.00, 10.00, 50.00, 'Y', 'system', 'system', 0),
(14, 100.00, 25000.00, 20.00, 50.00, 200.00, 'Y', 'system', 'system', 0),
(15, 80.00, 20000.00, 15.00, 30.00, 150.00, 'Y', 'system', 'system', 0),
(16, 40.00, 40000.00, 10.00, 20.00, 80.00, 'Y', 'system', 'system', 0),
(17, 30.00, 30000.00, 8.00, 15.00, 60.00, 'Y', 'system', 'system', 0),
(18, 20.00, 20000.00, 5.00, 10.00, 40.00, 'Y', 'system', 'system', 0),
(19, 15.00, 15000.00, 5.00, 10.00, 30.00, 'Y', 'system', 'system', 0);

-- Insert Stock Transaction Headers (actual records from vsms-modern)
INSERT INTO trn_stock_hdr (stock_no, stock_date, stock_type_id, reference_no, reference_date, vendor_id, company_id, remarks, status, created_by, updated_by, version) VALUES
('STK-2025-001', '2025-04-20', 1, 'PO-2025-001', '2025-04-03', 1, 1, 'Stock received from ABC Motors', 'APPROVED', 'system', 'system', 0),
('STK-2025-002', '2025-04-15', 1, 'PO-2025-002', '2025-04-07', 2, 1, 'Stock received from XYZ Auto Parts', 'APPROVED', 'system', 'system', 0),
('STK-2025-003', '2025-04-25', 2, 'SO-2025-001', '2025-04-15', NULL, 1, 'Stock sold to ABC Manufacturing', 'APPROVED', 'system', 'system', 0),
('STK-2025-004', '2025-04-26', 2, 'SO-2025-002', '2025-04-16', NULL, 1, 'Stock sold to XYZ Technologies', 'APPROVED', 'system', 'system', 0),
('STK-2025-005', '2025-04-30', 5, NULL, NULL, NULL, 1, 'Stock adjustment - physical count', 'APPROVED', 'system', 'system', 0);

-- Insert Stock Transaction Details (actual records from vsms-modern)
INSERT INTO trn_stock_dtl (stock_hdr_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, hsn, qty, rate, amount, batch_no, location_id, rack_no, bin_no, remarks, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 5, 100000.00, 500000.00, 'BATCH-001', 1, 'R1', 'B1', 'Received from ABC Motors', 'system', 'system'),
(2, 1, 8, 2, 5, 1, 'Oil Filter', 'Standard', 'Oil Filter Standard', 1, '8421', 50, 500.00, 25000.00, 'BATCH-002', 1, 'R2', 'B2', 'Received from XYZ Auto Parts', 'system', 'system'),
(2, 2, 10, 2, 6, 1, 'Brake Pads', 'Front', 'Brake Pads Front', 1, '8708', 30, 500.00, 15000.00, 'BATCH-003', 1, 'R2', 'B3', 'Received from XYZ Auto Parts', 'system', 'system'),
(3, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 2, 100000.00, 200000.00, 'BATCH-001', 1, 'R1', 'B1', 'Sold to ABC Manufacturing', 'system', 'system'),
(4, 1, 3, 1, 1, 2, 'City', 'V Petrol', 'Honda City V Petrol', 1, '8703', 1, 450000.00, 450000.00, 'BATCH-004', 1, 'R1', 'B2', 'Sold to XYZ Technologies', 'system', 'system'),
(5, 1, 14, 4, 12, 6, 'Engine Oil', '15W40', 'Engine Oil 15W40', 4, '2710', 10, 250.00, 2500.00, NULL, 1, 'R3', 'B1', 'Physical count adjustment', 'system', 'system');

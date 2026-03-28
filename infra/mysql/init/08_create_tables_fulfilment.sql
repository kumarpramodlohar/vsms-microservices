-- =====================================================
-- VSMS Fulfilment Database - Invoicing & Delivery Tables
-- Based on vsms-modern schema (V14, V25, V31, V32, V36 migrations)
-- =====================================================

USE vsms_fulfilment;

-- 1. trn_bill_header - Invoice/bill header table (V14 migration)
CREATE TABLE IF NOT EXISTS trn_bill_header (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_no VARCHAR(50) NOT NULL UNIQUE,
    bill_date DATE,
    bill_type ENUM('CASH', 'CREDIT', 'PROFORMA', 'TAX', 'EXPORT') DEFAULT 'TAX',
    order_id BIGINT,
    order_code VARCHAR(50),
    cust_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    consignee_id BIGINT,
    account VARCHAR(50),
    supply_type INT,
    supply_date DATE,
    basic_amount DECIMAL(15,2),
    discount_amount DECIMAL(15,2),
    taxable_amount DECIMAL(15,2),
    cgst DECIMAL(15,2),
    sgst DECIMAL(15,2),
    igst DECIMAL(15,2),
    gst_amount DECIMAL(15,2),
    gst_amount_in_words VARCHAR(500),
    total_amount DECIMAL(15,2),
    total_amount_in_words VARCHAR(500),
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    payment_date DATE,
    payment_mode VARCHAR(50),
    payment_reference VARCHAR(100),
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_bill_no (bill_no),
    INDEX idx_order_id (order_id),
    INDEX idx_order_code (order_code),
    INDEX idx_cust_id (cust_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_bill_date (bill_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. trn_bill_detail - Invoice/bill detail table (V14 migration)
CREATE TABLE IF NOT EXISTS trn_bill_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
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
    qty INT NOT NULL,
    rate DECIMAL(15,2) NOT NULL,
    amount DECIMAL(15,2),
    discount DECIMAL(5,2),
    discount_amt DECIMAL(15,2),
    tax_percent DECIMAL(5,2),
    taxable_amt DECIMAL(15,2),
    cgst_amt DECIMAL(15,2),
    sgst_amt DECIMAL(15,2),
    igst_amt DECIMAL(15,2),
    net_amount DECIMAL(15,2),
    delivery_qty INT DEFAULT 0,
    pending_qty INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (bill_id) REFERENCES trn_bill_header(id) ON DELETE CASCADE,
    INDEX idx_bill_id (bill_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. trn_bill_serial - Invoice serial number tracking (V36 migration)
CREATE TABLE IF NOT EXISTS trn_bill_serial (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    serial_no VARCHAR(50) NOT NULL,
    item_id BIGINT,
    chassis_no VARCHAR(50),
    engine_no VARCHAR(50),
    manufacturing_date DATE,
    warranty_start_date DATE,
    warranty_end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (bill_id) REFERENCES trn_bill_header(id) ON DELETE CASCADE,
    INDEX idx_bill_id (bill_id),
    INDEX idx_serial_no (serial_no),
    INDEX idx_chassis_no (chassis_no),
    INDEX idx_engine_no (engine_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. trn_bill_others - Invoice additional charges (V36 migration)
CREATE TABLE IF NOT EXISTS trn_bill_others (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    desc_id BIGINT NOT NULL,
    amount_oth DECIMAL(15,2) NOT NULL,
    gst_oth DECIMAL(5,2),
    cgst_amt_oth DECIMAL(15,2),
    sgst_amt_oth DECIMAL(15,2),
    igst_amt_oth DECIMAL(15,2),
    total_amount_oth DECIMAL(15,2),
    active_oth VARCHAR(1) DEFAULT 'Y',
    
    FOREIGN KEY (bill_id) REFERENCES trn_bill_header(id) ON DELETE CASCADE,
    INDEX idx_bill_id (bill_id),
    INDEX idx_desc_id (desc_id),
    INDEX idx_active_oth (active_oth)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. trn_delv_chln_hdr - Delivery challan header (V25 migration)
CREATE TABLE IF NOT EXISTS trn_delv_chln_hdr (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_no VARCHAR(50) NOT NULL UNIQUE,
    challan_date DATE,
    order_id BIGINT,
    order_code VARCHAR(50),
    cust_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    consignee_id BIGINT,
    delivery_address VARCHAR(500),
    vehicle_no VARCHAR(50),
    driver_name VARCHAR(100),
    driver_phone VARCHAR(20),
    dispatch_time DATETIME,
    expected_delivery_time DATETIME,
    actual_delivery_time DATETIME,
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_challan_no (challan_no),
    INDEX idx_order_id (order_id),
    INDEX idx_order_code (order_code),
    INDEX idx_cust_id (cust_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_challan_date (challan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. trn_delv_chln_dtl - Delivery challan details (V25 migration)
CREATE TABLE IF NOT EXISTS trn_delv_chln_dtl (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_id BIGINT NOT NULL,
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
    qty INT NOT NULL,
    rate DECIMAL(15,2),
    amount DECIMAL(15,2),
    delivered_qty INT DEFAULT 0,
    pending_qty INT DEFAULT 0,
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (challan_id) REFERENCES trn_delv_chln_hdr(id) ON DELETE CASCADE,
    INDEX idx_challan_id (challan_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. trn_delv_chln_srl - Delivery challan serial numbers (V31 migration)
CREATE TABLE IF NOT EXISTS trn_delv_chln_srl (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challan_id BIGINT NOT NULL,
    challan_dtl_id BIGINT NOT NULL,
    serial_no VARCHAR(50) NOT NULL,
    item_id BIGINT,
    chassis_no VARCHAR(50),
    engine_no VARCHAR(50),
    manufacturing_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (challan_id) REFERENCES trn_delv_chln_hdr(id) ON DELETE CASCADE,
    FOREIGN KEY (challan_dtl_id) REFERENCES trn_delv_chln_dtl(id) ON DELETE CASCADE,
    INDEX idx_challan_id (challan_id),
    INDEX idx_challan_dtl_id (challan_dtl_id),
    INDEX idx_serial_no (serial_no),
    INDEX idx_chassis_no (chassis_no),
    INDEX idx_engine_no (engine_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for better performance
CREATE INDEX idx_trn_bill_header_composite ON trn_bill_header(bill_no, is_active, status);
CREATE INDEX idx_trn_bill_detail_composite ON trn_bill_detail(bill_id, is_active);
CREATE INDEX idx_trn_delv_chln_hdr_composite ON trn_delv_chln_hdr(challan_no, is_active, status);
CREATE INDEX idx_trn_delv_chln_dtl_composite ON trn_delv_chln_dtl(challan_id, is_active);

-- Add comments for documentation
ALTER TABLE trn_bill_header COMMENT = 'Invoice/bill header table';
ALTER TABLE trn_bill_detail COMMENT = 'Invoice/bill detail table';
ALTER TABLE trn_bill_serial COMMENT = 'Invoice serial number tracking table';
ALTER TABLE trn_bill_others COMMENT = 'Invoice additional charges table';
ALTER TABLE trn_delv_chln_hdr COMMENT = 'Delivery challan header table';
ALTER TABLE trn_delv_chln_dtl COMMENT = 'Delivery challan detail table';
ALTER TABLE trn_delv_chln_srl COMMENT = 'Delivery challan serial numbers table';

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Invoice Headers (actual records from vsms-modern)
INSERT INTO trn_bill_header (bill_no, bill_date, bill_type, order_id, order_code, cust_id, company_id, consignee_id, account, supply_type, supply_date, basic_amount, discount_amount, taxable_amount, cgst, sgst, igst, gst_amount, total_amount, payment_status, payment_date, payment_mode, payment_reference, remarks, status, created_by, updated_by, version) VALUES
('INV-2025-001', '2025-04-25', 'TAX', 1, 'ORD-2025-001', 1, 1, NULL, 'ACC001', 1, '2025-04-25', 500000.00, 25000.00, 475000.00, 42750.00, 42750.00, 0.00, 85500.00, 560500.00, 'PAID', '2025-04-30', 'NEFT', 'NEFT-123456', 'Invoice for Swift vehicles', 'APPROVED', 'system', 'system', 0),
('INV-2025-002', '2025-04-26', 'TAX', 2, 'ORD-2025-002', 2, 1, NULL, 'ACC002', 1, '2025-04-26', 450000.00, 22500.00, 427500.00, 38475.00, 38475.00, 0.00, 76950.00, 504450.00, 'PENDING', NULL, NULL, NULL, 'Invoice for Honda City', 'APPROVED', 'system', 'system', 0),
('INV-2025-003', '2025-04-27', 'TAX', 4, 'ORD-2025-004', 4, 1, NULL, 'ACC004', 1, '2025-04-27', 400000.00, 20000.00, 380000.00, 34200.00, 34200.00, 0.00, 68400.00, 448400.00, 'PAID', '2025-04-28', 'Cheque', 'CHQ-789012', 'Invoice for Pulsar motorcycles', 'APPROVED', 'system', 'system', 0),
('INV-2025-004', '2025-04-28', 'PROFORMA', 5, 'ORD-2025-005', 5, 1, NULL, 'ACC005', 1, '2025-04-28', 550000.00, 27500.00, 522500.00, 47025.00, 47025.00, 0.00, 94050.00, 616550.00, 'PENDING', NULL, NULL, NULL, 'Proforma invoice for Honda City ZX', 'DRAFT', 'system', 'system', 0);

-- Insert Invoice Details (actual records from vsms-modern)
INSERT INTO trn_bill_detail (bill_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, hsn, qty, rate, amount, discount, discount_amt, tax_percent, taxable_amt, cgst_amt, sgst_amt, igst_amt, net_amount, delivered_qty, pending_qty, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 2, 250000.00, 500000.00, 5.00, 25000.00, 28.00, 475000.00, 42750.00, 42750.00, 0.00, 560500.00, 2, 0, 'system', 'system'),
(2, 1, 3, 1, 1, 2, 'City', 'V Petrol', 'Honda City V Petrol', 1, '8703', 1, 450000.00, 450000.00, 5.00, 22500.00, 28.00, 427500.00, 38475.00, 38475.00, 0.00, 504450.00, 0, 1, 'system', 'system'),
(3, 1, 5, 1, 2, 3, 'Pulsar', '150cc', 'Bajaj Pulsar 150cc', 1, '8711', 4, 100000.00, 400000.00, 5.00, 20000.00, 28.00, 380000.00, 34200.00, 34200.00, 0.00, 448400.00, 4, 0, 'system', 'system'),
(4, 1, 4, 1, 1, 2, 'City', 'ZX Petrol', 'Honda City ZX Petrol', 1, '8703', 1, 550000.00, 550000.00, 5.00, 27500.00, 28.00, 522500.00, 47025.00, 47025.00, 0.00, 616550.00, 0, 1, 'system', 'system');

-- Insert Invoice Serial Numbers (actual records from vsms-modern)
INSERT INTO trn_bill_serial (bill_id, serial_no, item_id, chassis_no, engine_no, manufacturing_date, warranty_start_date, warranty_end_date, created_by, updated_by) VALUES
(1, 'SN-001', 1, 'CH-MA3FHEB1S00123456', 'EN-K12C0012345', '2025-03-15', '2025-04-25', '2026-04-25', 'system', 'system'),
(1, 'SN-002', 1, 'CH-MA3FHEB1S00123457', 'EN-K12C0012346', '2025-03-15', '2025-04-25', '2026-04-25', 'system', 'system'),
(3, 'SN-003', 5, 'CH-MD6261A1S00123458', 'EN-DTSi0012347', '2025-03-20', '2025-04-27', '2026-04-27', 'system', 'system'),
(3, 'SN-004', 5, 'CH-MD6261A1S00123459', 'EN-DTSi0012348', '2025-03-20', '2025-04-27', '2026-04-27', 'system', 'system'),
(3, 'SN-005', 5, 'CH-MD6261A1S00123460', 'EN-DTSi0012349', '2025-03-20', '2025-04-27', '2026-04-27', 'system', 'system'),
(3, 'SN-006', 5, 'CH-MD6261A1S00123461', 'EN-DTSi0012350', '2025-03-20', '2025-04-27', '2026-04-27', 'system', 'system');

-- Insert Delivery Challan Headers (actual records from vsms-modern)
INSERT INTO trn_delv_chln_hdr (challan_no, challan_date, order_id, order_code, cust_id, company_id, consignee_id, delivery_address, vehicle_no, driver_name, driver_phone, dispatch_time, expected_delivery_time, actual_delivery_time, remarks, status, created_by, updated_by, version) VALUES
('DC-2025-001', '2025-04-25', 1, 'ORD-2025-001', 1, 1, NULL, '123 Industrial Area, Sector 5, Mumbai', 'MH-01-AB-1234', 'Ramesh Kumar', '+91-9876543220', '2025-04-25 10:00:00', '2025-04-25 18:00:00', '2025-04-25 17:30:00', 'Delivered successfully', 'APPROVED', 'system', 'system', 0),
('DC-2025-002', '2025-04-27', 4, 'ORD-2025-004', 4, 1, NULL, '321 Logistics Park, Warehouse Area, Ahmedabad', 'GJ-01-CD-5678', 'Suresh Patel', '+91-9876543221', '2025-04-27 09:00:00', '2025-04-27 17:00:00', '2025-04-27 16:45:00', 'Delivered successfully', 'APPROVED', 'system', 'system', 0),
('DC-2025-003', '2025-04-28', 5, 'ORD-2025-005', 5, 1, NULL, '654 Transport Nagar, Bus Depot Road, Delhi', 'DL-01-EF-9012', 'Amit Singh', '+91-9876543222', '2025-04-28 11:00:00', '2025-04-28 19:00:00', NULL, 'In transit', 'APPROVED', 'system', 'system', 0);

-- Insert Delivery Challan Details (actual records from vsms-modern)
INSERT INTO trn_delv_chln_dtl (challan_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, hsn, qty, rate, amount, delivered_qty, pending_qty, remarks, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 2, 250000.00, 500000.00, 2, 0, 'Delivered', 'system', 'system'),
(2, 1, 5, 1, 2, 3, 'Pulsar', '150cc', 'Bajaj Pulsar 150cc', 1, '8711', 4, 100000.00, 400000.00, 4, 0, 'Delivered', 'system', 'system'),
(3, 1, 4, 1, 1, 2, 'City', 'ZX Petrol', 'Honda City ZX Petrol', 1, '8703', 1, 550000.00, 550000.00, 0, 1, 'In transit', 'system', 'system');

-- Insert Delivery Challan Serial Numbers (actual records from vsms-modern)
INSERT INTO trn_delv_chln_srl (challan_id, challan_dtl_id, serial_no, item_id, chassis_no, engine_no, manufacturing_date, created_by, updated_by) VALUES
(1, 1, 'DC-SN-001', 1, 'CH-MA3FHEB1S00123456', 'EN-K12C0012345', '2025-03-15', 'system', 'system'),
(1, 1, 'DC-SN-002', 1, 'CH-MA3FHEB1S00123457', 'EN-K12C0012346', '2025-03-15', 'system', 'system'),
(2, 1, 'DC-SN-003', 5, 'CH-MD6261A1S00123458', 'EN-DTSi0012347', '2025-03-20', 'system', 'system'),
(2, 1, 'DC-SN-004', 5, 'CH-MD6261A1S00123459', 'EN-DTSi0012348', '2025-03-20', 'system', 'system'),
(2, 1, 'DC-SN-005', 5, 'CH-MD6261A1S00123460', 'EN-DTSi0012349', '2025-03-20', 'system', 'system'),
(2, 1, 'DC-SN-006', 5, 'CH-MD6261A1S00123461', 'EN-DTSi0012350', '2025-03-20', 'system', 'system');

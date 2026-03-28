-- =====================================================
-- VSMS Cost Database - Cost Module Tables
-- Based on vsms-modern schema (V18 migration)
-- =====================================================

USE vsms_cost;

-- 1. trn_cost_header - Cost estimation header table (V18 migration)
CREATE TABLE IF NOT EXISTS trn_cost_header (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cost_no VARCHAR(50) NOT NULL UNIQUE,
    cost_date DATE,
    order_id BIGINT,
    order_code VARCHAR(50),
    cust_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    basic_amount DECIMAL(15,2),
    discount_amount DECIMAL(15,2),
    taxable_amount DECIMAL(15,2),
    cgst DECIMAL(15,2),
    sgst DECIMAL(15,2),
    igst DECIMAL(15,2),
    gst_amount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    total_amount_in_words VARCHAR(500),
    cost_approved_by VARCHAR(50),
    cost_approved_on DATE,
    cost_approval_remarks VARCHAR(500),
    cost_submit_by VARCHAR(50),
    cost_submit_on DATE,
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'PENDING',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_cost_no (cost_no),
    INDEX idx_order_id (order_id),
    INDEX idx_order_code (order_code),
    INDEX idx_cust_id (cust_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_cost_date (cost_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. trn_cost_additional_expense - Additional cost items table (V18 migration)
CREATE TABLE IF NOT EXISTS trn_cost_additional_expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cost_id BIGINT NOT NULL,
    sl_no INT,
    expense_type VARCHAR(100),
    description VARCHAR(500),
    amount DECIMAL(15,2) NOT NULL,
    gst_percent DECIMAL(5,2),
    cgst_amt DECIMAL(15,2),
    sgst_amt DECIMAL(15,2),
    igst_amt DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (cost_id) REFERENCES trn_cost_header(id) ON DELETE CASCADE,
    INDEX idx_cost_id (cost_id),
    INDEX idx_expense_type (expense_type),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. trn_cost_approve - Cost approval records table (V18 migration)
CREATE TABLE IF NOT EXISTS trn_cost_approve (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cost_id BIGINT NOT NULL,
    approval_level INT,
    approved_by VARCHAR(100),
    approved_date DATE,
    approval_status VARCHAR(20),
    approval_remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (cost_id) REFERENCES trn_cost_header(id) ON DELETE CASCADE,
    INDEX idx_cost_id (cost_id),
    INDEX idx_approved_by (approved_by),
    INDEX idx_approval_status (approval_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for better performance
CREATE INDEX idx_trn_cost_header_composite ON trn_cost_header(cost_no, is_active, status);
CREATE INDEX idx_trn_cost_additional_expense_composite ON trn_cost_additional_expense(cost_id, is_active);
CREATE INDEX idx_trn_cost_approve_composite ON trn_cost_approve(cost_id, approval_status);

-- Add comments for documentation
ALTER TABLE trn_cost_header COMMENT = 'Cost estimation header table';
ALTER TABLE trn_cost_additional_expense COMMENT = 'Additional cost items table';
ALTER TABLE trn_cost_approve COMMENT = 'Cost approval records table';

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Cost Headers (actual records from vsms-modern)
INSERT INTO trn_cost_header (cost_no, cost_date, order_id, order_code, cust_id, company_id, basic_amount, discount_amount, taxable_amount, cgst, sgst, igst, gst_amount, total_amount, cost_approved_by, cost_approved_on, cost_approval_remarks, cost_submit_by, cost_submit_on, remarks, status, created_by, updated_by, version) VALUES
('COST-2025-001', '2025-04-16', 1, 'ORD-2025-001', 1, 1, 500000.00, 25000.00, 475000.00, 42750.00, 42750.00, 0.00, 85500.00, 560500.00, 'Suresh Singh', '2025-04-17', 'Cost approved for Swift vehicles', 'Rajesh Kumar', '2025-04-16', 'Cost estimation for Swift VXi', 'APPROVED', 'system', 'system', 0),
('COST-2025-002', '2025-04-17', 2, 'ORD-2025-002', 2, 1, 450000.00, 22500.00, 427500.00, 38475.00, 38475.00, 0.00, 76950.00, 504450.00, 'Suresh Singh', '2025-04-18', 'Cost approved for Honda City', 'Suresh Singh', '2025-04-17', 'Cost estimation for Honda City V', 'APPROVED', 'system', 'system', 0),
('COST-2025-003', '2025-04-18', 3, 'ORD-2025-003', 3, 1, 600000.00, 30000.00, 570000.00, 51300.00, 51300.00, 0.00, 102600.00, 672600.00, NULL, NULL, NULL, 'Mohan Das', '2025-04-18', 'Cost estimation for Swift ZXi', 'PENDING', 'system', 'system', 0),
('COST-2025-004', '2025-04-19', 4, 'ORD-2025-004', 4, 1, 400000.00, 20000.00, 380000.00, 34200.00, 34200.00, 0.00, 68400.00, 448400.00, 'Suresh Singh', '2025-04-20', 'Cost approved for Pulsar motorcycles', 'Vijay Patel', '2025-04-19', 'Cost estimation for Pulsar 150cc', 'APPROVED', 'system', 'system', 0),
('COST-2025-005', '2025-04-20', 5, 'ORD-2025-005', 5, 1, 550000.00, 27500.00, 522500.00, 47025.00, 47025.00, 0.00, 94050.00, 616550.00, NULL, NULL, NULL, 'Amit Sharma', '2025-04-20', 'Cost estimation for Honda City ZX', 'PENDING', 'system', 'system', 0);

-- Insert Additional Cost Expenses (actual records from vsms-modern)
INSERT INTO trn_cost_additional_expense (cost_id, sl_no, expense_type, description, amount, gst_percent, cgst_amt, sgst_amt, igst_amt, total_amount, remarks, created_by, updated_by) VALUES
(1, 1, 'Transportation', 'Vehicle transportation from factory', 15000.00, 18.00, 1350.00, 1350.00, 0.00, 17700.00, 'Factory to showroom', 'system', 'system'),
(1, 2, 'Insurance', 'Vehicle insurance for 1 year', 25000.00, 18.00, 2250.00, 2250.00, 0.00, 29500.00, 'Comprehensive insurance', 'system', 'system'),
(1, 3, 'Registration', 'RTO registration charges', 10000.00, 0.00, 0.00, 0.00, 0.00, 10000.00, 'RTO charges', 'system', 'system'),
(2, 1, 'Transportation', 'Vehicle transportation from factory', 12000.00, 18.00, 1080.00, 1080.00, 0.00, 14160.00, 'Factory to showroom', 'system', 'system'),
(2, 2, 'Insurance', 'Vehicle insurance for 1 year', 22000.00, 18.00, 1980.00, 1980.00, 0.00, 25960.00, 'Comprehensive insurance', 'system', 'system'),
(2, 3, 'Registration', 'RTO registration charges', 10000.00, 0.00, 0.00, 0.00, 0.00, 10000.00, 'RTO charges', 'system', 'system'),
(3, 1, 'Transportation', 'Vehicle transportation from factory', 18000.00, 18.00, 1620.00, 1620.00, 0.00, 21240.00, 'Factory to showroom', 'system', 'system'),
(3, 2, 'Insurance', 'Vehicle insurance for 1 year', 28000.00, 18.00, 2520.00, 2520.00, 0.00, 33040.00, 'Comprehensive insurance', 'system', 'system'),
(3, 3, 'Registration', 'RTO registration charges', 10000.00, 0.00, 0.00, 0.00, 0.00, 10000.00, 'RTO charges', 'system', 'system'),
(4, 1, 'Transportation', 'Vehicle transportation from factory', 8000.00, 18.00, 720.00, 720.00, 0.00, 9440.00, 'Factory to showroom', 'system', 'system'),
(4, 2, 'Insurance', 'Vehicle insurance for 1 year', 15000.00, 18.00, 1350.00, 1350.00, 0.00, 17700.00, 'Comprehensive insurance', 'system', 'system'),
(4, 3, 'Registration', 'RTO registration charges', 5000.00, 0.00, 0.00, 0.00, 0.00, 5000.00, 'RTO charges', 'system', 'system'),
(5, 1, 'Transportation', 'Vehicle transportation from factory', 16000.00, 18.00, 1440.00, 1440.00, 0.00, 18880.00, 'Factory to showroom', 'system', 'system'),
(5, 2, 'Insurance', 'Vehicle insurance for 1 year', 26000.00, 18.00, 2340.00, 2340.00, 0.00, 30680.00, 'Comprehensive insurance', 'system', 'system'),
(5, 3, 'Registration', 'RTO registration charges', 10000.00, 0.00, 0.00, 0.00, 0.00, 10000.00, 'RTO charges', 'system', 'system');

-- Insert Cost Approval Records (actual records from vsms-modern)
INSERT INTO trn_cost_approve (cost_id, approval_level, approved_by, approved_date, approval_status, approval_remarks, created_by, updated_by) VALUES
(1, 1, 'Suresh Singh', '2025-04-17', 'APPROVED', 'Cost estimation is reasonable', 'system', 'system'),
(2, 1, 'Suresh Singh', '2025-04-18', 'APPROVED', 'Cost estimation is reasonable', 'system', 'system'),
(4, 1, 'Suresh Singh', '2025-04-20', 'APPROVED', 'Cost estimation is reasonable', 'system', 'system');

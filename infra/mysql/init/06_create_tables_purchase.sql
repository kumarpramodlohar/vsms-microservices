-- =====================================================
-- VSMS Purchase Database - Purchase Module Tables
-- Based on vsms-modern schema (V13, V26, V35 migrations)
-- =====================================================

USE vsms_purchase;

-- 1. trn_purchase_header - Purchase bill header (V13 migration)
CREATE TABLE IF NOT EXISTS trn_purchase_header (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_no VARCHAR(50) NOT NULL UNIQUE,
    purchase_date DATE,
    vendor_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    bill_no VARCHAR(50),
    bill_date DATE,
    challan_no VARCHAR(50),
    challan_date DATE,
    po_no VARCHAR(50),
    po_date DATE,
    basic_amount DECIMAL(15,2),
    discount_amount DECIMAL(15,2),
    taxable_amount DECIMAL(15,2),
    cgst DECIMAL(15,2),
    sgst DECIMAL(15,2),
    igst DECIMAL(15,2),
    gst_amount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    total_amount_in_words VARCHAR(500),
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_purchase_no (purchase_no),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_purchase_date (purchase_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. trn_purchase_detail - Purchase bill details (V13 migration)
CREATE TABLE IF NOT EXISTS trn_purchase_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
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
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (purchase_id) REFERENCES trn_purchase_header(id) ON DELETE CASCADE,
    INDEX idx_purchase_id (purchase_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. trn_indent_header - Indent/purchase request header (V13 migration)
CREATE TABLE IF NOT EXISTS trn_indent_header (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    indent_no VARCHAR(50) NOT NULL UNIQUE,
    indent_date DATE,
    department VARCHAR(100),
    requested_by VARCHAR(100),
    approved_by VARCHAR(100),
    approved_date DATE,
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'PENDING',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_indent_no (indent_no),
    INDEX idx_status (status),
    INDEX idx_indent_date (indent_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. trn_indent_detail - Indent/purchase request details (V13 migration)
CREATE TABLE IF NOT EXISTS trn_indent_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    indent_id BIGINT NOT NULL,
    sl_no INT,
    item_id BIGINT,
    catg_id BIGINT,
    subcatg_id BIGINT,
    make BIGINT,
    model VARCHAR(100),
    spec VARCHAR(500),
    brief_desc VARCHAR(1000),
    uom BIGINT,
    qty INT NOT NULL,
    required_date DATE,
    remarks VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (indent_id) REFERENCES trn_indent_header(id) ON DELETE CASCADE,
    INDEX idx_indent_id (indent_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. trn_po_header - Purchase Order header (V26 migration)
CREATE TABLE IF NOT EXISTS trn_po_header (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_no VARCHAR(50) NOT NULL UNIQUE,
    po_date DATE,
    vendor_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    indent_id BIGINT,
    delivery_date DATE,
    delivery_address VARCHAR(500),
    payment_terms VARCHAR(500),
    basic_amount DECIMAL(15,2),
    discount_amount DECIMAL(15,2),
    taxable_amount DECIMAL(15,2),
    cgst DECIMAL(15,2),
    sgst DECIMAL(15,2),
    igst DECIMAL(15,2),
    gst_amount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    total_amount_in_words VARCHAR(500),
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_po_no (po_no),
    INDEX idx_vendor_id (vendor_id),
    INDEX idx_company_id (company_id),
    INDEX idx_indent_id (indent_id),
    INDEX idx_status (status),
    INDEX idx_po_date (po_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. trn_po_detail - Purchase Order details (V26 migration)
CREATE TABLE IF NOT EXISTS trn_po_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_id BIGINT NOT NULL,
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
    received_qty INT DEFAULT 0,
    pending_qty INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (po_id) REFERENCES trn_po_header(id) ON DELETE CASCADE,
    INDEX idx_po_id (po_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. trn_po_terms - Purchase Order terms (V26 migration)
CREATE TABLE IF NOT EXISTS trn_po_terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_id BIGINT NOT NULL,
    term_no INT,
    term_desc VARCHAR(1000),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (po_id) REFERENCES trn_po_header(id) ON DELETE CASCADE,
    INDEX idx_po_id (po_id),
    INDEX idx_term_no (term_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. trn_po_enclosure - Purchase Order enclosures (V26 migration)
CREATE TABLE IF NOT EXISTS trn_po_enclosure (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_id BIGINT NOT NULL,
    enclosure_name VARCHAR(255),
    enclosure_path VARCHAR(500),
    enclosure_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (po_id) REFERENCES trn_po_header(id) ON DELETE CASCADE,
    INDEX idx_po_id (po_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. trn_po_others - Purchase Order additional charges (V35 migration)
CREATE TABLE IF NOT EXISTS trn_po_others (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_id BIGINT NOT NULL,
    desc_id BIGINT NOT NULL,
    amount_oth DECIMAL(15,2) NOT NULL,
    gst_oth DECIMAL(5,2),
    cgst_amt_oth DECIMAL(15,2),
    sgst_amt_oth DECIMAL(15,2),
    igst_amt_oth DECIMAL(15,2),
    total_amount_oth DECIMAL(15,2),
    active_oth VARCHAR(1) DEFAULT 'Y',
    
    FOREIGN KEY (po_id) REFERENCES trn_po_header(id) ON DELETE CASCADE,
    INDEX idx_po_id (po_id),
    INDEX idx_desc_id (desc_id),
    INDEX idx_active_oth (active_oth)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. trn_employee_advance_hdr - Employee advance header (V7 migration)
CREATE TABLE IF NOT EXISTS trn_employee_advance_hdr (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    advance_no VARCHAR(50) NOT NULL UNIQUE,
    advance_date DATE,
    employee_id BIGINT NOT NULL,
    department VARCHAR(100),
    purpose VARCHAR(500),
    amount DECIMAL(15,2) NOT NULL,
    approved_by VARCHAR(100),
    approved_date DATE,
    remarks VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'PENDING',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_advance_no (advance_no),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_advance_date (advance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. trn_employee_advance_dtl - Employee advance details (V7 migration)
CREATE TABLE IF NOT EXISTS trn_employee_advance_dtl (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    advance_id BIGINT NOT NULL,
    sl_no INT,
    expense_type VARCHAR(100),
    description VARCHAR(500),
    amount DECIMAL(15,2) NOT NULL,
    receipt_no VARCHAR(50),
    receipt_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (advance_id) REFERENCES trn_employee_advance_hdr(id) ON DELETE CASCADE,
    INDEX idx_advance_id (advance_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for better performance
CREATE INDEX idx_trn_purchase_header_composite ON trn_purchase_header(purchase_no, is_active, status);
CREATE INDEX idx_trn_purchase_detail_composite ON trn_purchase_detail(purchase_id, is_active);
CREATE INDEX idx_trn_po_header_composite ON trn_po_header(po_no, is_active, status);
CREATE INDEX idx_trn_po_detail_composite ON trn_po_detail(po_id, is_active);

-- Add comments for documentation
ALTER TABLE trn_purchase_header COMMENT = 'Purchase bill header table';
ALTER TABLE trn_purchase_detail COMMENT = 'Purchase bill detail table';
ALTER TABLE trn_indent_header COMMENT = 'Indent/purchase request header table';
ALTER TABLE trn_indent_detail COMMENT = 'Indent/purchase request detail table';
ALTER TABLE trn_po_header COMMENT = 'Purchase Order header table';
ALTER TABLE trn_po_detail COMMENT = 'Purchase Order detail table';
ALTER TABLE trn_po_terms COMMENT = 'Purchase Order terms table';
ALTER TABLE trn_po_enclosure COMMENT = 'Purchase Order enclosures table';
ALTER TABLE trn_po_others COMMENT = 'Purchase Order additional charges table';
ALTER TABLE trn_employee_advance_hdr COMMENT = 'Employee advance header table';
ALTER TABLE trn_employee_advance_dtl COMMENT = 'Employee advance detail table';

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Indent Headers (actual records from vsms-modern)
INSERT INTO trn_indent_header (indent_no, indent_date, department, requested_by, approved_by, approved_date, remarks, status, created_by, updated_by, version) VALUES
('IND-2025-001', '2025-04-01', 'Sales', 'Rajesh Kumar', 'Suresh Singh', '2025-04-02', 'Urgent requirement for vehicles', 'APPROVED', 'system', 'system', 0),
('IND-2025-002', '2025-04-05', 'Service', 'Mohan Das', 'Suresh Singh', '2025-04-06', 'Spare parts for service center', 'APPROVED', 'system', 'system', 0),
('IND-2025-003', '2025-04-10', 'Inventory', 'Vijay Patel', 'Suresh Singh', '2025-04-11', 'Stock replenishment', 'APPROVED', 'system', 'system', 0);

-- Insert Indent Details (actual records from vsms-modern)
INSERT INTO trn_indent_detail (indent_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, qty, required_date, remarks, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, 5, '2025-04-15', 'For showroom display', 'system', 'system'),
(1, 2, 3, 1, 1, 2, 'City', 'V Petrol', 'Honda City V Petrol', 1, 3, '2025-04-15', 'For customer delivery', 'system', 'system'),
(2, 1, 8, 2, 5, 1, 'Oil Filter', 'Standard', 'Oil Filter Standard', 1, 50, '2025-04-10', 'For service center', 'system', 'system'),
(2, 2, 10, 2, 6, 1, 'Brake Pads', 'Front', 'Brake Pads Front', 1, 30, '2025-04-10', 'For service center', 'system', 'system'),
(3, 1, 14, 4, 12, 6, 'Engine Oil', '15W40', 'Engine Oil 15W40', 4, 100, '2025-04-15', 'Stock replenishment', 'system', 'system'),
(3, 2, 16, 5, 15, 7, 'Tyre', '185/65R15', 'Tyre 185/65R15', 1, 40, '2025-04-15', 'Stock replenishment', 'system', 'system');

-- Insert Purchase Order Headers (actual records from vsms-modern)
INSERT INTO trn_po_header (po_no, po_date, vendor_id, company_id, indent_id, delivery_date, delivery_address, payment_terms, basic_amount, discount_amount, taxable_amount, cgst, sgst, igst, gst_amount, total_amount, remarks, status, created_by, updated_by, version) VALUES
('PO-2025-001', '2025-04-03', 1, 1, 1, '2025-04-20', '123 Main Street, Mumbai', '30 Days Credit', 500000.00, 25000.00, 475000.00, 42750.00, 42750.00, 0.00, 85500.00, 560500.00, 'Vehicle purchase order', 'APPROVED', 'system', 'system', 0),
('PO-2025-002', '2025-04-07', 2, 1, 2, '2025-04-15', '456 Service Road, Delhi', '15 Days Credit', 50000.00, 2500.00, 47500.00, 4275.00, 4275.00, 0.00, 8550.00, 56050.00, 'Spare parts order', 'APPROVED', 'system', 'system', 0),
('PO-2025-003', '2025-04-12', 3, 1, 3, '2025-04-25', '789 Highway, Chennai', '45 Days Credit', 75000.00, 3750.00, 71250.00, 6412.50, 6412.50, 0.00, 12825.00, 84075.00, 'Tyres and lubricants order', 'APPROVED', 'system', 'system', 0);

-- Insert Purchase Order Details (actual records from vsms-modern)
INSERT INTO trn_po_detail (po_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, hsn, qty, rate, amount, discount, discount_amt, tax_percent, taxable_amt, cgst_amt, sgst_amt, igst_amt, net_amount, received_qty, pending_qty, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 5, 100000.00, 500000.00, 5.00, 25000.00, 28.00, 475000.00, 42750.00, 42750.00, 0.00, 560500.00, 0, 5, 'system', 'system'),
(2, 1, 8, 2, 5, 1, 'Oil Filter', 'Standard', 'Oil Filter Standard', 1, '8421', 50, 500.00, 25000.00, 5.00, 1250.00, 18.00, 23750.00, 2137.50, 2137.50, 0.00, 28025.00, 0, 50, 'system', 'system'),
(2, 2, 10, 2, 6, 1, 'Brake Pads', 'Front', 'Brake Pads Front', 1, '8708', 30, 500.00, 15000.00, 5.00, 750.00, 28.00, 14250.00, 1282.50, 1282.50, 0.00, 16815.00, 0, 30, 'system', 'system'),
(3, 1, 14, 4, 12, 6, 'Engine Oil', '15W40', 'Engine Oil 15W40', 4, '2710', 100, 250.00, 25000.00, 5.00, 1250.00, 18.00, 23750.00, 2137.50, 2137.50, 0.00, 28025.00, 0, 100, 'system', 'system'),
(3, 2, 16, 5, 15, 7, 'Tyre', '185/65R15', 'Tyre 185/65R15', 1, '4011', 40, 1000.00, 40000.00, 5.00, 2000.00, 28.00, 38000.00, 3420.00, 3420.00, 0.00, 44840.00, 0, 40, 'system', 'system');

-- Insert Purchase Order Terms (actual records from vsms-modern)
INSERT INTO trn_po_terms (po_id, term_no, term_desc, created_by, updated_by) VALUES
(1, 1, 'Delivery within 15 days from PO date', 'system', 'system'),
(1, 2, 'Payment: 30 days credit from date of invoice', 'system', 'system'),
(1, 3, 'Warranty: As per manufacturer policy', 'system', 'system'),
(2, 1, 'Delivery within 7 days from PO date', 'system', 'system'),
(2, 2, 'Payment: 15 days credit from date of invoice', 'system', 'system'),
(2, 3, 'Quality: As per OEM specifications', 'system', 'system'),
(3, 1, 'Delivery within 10 days from PO date', 'system', 'system'),
(3, 2, 'Payment: 45 days credit from date of invoice', 'system', 'system'),
(3, 3, 'Quality: As per manufacturer specifications', 'system', 'system');

-- Insert Purchase Headers (actual records from vsms-modern)
INSERT INTO trn_purchase_header (purchase_no, purchase_date, vendor_id, company_id, bill_no, bill_date, challan_no, challan_date, po_no, po_date, basic_amount, discount_amount, taxable_amount, cgst, sgst, igst, gst_amount, total_amount, remarks, status, created_by, updated_by, version) VALUES
('PUR-2025-001', '2025-04-20', 1, 1, 'BILL-001', '2025-04-20', 'CH-001', '2025-04-20', 'PO-2025-001', '2025-04-03', 500000.00, 25000.00, 475000.00, 42750.00, 42750.00, 0.00, 85500.00, 560500.00, 'Vehicle purchase bill', 'APPROVED', 'system', 'system', 0),
('PUR-2025-002', '2025-04-15', 2, 1, 'BILL-002', '2025-04-15', 'CH-002', '2025-04-15', 'PO-2025-002', '2025-04-07', 50000.00, 2500.00, 47500.00, 4275.00, 4275.00, 0.00, 8550.00, 56050.00, 'Spare parts purchase bill', 'APPROVED', 'system', 'system', 0);

-- Insert Purchase Details (actual records from vsms-modern)
INSERT INTO trn_purchase_detail (purchase_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, hsn, qty, rate, amount, discount, discount_amt, tax_percent, taxable_amt, cgst_amt, sgst_amt, igst_amt, net_amount, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 5, 100000.00, 500000.00, 5.00, 25000.00, 28.00, 475000.00, 42750.00, 42750.00, 0.00, 560500.00, 'system', 'system'),
(2, 1, 8, 2, 5, 1, 'Oil Filter', 'Standard', 'Oil Filter Standard', 1, '8421', 50, 500.00, 25000.00, 5.00, 1250.00, 18.00, 23750.00, 2137.50, 2137.50, 0.00, 28025.00, 'system', 'system'),
(2, 2, 10, 2, 6, 1, 'Brake Pads', 'Front', 'Brake Pads Front', 1, '8708', 30, 500.00, 15000.00, 5.00, 750.00, 28.00, 14250.00, 1282.50, 1282.50, 0.00, 16815.00, 'system', 'system');

-- Insert Employee Advance Headers (actual records from vsms-modern)
INSERT INTO trn_employee_advance_hdr (advance_no, advance_date, employee_id, department, purpose, amount, approved_by, approved_date, remarks, status, created_by, updated_by, version) VALUES
('ADV-2025-001', '2025-04-01', 1, 'Sales', 'Travel expenses for client meeting', 10000.00, 'Suresh Singh', '2025-04-02', 'Client meeting in Mumbai', 'APPROVED', 'system', 'system', 0),
('ADV-2025-002', '2025-04-05', 2, 'Service', 'Tools and equipment purchase', 15000.00, 'Suresh Singh', '2025-04-06', 'Service center tools', 'APPROVED', 'system', 'system', 0);

-- Insert Employee Advance Details (actual records from vsms-modern)
INSERT INTO trn_employee_advance_dtl (advance_id, sl_no, expense_type, description, amount, receipt_no, receipt_date, created_by, updated_by) VALUES
(1, 1, 'Travel', 'Flight tickets Mumbai-Delhi', 5000.00, 'REC-001', '2025-04-03', 'system', 'system'),
(1, 2, 'Accommodation', 'Hotel stay 2 nights', 3000.00, 'REC-002', '2025-04-04', 'system', 'system'),
(1, 3, 'Food', 'Meals during travel', 2000.00, 'REC-003', '2025-04-05', 'system', 'system'),
(2, 1, 'Tools', 'Wrench set', 5000.00, 'REC-004', '2025-04-07', 'system', 'system'),
(2, 2, 'Equipment', 'Diagnostic scanner', 10000.00, 'REC-005', '2025-04-08', 'system', 'system');

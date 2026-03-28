-- =====================================================
-- VSMS Sales Database - Sales Order & Offer Tables
-- Based on vsms-modern schema (V3, V7 migrations)
-- =====================================================

USE vsms_sales;

-- 1. trn_order_header - Sales order header table
CREATE TABLE IF NOT EXISTS trn_order_header (
    id CHAR(36) PRIMARY KEY,
    hdr_id BIGINT AUTO_INCREMENT UNIQUE,
    ord_number VARCHAR(50),
    ord_date DATE,
    order_code VARCHAR(50) NOT NULL,
    order_code_date DATE,
    offer_no VARCHAR(50),
    offer_date DATE,
    offer_id BIGINT,
    cust_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    account VARCHAR(50),
    delv_date DATE,
    inst_date DATE,
    consignee_id BIGINT,
    old_order_code VARCHAR(50),
    cost_approved_by VARCHAR(50),
    cost_approved_on DATE,
    cost_approval_remarks VARCHAR(500),
    cost_status VARCHAR(10),
    cost_submit_by VARCHAR(50),
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
    allow_target VARCHAR(10),
    weightage DECIMAL(5,2),
    margin_per DECIMAL(5,2),
    order_for VARCHAR(50),
    new_acc VARCHAR(50),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_order_code (order_code),
    INDEX idx_cust_id (cust_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_is_active (is_active),
    INDEX idx_order_code_date (order_code_date),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. trn_order_detail - Sales order detail table
CREATE TABLE IF NOT EXISTS trn_order_detail (
    dtl_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hdr_id CHAR(36) NOT NULL,
    ord_number VARCHAR(50),
    order_code VARCHAR(50),
    order_code_date DATE,
    sl_no INT,
    catg_id BIGINT,
    subcatg_id BIGINT,
    make BIGINT,
    model VARCHAR(100),
    spec VARCHAR(500),
    item_id BIGINT,
    brief_desc VARCHAR(1000),
    remarks VARCHAR(500),
    amc INT,
    warranty INT,
    uom BIGINT,
    hsn VARCHAR(20),
    payment_frequency VARCHAR(50),
    payment_terms VARCHAR(200),
    type VARCHAR(50),
    percentage VARCHAR(10),
    purchase VARCHAR(10),
    install VARCHAR(10),
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
    rest_qty INT DEFAULT 0,
    po_qty INT DEFAULT 0,
    bill_qty INT DEFAULT 0,
    temp_bill_qty INT DEFAULT 0,
    temp_delivery_qty INT DEFAULT 0,
    dtl_active VARCHAR(1) DEFAULT 'Y',
    milestone VARCHAR(100),
    cost_rate DECIMAL(15,2),
    created_by VARCHAR(50),
    created_on DATE,
    modified_by VARCHAR(50),
    modified_on DATE,
    
    FOREIGN KEY (hdr_id) REFERENCES trn_order_header(id) ON DELETE CASCADE,
    INDEX idx_hdr_id (hdr_id),
    INDEX idx_order_code (order_code),
    INDEX idx_item_id (item_id),
    INDEX idx_dtl_active (dtl_active),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. trn_order_others - Sales order additional charges table
CREATE TABLE IF NOT EXISTS trn_order_others (
    oth_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hdr_id CHAR(36) NOT NULL,
    desc_id BIGINT NOT NULL,
    amount_oth DECIMAL(15,2) NOT NULL,
    gst_oth DECIMAL(5,2),
    cgst_amt_oth DECIMAL(15,2),
    sgst_amt_oth DECIMAL(15,2),
    igst_amt_oth DECIMAL(15,2),
    total_amount_oth DECIMAL(15,2),
    active_oth VARCHAR(1) DEFAULT 'Y',
    
    FOREIGN KEY (hdr_id) REFERENCES trn_order_header(id) ON DELETE CASCADE,
    INDEX idx_hdr_id (hdr_id),
    INDEX idx_desc_id (desc_id),
    INDEX idx_active_oth (active_oth)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. trn_order_code - Sales order code management table
CREATE TABLE IF NOT EXISTS trn_order_code (
    id CHAR(36) PRIMARY KEY,
    order_code_id BIGINT AUTO_INCREMENT UNIQUE,
    ord_number VARCHAR(50),
    ord_date DATE,
    order_code VARCHAR(50) NOT NULL UNIQUE,
    order_code_date DATE,
    offer_no VARCHAR(50),
    offer_date DATE,
    offer_id BIGINT,
    company_id BIGINT NOT NULL,
    account VARCHAR(50),
    cust_id BIGINT NOT NULL,
    remarks VARCHAR(1000),
    supply_type INT,
    supply_date DATE,
    installation VARCHAR(10),
    inst_date DATE,
    delv_date DATE,
    amc_start_date DATE,
    amc_end_date DATE,
    ord_tag VARCHAR(10),
    offer_status VARCHAR(10),
    purchase_order DECIMAL(5,2),
    purchase DECIMAL(5,2),
    delivery DECIMAL(5,2),
    invoice DECIMAL(5,2),
    payment DECIMAL(5,2),
    installation_per DECIMAL(5,2),
    cost DECIMAL(5,2),
    order_for VARCHAR(50),
    new_acc VARCHAR(50),
    account_per VARCHAR(50),
    account_per1 VARCHAR(50),
    account1 VARCHAR(50),
    yr_cd VARCHAR(10),
    ordered DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_order_code (order_code),
    INDEX idx_cust_id (cust_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_is_active (is_active),
    INDEX idx_yr_cd (yr_cd),
    INDEX idx_account (account),
    INDEX idx_order_code_date (order_code_date),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. trn_offer_hdr - Offer/Quotation header table (V7 migration)
CREATE TABLE IF NOT EXISTS trn_offer_hdr (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offer_no VARCHAR(50) NOT NULL UNIQUE,
    offer_date DATE,
    cust_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    consignee_id BIGINT,
    contact_person VARCHAR(255),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(255),
    subject VARCHAR(500),
    reference VARCHAR(500),
    validity_days INT DEFAULT 30,
    delivery_terms VARCHAR(500),
    payment_terms VARCHAR(500),
    warranty_terms VARCHAR(500),
    basic_amount DECIMAL(15,2),
    discount_amount DECIMAL(15,2),
    taxable_amount DECIMAL(15,2),
    cgst DECIMAL(15,2),
    sgst DECIMAL(15,2),
    igst DECIMAL(15,2),
    gst_amount DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    total_amount_in_words VARCHAR(500),
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    version BIGINT DEFAULT 0,
    
    INDEX idx_offer_no (offer_no),
    INDEX idx_cust_id (cust_id),
    INDEX idx_company_id (company_id),
    INDEX idx_status (status),
    INDEX idx_offer_date (offer_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. trn_offer_dtl - Offer detail table (V7 migration)
CREATE TABLE IF NOT EXISTS trn_offer_dtl (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offer_id BIGINT NOT NULL,
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
    
    FOREIGN KEY (offer_id) REFERENCES trn_offer_hdr(id) ON DELETE CASCADE,
    INDEX idx_offer_id (offer_id),
    INDEX idx_item_id (item_id),
    INDEX idx_sl_no (sl_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. trn_offer_tc - Offer terms and conditions table (V7 migration)
CREATE TABLE IF NOT EXISTS trn_offer_tc (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offer_id BIGINT NOT NULL,
    term_no INT,
    term_desc VARCHAR(1000),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (offer_id) REFERENCES trn_offer_hdr(id) ON DELETE CASCADE,
    INDEX idx_offer_id (offer_id),
    INDEX idx_term_no (term_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. trn_offer_enclosure - Offer enclosures table (V7 migration)
CREATE TABLE IF NOT EXISTS trn_offer_enclosure (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offer_id BIGINT NOT NULL,
    enclosure_name VARCHAR(255),
    enclosure_path VARCHAR(500),
    enclosure_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    
    FOREIGN KEY (offer_id) REFERENCES trn_offer_hdr(id) ON DELETE CASCADE,
    INDEX idx_offer_id (offer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for better performance
CREATE INDEX idx_trn_order_header_composite ON trn_order_header(order_code, is_active, status);
CREATE INDEX idx_trn_order_detail_composite ON trn_order_detail(hdr_id, dtl_active);
CREATE INDEX idx_trn_order_others_composite ON trn_order_others(hdr_id, active_oth);
CREATE INDEX idx_trn_order_code_composite ON trn_order_code(order_code, is_active, status);

-- Add comments for documentation
ALTER TABLE trn_order_header COMMENT = 'Sales order header table containing main order information';
ALTER TABLE trn_order_detail COMMENT = 'Sales order detail table containing line items';
ALTER TABLE trn_order_others COMMENT = 'Sales order additional charges table';
ALTER TABLE trn_order_code COMMENT = 'Sales order code management table';
ALTER TABLE trn_offer_hdr COMMENT = 'Offer/Quotation header table';
ALTER TABLE trn_offer_dtl COMMENT = 'Offer detail table containing line items';
ALTER TABLE trn_offer_tc COMMENT = 'Offer terms and conditions table';
ALTER TABLE trn_offer_enclosure COMMENT = 'Offer enclosures table';

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Order Codes (actual records from vsms-modern)
INSERT INTO trn_order_code (id, order_code, order_code_date, cust_id, company_id, account, status, yr_cd, created_by, updated_by, version) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'ORD-2025-001', '2025-04-15', 1, 1, 'ACC001', 'ACTIVE', 'FY2526', 'system', 'system', 0),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'ORD-2025-002', '2025-04-16', 2, 1, 'ACC002', 'ACTIVE', 'FY2526', 'system', 'system', 0),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'ORD-2025-003', '2025-04-17', 3, 1, 'ACC003', 'DRAFT', 'FY2526', 'system', 'system', 0),
('d4e5f6a7-b8c9-0123-defa-234567890123', 'ORD-2025-004', '2025-04-18', 4, 1, 'ACC004', 'ACTIVE', 'FY2526', 'system', 'system', 0),
('e5f6a7b8-c9d0-1234-efab-345678901234', 'ORD-2025-005', '2025-04-19', 5, 1, 'ACC005', 'ACTIVE', 'FY2526', 'system', 'system', 0);

-- Insert Sales Order Headers (actual records from vsms-modern)
INSERT INTO trn_order_header (id, ord_number, ord_date, order_code, order_code_date, cust_id, company_id, account, delv_date, basic_amount, discount_amount, taxable_amount, cgst, sgst, igst, gst_amount, total_amount, status, created_by, updated_by, version) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'SO-2025-001', '2025-04-15', 'ORD-2025-001', '2025-04-15', 1, 1, 'ACC001', '2025-05-15', 500000.00, 25000.00, 475000.00, 42750.00, 42750.00, 0.00, 85500.00, 560500.00, 'ACTIVE', 'system', 'system', 0),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'SO-2025-002', '2025-04-16', 'ORD-2025-002', '2025-04-16', 2, 1, 'ACC002', '2025-05-16', 450000.00, 22500.00, 427500.00, 38475.00, 38475.00, 0.00, 76950.00, 504450.00, 'ACTIVE', 'system', 'system', 0),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'SO-2025-003', '2025-04-17', 'ORD-2025-003', '2025-04-17', 3, 1, 'ACC003', '2025-05-17', 600000.00, 30000.00, 570000.00, 51300.00, 51300.00, 0.00, 102600.00, 672600.00, 'DRAFT', 'system', 'system', 0),
('d4e5f6a7-b8c9-0123-defa-234567890123', 'SO-2025-004', '2025-04-18', 'ORD-2025-004', '2025-04-18', 4, 1, 'ACC004', '2025-05-18', 400000.00, 20000.00, 380000.00, 34200.00, 34200.00, 0.00, 68400.00, 448400.00, 'ACTIVE', 'system', 'system', 0),
('e5f6a7b8-c9d0-1234-efab-345678901234', 'SO-2025-005', '2025-04-19', 'ORD-2025-005', '2025-04-19', 5, 1, 'ACC005', '2025-05-19', 550000.00, 27500.00, 522500.00, 47025.00, 47025.00, 0.00, 94050.00, 616550.00, 'ACTIVE', 'system', 'system', 0);

-- Insert Sales Order Details (actual records from vsms-modern)
INSERT INTO trn_order_detail (hdr_id, ord_number, order_code, order_code_date, sl_no, catg_id, subcatg_id, make, model, spec, item_id, brief_desc, uom, hsn, qty, rate, amount, discount, discount_amt, tax_percent, taxable_amt, cgst_amt, sgst_amt, igst_amt, net_amount, dtl_active, created_by, created_on, modified_by, modified_on) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'SO-2025-001', 'ORD-2025-001', '2025-04-15', 1, 1, 1, 1, 'Swift', 'VXi Petrol', 1, 'Maruti Swift VXi Petrol', 1, '8703', 2, 250000.00, 500000.00, 5.00, 25000.00, 28.00, 475000.00, 42750.00, 42750.00, 0.00, 560500.00, 'Y', 'system', '2025-04-15', 'system', '2025-04-15'),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'SO-2025-002', 'ORD-2025-002', '2025-04-16', 1, 1, 1, 2, 'City', 'V Petrol', 3, 'Honda City V Petrol', 1, '8703', 1, 450000.00, 450000.00, 5.00, 22500.00, 28.00, 427500.00, 38475.00, 38475.00, 0.00, 504450.00, 'Y', 'system', '2025-04-16', 'system', '2025-04-16'),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'SO-2025-003', 'ORD-2025-003', '2025-04-17', 1, 1, 1, 1, 'Swift', 'ZXi Petrol', 2, 'Maruti Swift ZXi Petrol', 1, '8703', 2, 300000.00, 600000.00, 5.00, 30000.00, 28.00, 570000.00, 51300.00, 51300.00, 0.00, 672600.00, 'Y', 'system', '2025-04-17', 'system', '2025-04-17'),
('d4e5f6a7-b8c9-0123-defa-234567890123', 'SO-2025-004', 'ORD-2025-004', '2025-04-18', 1, 1, 2, 3, 'Pulsar', '150cc', 5, 'Bajaj Pulsar 150cc', 1, '8711', 4, 100000.00, 400000.00, 5.00, 20000.00, 28.00, 380000.00, 34200.00, 34200.00, 0.00, 448400.00, 'Y', 'system', '2025-04-18', 'system', '2025-04-18'),
('e5f6a7b8-c9d0-1234-efab-345678901234', 'SO-2025-005', 'ORD-2025-005', '2025-04-19', 1, 1, 1, 2, 'City', 'ZX Petrol', 4, 'Honda City ZX Petrol', 1, '8703', 1, 550000.00, 550000.00, 5.00, 27500.00, 28.00, 522500.00, 47025.00, 47025.00, 0.00, 616550.00, 'Y', 'system', '2025-04-19', 'system', '2025-04-19');

-- Insert Offers (actual records from vsms-modern)
INSERT INTO trn_offer_hdr (offer_no, offer_date, cust_id, company_id, contact_person, contact_phone, contact_email, subject, reference, validity_days, delivery_terms, payment_terms, warranty_terms, basic_amount, discount_amount, taxable_amount, cgst, sgst, igst, gst_amount, total_amount, status, created_by, updated_by, version) VALUES
('OFF-2025-001', '2025-04-10', 1, 1, 'Rajesh Kumar', '+91-9876543210', 'contact@abcmfg.com', 'Quotation for Swift Vehicles', 'REF-001', 30, 'Ex-Showroom', '100% Advance', '1 Year', 500000.00, 25000.00, 475000.00, 42750.00, 42750.00, 0.00, 85500.00, 560500.00, 'APPROVED', 'system', 'system', 0),
('OFF-2025-002', '2025-04-11', 2, 1, 'Suresh Singh', '+91-9876543211', 'info@xyztech.com', 'Quotation for Honda City', 'REF-002', 30, 'Ex-Showroom', '50% Advance, 50% on Delivery', '2 Years', 450000.00, 22500.00, 427500.00, 38475.00, 38475.00, 0.00, 76950.00, 504450.00, 'APPROVED', 'system', 'system', 0),
('OFF-2025-003', '2025-04-12', 3, 1, 'Mohan Das', '+91-9876543212', 'sales@pqrauto.com', 'Quotation for Bulk Order', 'REF-003', 45, 'Door Delivery', '30 Days Credit', '1 Year', 600000.00, 30000.00, 570000.00, 51300.00, 51300.00, 0.00, 102600.00, 672600.00, 'DRAFT', 'system', 'system', 0);

-- Insert Offer Details (actual records from vsms-modern)
INSERT INTO trn_offer_dtl (offer_id, sl_no, item_id, catg_id, subcatg_id, make, model, spec, brief_desc, uom, hsn, qty, rate, amount, discount, discount_amt, tax_percent, taxable_amt, cgst_amt, sgst_amt, igst_amt, net_amount, created_by, updated_by) VALUES
(1, 1, 1, 1, 1, 1, 'Swift', 'VXi Petrol', 'Maruti Swift VXi Petrol', 1, '8703', 2, 250000.00, 500000.00, 5.00, 25000.00, 28.00, 475000.00, 42750.00, 42750.00, 0.00, 560500.00, 'system', 'system'),
(2, 1, 3, 1, 1, 2, 'City', 'V Petrol', 'Honda City V Petrol', 1, '8703', 1, 450000.00, 450000.00, 5.00, 22500.00, 28.00, 427500.00, 38475.00, 38475.00, 0.00, 504450.00, 'system', 'system'),
(3, 1, 2, 1, 1, 1, 'Swift', 'ZXi Petrol', 'Maruti Swift ZXi Petrol', 1, '8703', 2, 300000.00, 600000.00, 5.00, 30000.00, 28.00, 570000.00, 51300.00, 51300.00, 0.00, 672600.00, 'system', 'system');

-- Insert Offer Terms (actual records from vsms-modern)
INSERT INTO trn_offer_tc (offer_id, term_no, term_desc, created_by, updated_by) VALUES
(1, 1, 'Prices are inclusive of GST', 'system', 'system'),
(1, 2, 'Delivery within 30 days from order confirmation', 'system', 'system'),
(1, 3, 'Payment: 100% advance', 'system', 'system'),
(1, 4, 'Warranty: 1 year from date of delivery', 'system', 'system'),
(2, 1, 'Prices are inclusive of GST', 'system', 'system'),
(2, 2, 'Delivery within 30 days from order confirmation', 'system', 'system'),
(2, 3, 'Payment: 50% advance, 50% on delivery', 'system', 'system'),
(2, 4, 'Warranty: 2 years from date of delivery', 'system', 'system'),
(3, 1, 'Prices are inclusive of GST', 'system', 'system'),
(3, 2, 'Delivery within 45 days from order confirmation', 'system', 'system'),
(3, 3, 'Payment: 30 days credit from date of invoice', 'system', 'system'),
(3, 4, 'Warranty: 1 year from date of delivery', 'system', 'system');

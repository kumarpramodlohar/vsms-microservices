-- =====================================================
-- VSMS HR Database - HR Module Tables
-- Based on vsms-modern schema (V21, V22, V28, V34, V38, V41 migrations)
-- =====================================================

USE vsms_hr;

-- 1. mst_employee - Employee master table (V21 migration)
CREATE TABLE IF NOT EXISTS mst_employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(50) NOT NULL UNIQUE,
    employee_name VARCHAR(255) NOT NULL,
    father_name VARCHAR(255),
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    marital_status ENUM('SINGLE', 'MARRIED', 'DIVORCED', 'WIDOWED'),
    blood_group VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(255),
    address VARCHAR(500),
    city VARCHAR(100),
    state_id BIGINT,
    country_id BIGINT,
    pin_code VARCHAR(10),
    aadhar_no VARCHAR(12),
    pan_no VARCHAR(10),
    passport_no VARCHAR(20),
    driving_license_no VARCHAR(20),
    department_id BIGINT,
    designation_id BIGINT,
    date_of_joining DATE,
    date_of_confirmation DATE,
    employment_type ENUM('PERMANENT', 'CONTRACT', 'TEMPORARY', 'PROBATION'),
    reporting_to BIGINT,
    basic_salary DECIMAL(15,2),
    gross_salary DECIMAL(15,2),
    ctc DECIMAL(15,2),
    bank_name VARCHAR(255),
    bank_account_no VARCHAR(50),
    bank_ifsc VARCHAR(20),
    pf_account_no VARCHAR(50),
    esi_no VARCHAR(50),
    uan_no VARCHAR(50),
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_employee_code (employee_code),
    INDEX idx_employee_name (employee_name),
    INDEX idx_department_id (department_id),
    INDEX idx_designation_id (designation_id),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. mst_employee_detail - Employee details table (V21 migration)
CREATE TABLE IF NOT EXISTS mst_employee_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    detail_type VARCHAR(50),
    detail_value VARCHAR(500),
    effective_date DATE,
    remarks VARCHAR(500),
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (employee_id) REFERENCES mst_employee(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_detail_type (detail_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. trn_employee_attendance - Employee attendance table (V21 migration)
CREATE TABLE IF NOT EXISTS trn_employee_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    check_in_time DATETIME,
    check_out_time DATETIME,
    working_hours DECIMAL(5,2),
    overtime_hours DECIMAL(5,2),
    status ENUM('PRESENT', 'ABSENT', 'HALF_DAY', 'LEAVE', 'HOLIDAY', 'WEEK_OFF') DEFAULT 'PRESENT',
    leave_type VARCHAR(50),
    remarks VARCHAR(500),
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    FOREIGN KEY (employee_id) REFERENCES mst_employee(id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_status (status),
    UNIQUE KEY uk_employee_date (employee_id, attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. trn_employee_advance_hdr - Employee advance header table (V21 migration)
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
    FOREIGN KEY (employee_id) REFERENCES mst_employee(id) ON DELETE CASCADE,
    INDEX idx_advance_no (advance_no),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_advance_date (advance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. trn_employee_advance_dtl - Employee advance details table (V21 migration)
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

-- 6. mst_department - Department master table (V38 migration)
CREATE TABLE IF NOT EXISTS mst_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    department_code VARCHAR(50) UNIQUE,
    description VARCHAR(500),
    head_of_department BIGINT,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_department_name (department_name),
    INDEX idx_department_code (department_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. mst_designation - Designation master table (V38 migration)
CREATE TABLE IF NOT EXISTS mst_designation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    designation_name VARCHAR(100) NOT NULL UNIQUE,
    designation_code VARCHAR(50) UNIQUE,
    description VARCHAR(500),
    grade VARCHAR(20),
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_designation_name (designation_name),
    INDEX idx_designation_code (designation_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. mst_earning_deduction_master - Earning/Deduction master table (V34 migration)
CREATE TABLE IF NOT EXISTS mst_earning_deduction_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    component_name VARCHAR(100) NOT NULL,
    component_code VARCHAR(50) UNIQUE,
    component_type ENUM('EARNING', 'DEDUCTION') NOT NULL,
    calculation_type ENUM('FIXED', 'PERCENTAGE', 'FORMULA') NOT NULL,
    calculation_value DECIMAL(15,2),
    calculation_base VARCHAR(100),
    is_taxable VARCHAR(1) DEFAULT 'Y',
    is_mandatory VARCHAR(1) DEFAULT 'N',
    display_order INT,
    is_active VARCHAR(1) DEFAULT 'Y',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(25),
    updated_by VARCHAR(25),
    version BIGINT DEFAULT 0,
    INDEX idx_component_name (component_name),
    INDEX idx_component_code (component_code),
    INDEX idx_component_type (component_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create composite indexes for better performance
CREATE INDEX idx_mst_employee_composite ON mst_employee(employee_code, is_active);
CREATE INDEX idx_trn_employee_attendance_composite ON trn_employee_attendance(employee_id, attendance_date, status);
CREATE INDEX idx_trn_employee_advance_hdr_composite ON trn_employee_advance_hdr(advance_no, is_active, status);

-- Add comments for documentation
ALTER TABLE mst_employee COMMENT = 'Employee master table';
ALTER TABLE mst_employee_detail COMMENT = 'Employee details table';
ALTER TABLE trn_employee_attendance COMMENT = 'Employee attendance table';
ALTER TABLE trn_employee_advance_hdr COMMENT = 'Employee advance header table';
ALTER TABLE trn_employee_advance_dtl COMMENT = 'Employee advance detail table';
ALTER TABLE mst_department COMMENT = 'Department master table';
ALTER TABLE mst_designation COMMENT = 'Designation master table';
ALTER TABLE mst_earning_deduction_master COMMENT = 'Earning/Deduction master table';

-- =====================================================
-- Actual Records from vsms-modern schema
-- =====================================================

-- Insert Departments (actual records from vsms-modern)
INSERT INTO mst_department (department_name, department_code, description, created_by, updated_by, version) VALUES
('Sales', 'SALES', 'Sales and Marketing Department', 'system', 'system', 0),
('Service', 'SERVICE', 'Service and Maintenance Department', 'system', 'system', 0),
('Inventory', 'INV', 'Inventory and Warehouse Department', 'system', 'system', 0),
('Accounts', 'ACC', 'Accounts and Finance Department', 'system', 'system', 0),
('HR', 'HR', 'Human Resources Department', 'system', 'system', 0),
('Admin', 'ADMIN', 'Administration Department', 'system', 'system', 0),
('Purchase', 'PUR', 'Purchase and Procurement Department', 'system', 'system', 0),
('IT', 'IT', 'Information Technology Department', 'system', 'system', 0);

-- Insert Designations (actual records from vsms-modern)
INSERT INTO mst_designation (designation_name, designation_code, description, grade, created_by, updated_by, version) VALUES
('Manager', 'MGR', 'Department Manager', 'A', 'system', 'system', 0),
('Assistant Manager', 'AMGR', 'Assistant Department Manager', 'B', 'system', 'system', 0),
('Executive', 'EXEC', 'Executive Level', 'C', 'system', 'system', 0),
('Senior Executive', 'SEXE', 'Senior Executive', 'B', 'system', 'system', 0),
('Team Lead', 'TL', 'Team Lead', 'B', 'system', 'system', 0),
('Supervisor', 'SUP', 'Supervisor', 'C', 'system', 'system', 0),
('Staff', 'STF', 'Staff Member', 'D', 'system', 'system', 0),
('Trainee', 'TRN', 'Trainee', 'E', 'system', 'system', 0);

-- Insert Employees (actual records from vsms-modern)
INSERT INTO mst_employee (employee_code, employee_name, father_name, date_of_birth, gender, marital_status, blood_group, phone, email, address, city, state_id, country_id, pin_code, aadhar_no, pan_no, department_id, designation_id, date_of_joining, date_of_confirmation, employment_type, reporting_to, basic_salary, gross_salary, ctc, bank_name, bank_account_no, bank_ifsc, pf_account_no, esi_no, uan_no, created_by, updated_by, version) VALUES
('EMP001', 'Rajesh Kumar', 'Ramesh Kumar', '1985-05-15', 'MALE', 'MARRIED', 'B+', '+91-9876543210', 'rajesh.kumar@vsms.com', '123 Main Street', 'Mumbai', 1, 1, '400001', '123456789012', 'ABCDE1234F', 1, 1, '2020-01-15', '2020-07-15', 'PERMANENT', NULL, 50000.00, 75000.00, 900000.00, 'State Bank of India', '1234567890', 'SBIN0001234', 'PF123456', 'ESI123456', 'UAN123456', 'system', 'system', 0),
('EMP002', 'Suresh Singh', 'Mahesh Singh', '1988-08-20', 'MALE', 'MARRIED', 'O+', '+91-9876543211', 'suresh.singh@vsms.com', '456 Service Road', 'Delhi', 5, 1, '110001', '234567890123', 'BCDEF2345G', 2, 1, '2019-03-10', '2019-09-10', 'PERMANENT', 1, 45000.00, 70000.00, 840000.00, 'HDFC Bank', '0987654321', 'HDFC0001234', 'PF234567', 'ESI234567', 'UAN234567', 'system', 'system', 0),
('EMP003', 'Mohan Das', 'Sohan Das', '1990-12-10', 'MALE', 'SINGLE', 'A+', '+91-9876543212', 'mohan.das@vsms.com', '789 Auto Hub', 'Chennai', 3, 1, '600001', '345678901234', 'CDEFG3456H', 3, 3, '2021-06-01', '2021-12-01', 'PERMANENT', 2, 35000.00, 55000.00, 660000.00, 'ICICI Bank', '1122334455', 'ICIC0001234', 'PF345678', 'ESI345678', 'UAN345678', 'system', 'system', 0),
('EMP004', 'Vijay Patel', 'Ajay Patel', '1992-04-25', 'MALE', 'MARRIED', 'AB+', '+91-9876543213', 'vijay.patel@vsms.com', '321 Logistics Park', 'Ahmedabad', 4, 1, '380001', '456789012345', 'DEFGH4567I', 4, 3, '2022-02-15', '2022-08-15', 'PERMANENT', 1, 30000.00, 48000.00, 576000.00, 'Axis Bank', '5566778899', 'UTIB0001234', 'PF456789', 'ESI456789', 'UAN456789', 'system', 'system', 0),
('EMP005', 'Amit Sharma', 'Ramesh Sharma', '1995-07-30', 'MALE', 'SINGLE', 'B-', '+91-9876543214', 'amit.sharma@vsms.com', '654 Transport Nagar', 'Delhi', 5, 1, '110001', '567890123456', 'EFGHI5678J', 5, 7, '2023-01-10', NULL, 'PROBATION', 3, 25000.00, 40000.00, 480000.00, 'Punjab National Bank', '9988776655', 'PUNB0001234', 'PF567890', 'ESI567890', 'UAN567890', 'system', 'system', 0);

-- Insert Earning/Deduction Master (actual records from vsms-modern)
INSERT INTO mst_earning_deduction_master (component_name, component_code, component_type, calculation_type, calculation_value, calculation_base, is_taxable, is_mandatory, display_order, created_by, updated_by, version) VALUES
('Basic Salary', 'BASIC', 'EARNING', 'PERCENTAGE', 50.00, 'GROSS_SALARY', 'Y', 'Y', 1, 'system', 'system', 0),
('House Rent Allowance', 'HRA', 'EARNING', 'PERCENTAGE', 20.00, 'BASIC_SALARY', 'Y', 'Y', 2, 'system', 'system', 0),
('Dearness Allowance', 'DA', 'EARNING', 'PERCENTAGE', 10.00, 'BASIC_SALARY', 'Y', 'Y', 3, 'system', 'system', 0),
('Conveyance Allowance', 'CONV', 'EARNING', 'FIXED', 1600.00, NULL, 'Y', 'Y', 4, 'system', 'system', 0),
('Medical Allowance', 'MED', 'EARNING', 'FIXED', 1250.00, NULL, 'Y', 'Y', 5, 'system', 'system', 0),
('Special Allowance', 'SPL', 'EARNING', 'FORMULA', NULL, 'GROSS_SALARY - (BASIC + HRA + DA + CONV + MED)', 'Y', 'Y', 6, 'system', 'system', 0),
('Provident Fund', 'PF', 'DEDUCTION', 'PERCENTAGE', 12.00, 'BASIC_SALARY', 'N', 'Y', 1, 'system', 'system', 0),
('Employee State Insurance', 'ESI', 'DEDUCTION', 'PERCENTAGE', 1.75, 'GROSS_SALARY', 'N', 'Y', 2, 'system', 'system', 0),
('Professional Tax', 'PT', 'DEDUCTION', 'FIXED', 200.00, NULL, 'N', 'Y', 3, 'system', 'system', 0),
('Income Tax', 'IT', 'DEDUCTION', 'FORMULA', NULL, 'Taxable Income Slab', 'N', 'N', 4, 'system', 'system', 0);

-- Insert Employee Attendance (actual records from vsms-modern)
INSERT INTO trn_employee_attendance (employee_id, attendance_date, check_in_time, check_out_time, working_hours, overtime_hours, status, remarks, created_by, updated_by, version) VALUES
(1, '2025-04-01', '2025-04-01 09:00:00', '2025-04-01 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(1, '2025-04-02', '2025-04-02 09:15:00', '2025-04-02 18:30:00', 9.25, 0.50, 'PRESENT', 'Worked late', 'system', 'system', 0),
(1, '2025-04-03', '2025-04-03 09:00:00', '2025-04-03 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(2, '2025-04-01', '2025-04-01 09:30:00', '2025-04-01 18:30:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(2, '2025-04-02', '2025-04-02 09:00:00', '2025-04-02 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(2, '2025-04-03', NULL, NULL, 0.00, 0.00, 'LEAVE', 'Sick leave', 'system', 'system', 0),
(3, '2025-04-01', '2025-04-01 09:00:00', '2025-04-01 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(3, '2025-04-02', '2025-04-02 09:00:00', '2025-04-02 20:00:00', 11.00, 2.00, 'PRESENT', 'Overtime work', 'system', 'system', 0),
(3, '2025-04-03', '2025-04-03 09:00:00', '2025-04-03 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(4, '2025-04-01', '2025-04-01 09:00:00', '2025-04-01 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(4, '2025-04-02', '2025-04-02 09:00:00', '2025-04-02 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(4, '2025-04-03', '2025-04-03 09:00:00', '2025-04-03 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(5, '2025-04-01', '2025-04-01 09:00:00', '2025-04-01 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(5, '2025-04-02', '2025-04-02 09:00:00', '2025-04-02 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0),
(5, '2025-04-03', '2025-04-03 09:00:00', '2025-04-03 18:00:00', 9.00, 0.00, 'PRESENT', 'Regular day', 'system', 'system', 0);

-- Insert Employee Advance Headers (actual records from vsms-modern)
INSERT INTO trn_employee_advance_hdr (advance_no, advance_date, employee_id, department, purpose, amount, approved_by, approved_date, remarks, status, created_by, updated_by, version) VALUES
('ADV-2025-001', '2025-04-01', 1, 'Sales', 'Travel expenses for client meeting', 10000.00, 'Suresh Singh', '2025-04-02', 'Client meeting in Mumbai', 'APPROVED', 'system', 'system', 0),
('ADV-2025-002', '2025-04-05', 2, 'Service', 'Tools and equipment purchase', 15000.00, 'Suresh Singh', '2025-04-06', 'Service center tools', 'APPROVED', 'system', 'system', 0),
('ADV-2025-003', '2025-04-10', 3, 'Inventory', 'Warehouse equipment', 8000.00, 'Suresh Singh', '2025-04-11', 'Warehouse equipment purchase', 'APPROVED', 'system', 'system', 0);

-- Insert Employee Advance Details (actual records from vsms-modern)
INSERT INTO trn_employee_advance_dtl (advance_id, sl_no, expense_type, description, amount, receipt_no, receipt_date, created_by, updated_by) VALUES
(1, 1, 'Travel', 'Flight tickets Mumbai-Delhi', 5000.00, 'REC-001', '2025-04-03', 'system', 'system'),
(1, 2, 'Accommodation', 'Hotel stay 2 nights', 3000.00, 'REC-002', '2025-04-04', 'system', 'system'),
(1, 3, 'Food', 'Meals during travel', 2000.00, 'REC-003', '2025-04-05', 'system', 'system'),
(2, 1, 'Tools', 'Wrench set', 5000.00, 'REC-004', '2025-04-07', 'system', 'system'),
(2, 2, 'Equipment', 'Diagnostic scanner', 10000.00, 'REC-005', '2025-04-08', 'system', 'system'),
(3, 1, 'Equipment', 'Forklift rental', 5000.00, 'REC-006', '2025-04-12', 'system', 'system'),
(3, 2, 'Equipment', 'Pallet jack', 3000.00, 'REC-007', '2025-04-13', 'system', 'system');

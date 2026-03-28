-- DRS Module Tables
-- Based on vsms-modern schema

USE vsms_drs;

-- Daily Report Header table
CREATE TABLE IF NOT EXISTS trn_drs (
    hdr_id INT PRIMARY KEY AUTO_INCREMENT,
    cust_id BIGINT,
    oppor_size DECIMAL(15,2),
    close_date DATE,
    follow_plan VARCHAR(255),
    remarks VARCHAR(1000),
    visited_date DATE,
    visit_type VARCHAR(50),
    start_time DATETIME(6),
    end_time DATETIME(6),
    order_code VARCHAR(50),
    color VARCHAR(20),
    project_id BIGINT,
    call_id VARCHAR(50),
    job_type INT,
    user_type VARCHAR(50),
    sch_id BIGINT,
    created_by VARCHAR(100),
    created_on DATETIME(6),
    active VARCHAR(1) DEFAULT 'Y',
    followup_date DATETIME(6),
    INDEX idx_cust_id (cust_id),
    INDEX idx_visited_date (visited_date),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Daily Report Item table
CREATE TABLE IF NOT EXISTS trn_drs_item (
    dtl_id INT PRIMARY KEY AUTO_INCREMENT,
    hdr_id INT,
    item_id BIGINT,
    oem_id BIGINT,
    INDEX idx_hdr_id (hdr_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Daily Report Customer Visited table
CREATE TABLE IF NOT EXISTS trn_drs_cust_visited (
    vst_id INT PRIMARY KEY AUTO_INCREMENT,
    hdr_id INT,
    cust_id BIGINT,
    contact_id BIGINT,
    nm VARCHAR(255),
    mobile_no VARCHAR(20),
    email VARCHAR(100),
    INDEX idx_hdr_id (hdr_id),
    INDEX idx_cust_id (cust_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work Allocation table
CREATE TABLE IF NOT EXISTS trn_dly_wrk_sch (
    id INT PRIMARY KEY AUTO_INCREMENT,
    proj_id BIGINT,
    user_id VARCHAR(100),
    work_alot VARCHAR(500),
    st_date DATE,
    hrs_alot DECIMAL(10,2),
    hrs_taken DECIMAL(10,2),
    status_per VARCHAR(20),
    status VARCHAR(1) DEFAULT 'Y',
    end_date DATE,
    activity INT,
    INDEX idx_proj_id (proj_id),
    INDEX idx_user_id (user_id),
    INDEX idx_st_date (st_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample Data for DRS Module

-- Insert sample daily reports
INSERT INTO trn_drs (cust_id, oppor_size, close_date, follow_plan, remarks, visited_date, visit_type, start_time, end_time, order_code, color, project_id, call_id, job_type, user_type, created_by, created_on, active, followup_date) VALUES
(1, 50000.00, '2024-02-15', 'Follow up next week', 'Customer interested in premium package', '2024-01-15', 'Site Visit', '2024-01-15 09:00:00', '2024-01-15 11:00:00', 'ORD-001', 'Green', 1, 'CALL-001', 1, 'Sales', 'admin', NOW(), 'Y', '2024-01-22 10:00:00'),
(2, 75000.00, '2024-02-20', 'Send quotation', 'Customer needs detailed proposal', '2024-01-16', 'Office Visit', '2024-01-16 14:00:00', '2024-01-16 16:00:00', 'ORD-002', 'Yellow', 2, 'CALL-002', 2, 'Sales', 'admin', NOW(), 'Y', '2024-01-23 14:00:00'),
(3, 30000.00, '2024-02-10', 'Demo scheduled', 'Customer wants product demo', '2024-01-17', 'Demo', '2024-01-17 10:00:00', '2024-01-17 12:00:00', 'ORD-003', 'Red', 3, 'CALL-003', 3, 'Sales', 'admin', NOW(), 'Y', '2024-01-24 10:00:00'),
(4, 100000.00, '2024-03-01', 'Contract review', 'Large enterprise deal', '2024-01-18', 'Site Visit', '2024-01-18 09:00:00', '2024-01-18 17:00:00', 'ORD-004', 'Green', 4, 'CALL-004', 4, 'Sales', 'admin', NOW(), 'Y', '2024-01-25 09:00:00'),
(5, 25000.00, '2024-02-05', 'Quick follow up', 'Existing customer upgrade', '2024-01-19', 'Phone Call', '2024-01-19 11:00:00', '2024-01-19 11:30:00', 'ORD-005', 'Blue', 5, 'CALL-005', 5, 'Support', 'admin', NOW(), 'Y', '2024-01-26 11:00:00');

-- Insert sample DRS items
INSERT INTO trn_drs_item (hdr_id, item_id, oem_id) VALUES
(1, 1, 1),
(1, 2, 1),
(2, 3, 2),
(2, 4, 2),
(3, 5, 3),
(4, 1, 1),
(4, 3, 2),
(5, 2, 1);

-- Insert sample customers visited
INSERT INTO trn_drs_cust_visited (hdr_id, cust_id, contact_id, nm, mobile_no, email) VALUES
(1, 1, 1, 'John Doe', '9876543210', 'john@example.com'),
(1, 1, 2, 'Jane Doe', '9876543211', 'jane@example.com'),
(2, 2, 3, 'Bob Smith', '9876543212', 'bob@example.com'),
(3, 3, 4, 'Alice Johnson', '9876543213', 'alice@example.com'),
(4, 4, 5, 'Charlie Brown', '9876543214', 'charlie@example.com'),
(5, 5, 6, 'Diana Prince', '9876543215', 'diana@example.com');

-- Insert sample work allocations
INSERT INTO trn_dly_wrk_sch (proj_id, user_id, work_alot, st_date, hrs_alot, hrs_taken, status_per, status, end_date, activity) VALUES
(1, 'user1', 'Complete project documentation', '2024-01-15', 8.00, 6.50, '80%', 'Y', '2024-01-15', 1),
(1, 'user2', 'Review code changes', '2024-01-15', 4.00, 4.00, '100%', 'Y', '2024-01-15', 2),
(2, 'user1', 'Client meeting preparation', '2024-01-16', 3.00, 3.00, '100%', 'Y', '2024-01-16', 3),
(2, 'user3', 'Testing new features', '2024-01-16', 6.00, 5.00, '85%', 'Y', '2024-01-16', 4),
(3, 'user2', 'Bug fixes', '2024-01-17', 5.00, 5.00, '100%', 'Y', '2024-01-17', 5),
(3, 'user1', 'Deployment planning', '2024-01-17', 4.00, 3.50, '90%', 'Y', '2024-01-17', 6),
(4, 'user3', 'Performance optimization', '2024-01-18', 7.00, 6.00, '85%', 'Y', '2024-01-18', 7),
(4, 'user2', 'Security audit', '2024-01-18', 4.00, 4.00, '100%', 'Y', '2024-01-18', 8),
(5, 'user1', 'User training', '2024-01-19', 3.00, 3.00, '100%', 'Y', '2024-01-19', 9),
(5, 'user3', 'Support documentation', '2024-01-19', 5.00, 4.50, '90%', 'Y', '2024-01-19', 10);

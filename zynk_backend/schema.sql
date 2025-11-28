-- InternFlow HRMS Database Schema
-- MySQL Database Schema for Team Setup
-- Run this script to create all tables in your local MySQL database

-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS internflow;
USE internflow;

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS separation_requests;
DROP TABLE IF EXISTS leaves;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS intern_details;
DROP TABLE IF EXISTS users;

-- Table: users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('INTERN', 'HR') NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: intern_details
CREATE TABLE intern_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    joining_date DATE,
    internship_duration_months INT,
    stipend_type ENUM('MONTHLY', 'DAILY'),
    stipend_amount DOUBLE,
    manager_email VARCHAR(255),
    pan_number VARCHAR(255) UNIQUE,
    aadhaar_number VARCHAR(255) UNIQUE,
    bank_account_number VARCHAR(255) UNIQUE,
    bank_ifsc_code VARCHAR(255),
    bank_name VARCHAR(255),
    bank_branch VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    pincode VARCHAR(255),
    phone_number VARCHAR(255),
    signature_file_path VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_joining_date (joining_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: invoices
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intern_id BIGINT NOT NULL,
    invoice_number VARCHAR(255) NOT NULL UNIQUE,
    invoice_date DATE NOT NULL,
    billing_period_from DATE NOT NULL,
    billing_period_till DATE NOT NULL,
    total_working_days INT NOT NULL,
    paid_leaves INT NOT NULL DEFAULT 0,
    unpaid_leaves INT NOT NULL DEFAULT 0,
    stipend_amount DOUBLE NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'PAID') NOT NULL DEFAULT 'PENDING',
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (intern_id) REFERENCES intern_details(id) ON DELETE CASCADE,
    INDEX idx_intern_id (intern_id),
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_status (status),
    INDEX idx_invoice_date (invoice_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: leaves
CREATE TABLE leaves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intern_id BIGINT NOT NULL,
    leave_date DATE NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    leave_type ENUM('PAID', 'UNPAID') NOT NULL,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (intern_id) REFERENCES intern_details(id) ON DELETE CASCADE,
    INDEX idx_intern_id (intern_id),
    INDEX idx_leave_date (leave_date),
    INDEX idx_status (status),
    INDEX idx_leave_type (leave_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: announcements
CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    expiry_date DATE NOT NULL,
    created_by BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_created_by (created_by),
    INDEX idx_expiry_date (expiry_date),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: separation_requests
CREATE TABLE separation_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    intern_id BIGINT NOT NULL,
    requested_separation_date DATE NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    approved_by VARCHAR(255),
    approved_at TIMESTAMP NULL,
    hr_remarks VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (intern_id) REFERENCES intern_details(id) ON DELETE CASCADE,
    INDEX idx_intern_id (intern_id),
    INDEX idx_status (status),
    INDEX idx_requested_separation_date (requested_separation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Show tables created
SHOW TABLES;


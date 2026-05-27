-- MySQL Schema for Patta Transfer Web Application
-- This script outlines the database structure and can be run manually in MySQL WorkBench or command line.
-- Note: Spring Boot JPA will automatically create these tables on startup, but this file serves as a reference.

-- Create Database (if not exists)
CREATE DATABASE IF NOT EXISTS patta_transfer;
USE patta_transfer;

-- 1. Users Table
-- Stores credentials and profiles of applicants and administrative personnel.
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(15) NOT NULL,
    address VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Patta Applications Table
-- Stores individual land transfer requests and references to the physical files uploaded.
CREATE TABLE IF NOT EXISTS patta_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    applicant_name VARCHAR(100) NOT NULL,
    survey_number VARCHAR(50) NOT NULL,
    patta_number VARCHAR(50) NOT NULL,
    transfer_type VARCHAR(20) NOT NULL,    -- 'ISD' (Individual Sub Division) or 'NISD' (Non Sub Division)
    document_type VARCHAR(50) NOT NULL,    -- 'Sale Deed', 'Settlement Deed', 'Partition Deed', etc.
    property_document VARCHAR(255) NOT NULL,-- Filename of stored property document
    identity_proof VARCHAR(255) NOT NULL,   -- Filename of stored identity proof
    address_proof VARCHAR(255) NOT NULL,    -- Filename of stored address proof
    status VARCHAR(20) NOT NULL DEFAULT 'Pending', -- 'Pending', 'Approved', 'Rejected'
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Seed Seed Admin User
-- Inserts a default admin account if it does not already exist.
-- Email: admin@patta.com | Password: admin123 | Role: ADMIN
INSERT INTO users (name, mobile, address, email, password, role)
SELECT 'System Administrator', '9999999999', 'Admin Office, Patta Department', 'admin@patta.com', 'admin123', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@patta.com');

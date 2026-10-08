-- ==========================================================
-- MediConnect Database Schema & Initial Data
-- Database: MySQL 8.0+
-- ==========================================================

CREATE DATABASE IF NOT EXISTS mediconnect;
USE mediconnect;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    age INT,
    gender VARCHAR(255),
    role VARCHAR(255) DEFAULT 'PATIENT'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    specialty VARCHAR(255) NOT NULL,
    hospital VARCHAR(255) NOT NULL,
    experience VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor VARCHAR(255) NOT NULL,
    specialty VARCHAR(255),
    hospital VARCHAR(255),
    date VARCHAR(255) NOT NULL,
    time VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'Confirmed',
    patient_name VARCHAR(255),
    patient_email VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Medical Records Table
CREATE TABLE IF NOT EXISTS medical_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_name VARCHAR(255),
    patient_email VARCHAR(255),
    record_source VARCHAR(32),
    hospital VARCHAR(255),
    doctor VARCHAR(255),
    disease VARCHAR(255),
    medicines VARCHAR(255),
    visit_date VARCHAR(255),
    notes VARCHAR(1000),
    INDEX idx_medical_record_patient_email (patient_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Patient ownership is required for authenticated records and consent-based QR sharing.
-- Existing records are intentionally not assigned to accounts automatically; link them
-- only after verifying the patient's identity and account email.
-- Bearer sessions and QR shares persist hashes only; raw secrets are returned once.
CREATE TABLE IF NOT EXISTS auth_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_hash CHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_auth_sessions_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS medical_record_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_hash CHAR(64) NOT NULL UNIQUE,
    patient_email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_medical_record_share_patient_email (patient_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- Initial Sample Seed Data (Safe Insert)
-- ==========================================================

INSERT INTO doctors (id, name, specialty, hospital, experience)
SELECT 1, 'Dr. Anjali Sharma', 'Cardiologist', 'Apollo Hospital', '10 years'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE id = 1);

INSERT INTO doctors (id, name, specialty, hospital, experience)
SELECT 2, 'Dr. Rahul Kumar', 'Dermatologist', 'Yashoda Hospital', '8 years'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE id = 2);

INSERT INTO doctors (id, name, specialty, hospital, experience)
SELECT 3, 'Dr. Priya Reddy', 'General Physician', 'KIMS Hospital', '7 years'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE id = 3);

INSERT INTO doctors (id, name, specialty, hospital, experience)
SELECT 4, 'Dr. Arjun Rao', 'Neurologist', 'CARE Hospital', '12 years'
WHERE NOT EXISTS (SELECT 1 FROM doctors WHERE id = 4);

INSERT INTO users (id, name, email, password, phone, age, gender, role)
SELECT 1, 'Siri', 'siri@example.com', 'password123', '9876543210', 24, 'Female', 'PATIENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 1);

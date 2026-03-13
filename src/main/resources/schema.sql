-- ============================================================
-- AI-Driven Healthcare Intelligence Platform
-- Full MySQL Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS medcare_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE medcare_db;

-- ============================================================
-- ROLES
-- ============================================================
CREATE TABLE roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role_name (name)
);

-- ============================================================
-- USERS (Doctors & Admins)
-- ============================================================
CREATE TABLE users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name      VARCHAR(150) NOT NULL,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    phone          VARCHAR(20),
    specialization VARCHAR(100),
    hospital_id    BIGINT,
    role_id        BIGINT NOT NULL,
    is_active      BOOLEAN DEFAULT TRUE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role_id)
);

-- ============================================================
-- HOSPITALS
-- ============================================================
CREATE TABLE hospitals (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    address      TEXT,
    city         VARCHAR(100),
    phone        VARCHAR(20),
    total_beds   INT DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_hospital_city (city)
);

-- ============================================================
-- PATIENTS
-- ============================================================
CREATE TABLE patients (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_code    VARCHAR(20) NOT NULL UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    date_of_birth   DATE NOT NULL,
    gender          ENUM('MALE','FEMALE','OTHER') NOT NULL,
    blood_group     VARCHAR(5),
    phone           VARCHAR(20),
    address         TEXT,
    emergency_contact VARCHAR(150),
    emergency_phone   VARCHAR(20),
    doctor_id       BIGINT,
    hospital_id     BIGINT,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_code (patient_code),
    INDEX idx_patient_doctor (doctor_id),
    INDEX idx_patient_hospital (hospital_id),
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL
);

-- ============================================================
-- SYMPTOMS
-- ============================================================
CREATE TABLE symptoms (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id   BIGINT NOT NULL,
    symptom_name VARCHAR(100) NOT NULL,
    severity     ENUM('MILD','MODERATE','SEVERE') DEFAULT 'MILD',
    duration_days INT,
    notes        TEXT,
    recorded_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by  BIGINT,
    INDEX idx_symptom_patient (patient_id),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- VITALS
-- ============================================================
CREATE TABLE vitals (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id        BIGINT NOT NULL,
    temperature       DECIMAL(5,2),
    systolic_bp       INT,
    diastolic_bp      INT,
    heart_rate        INT,
    oxygen_saturation DECIMAL(5,2),
    respiratory_rate  INT,
    weight_kg         DECIMAL(6,2),
    height_cm         DECIMAL(6,2),
    recorded_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    recorded_by       BIGINT,
    INDEX idx_vitals_patient (patient_id),
    INDEX idx_vitals_recorded (recorded_at),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- LAB REPORTS
-- ============================================================
CREATE TABLE lab_reports (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id     BIGINT NOT NULL,
    report_type    VARCHAR(100) NOT NULL,
    hemoglobin     DECIMAL(5,2),
    platelet_count INT,
    wbc_count      DECIMAL(8,2),
    rbc_count      DECIMAL(5,2),
    blood_sugar    DECIMAL(6,2),
    creatinine     DECIMAL(5,2),
    bilirubin      DECIMAL(5,2),
    alt            DECIMAL(6,2),
    ast            DECIMAL(6,2),
    report_date    DATE NOT NULL,
    lab_name       VARCHAR(150),
    notes          TEXT,
    uploaded_by    BIGINT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_lab_patient (patient_id),
    INDEX idx_lab_date (report_date),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- MEDICAL RECORDS
-- ============================================================
CREATE TABLE medical_records (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id    BIGINT NOT NULL,
    record_type   VARCHAR(100) NOT NULL,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    diagnosis     TEXT,
    treatment     TEXT,
    medications   TEXT,
    allergies     TEXT,
    file_path     VARCHAR(500),
    visit_date    DATE NOT NULL,
    doctor_id     BIGINT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_medrecord_patient (patient_id),
    INDEX idx_medrecord_visit (visit_date),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- DIAGNOSTIC ANALYSES
-- ============================================================
CREATE TABLE diagnostic_analyses (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    risk_score          DECIMAL(5,2) NOT NULL,
    risk_level          ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
    suspected_conditions TEXT,
    diagnostic_alerts   TEXT,
    recommendations     TEXT,
    analyzed_by         BIGINT,
    analyzed_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_diag_patient (patient_id),
    INDEX idx_diag_risk (risk_level),
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (analyzed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- ICU BEDS
-- ============================================================
CREATE TABLE icu_beds (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id     BIGINT NOT NULL,
    total_beds      INT NOT NULL DEFAULT 0,
    occupied_beds   INT NOT NULL DEFAULT 0,
    available_beds  INT GENERATED ALWAYS AS (total_beds - occupied_beds) STORED,
    utilization_pct DECIMAL(5,2) GENERATED ALWAYS AS (
        CASE WHEN total_beds > 0 THEN (occupied_beds / total_beds) * 100 ELSE 0 END
    ) STORED,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      BIGINT,
    INDEX idx_icu_hospital (hospital_id),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- STAFF
-- ============================================================
CREATE TABLE staff (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id     BIGINT NOT NULL,
    department      VARCHAR(100) NOT NULL,
    total_staff     INT NOT NULL DEFAULT 0,
    on_duty_staff   INT NOT NULL DEFAULT 0,
    workload_pct    DECIMAL(5,2) DEFAULT 0.0,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_staff_hospital (hospital_id),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE
);

-- ============================================================
-- EQUIPMENT
-- ============================================================
CREATE TABLE equipment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id     BIGINT NOT NULL,
    equipment_name  VARCHAR(150) NOT NULL,
    total_units     INT NOT NULL DEFAULT 0,
    in_use_units    INT NOT NULL DEFAULT 0,
    demand_level    ENUM('LOW','MEDIUM','HIGH','CRITICAL') DEFAULT 'LOW',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equip_hospital (hospital_id),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE
);

-- ============================================================
-- RESOURCE FORECASTS
-- ============================================================
CREATE TABLE resource_forecasts (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_id          BIGINT NOT NULL,
    stress_level         ENUM('NORMAL','ELEVATED','HIGH','CRITICAL') NOT NULL,
    icu_stress_score     DECIMAL(5,2),
    staff_stress_score   DECIMAL(5,2),
    equipment_stress_score DECIMAL(5,2),
    overall_stress_score DECIMAL(5,2),
    alerts               TEXT,
    recommendations      TEXT,
    forecast_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    generated_by         BIGINT,
    INDEX idx_forecast_hospital (hospital_id),
    INDEX idx_forecast_stress (stress_level),
    INDEX idx_forecast_date (forecast_date),
    FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
    FOREIGN KEY (generated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- ADD FK from users → hospitals (after hospitals table exists)
-- ============================================================
ALTER TABLE users
    ADD CONSTRAINT fk_user_hospital FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_user_role     FOREIGN KEY (role_id)     REFERENCES roles(id);

-- ============================================================
-- SEED DATA
-- ============================================================
INSERT INTO roles (name, description) VALUES
    ('ROLE_DOCTOR', 'Medical doctor with patient access'),
    ('ROLE_ADMIN',  'Hospital administrator with full access');

INSERT INTO hospitals (name, address, city, phone, total_beds) VALUES
    ('City General Hospital', '123 Main Street', 'New York', '+1-555-0100', 500),
    ('Metro Health Center',   '456 Park Avenue', 'Chicago',  '+1-555-0200', 300);

INSERT INTO icu_beds (hospital_id, total_beds, occupied_beds) VALUES
    (1, 50, 35),
    (2, 30, 28);

INSERT INTO staff (hospital_id, department, total_staff, on_duty_staff, workload_pct) VALUES
    (1, 'ICU',       20, 18, 90.0),
    (1, 'Emergency', 30, 22, 73.3),
    (2, 'ICU',       15, 10, 66.7);

INSERT INTO equipment (hospital_id, equipment_name, total_units, in_use_units, demand_level) VALUES
    (1, 'Ventilator',    20, 17, 'HIGH'),
    (1, 'ECG Machine',   10, 6,  'MEDIUM'),
    (2, 'Ventilator',    12, 11, 'CRITICAL');

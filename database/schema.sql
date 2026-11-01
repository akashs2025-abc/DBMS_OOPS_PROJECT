CREATE DATABASE IF NOT EXISTS job_tracker;
USE job_tracker;

CREATE TABLE user_details (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    normalized_name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE companies (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL,
    role_offered VARCHAR(100),
    contact_email VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    company_id INT NOT NULL,
    role VARCHAR(100) NOT NULL,
    type ENUM('Job', 'Internship') NOT NULL,
    applied_date DATE NOT NULL,
    status ENUM('Applied', 'Shortlisted', 'Interview Scheduled', 'Offered', 'Rejected') NOT NULL DEFAULT 'Applied',
    status_revision BIGINT UNSIGNED NOT NULL DEFAULT 0,
    follow_up_date DATE NULL,
    CONSTRAINT fk_app_user
        FOREIGN KEY (user_id) REFERENCES user_details(user_id),
    CONSTRAINT fk_app_company
        FOREIGN KEY (company_id) REFERENCES companies(company_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_applications_user_id ON applications(user_id);

CREATE TABLE interviews (
    interview_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    interview_date DATE NOT NULL,
    round ENUM('Aptitude', 'Technical', 'HR') NOT NULL,
    result ENUM('Pending', 'Cleared', 'Rejected') NOT NULL DEFAULT 'Pending',
    CONSTRAINT fk_interview_app
        FOREIGN KEY (application_id) REFERENCES applications(application_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE offers (
    offer_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    package DECIMAL(10,2) NOT NULL CHECK (package > 0),
    deadline DATE NOT NULL,
    offer_status ENUM('Pending', 'Accepted', 'Rejected') NOT NULL DEFAULT 'Pending',
    previous_application_status ENUM('Applied', 'Shortlisted', 'Interview Scheduled', 'Offered', 'Rejected') NULL,
    offered_status_revision BIGINT UNSIGNED NULL,
    CONSTRAINT fk_offer_app
        FOREIGN KEY (application_id) REFERENCES applications(application_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE OR REPLACE VIEW view_application_details AS
SELECT
    a.application_id,
    c.company_id,
    c.name AS company_name,
    c.location AS company_location,
    a.role,
    a.type,
    a.applied_date,
    a.status,
    COALESCE(a.follow_up_date, 'Not set') AS follow_up_status,
    o.package AS offer_package,
    o.offer_status
FROM applications a
INNER JOIN companies c ON a.company_id = c.company_id
LEFT JOIN offers o ON a.application_id = o.application_id;

CREATE OR REPLACE VIEW view_status_summary AS
SELECT
    status,
    COUNT(*) AS total_count
FROM applications
GROUP BY status;

CREATE OR REPLACE VIEW view_upcoming_followups AS
SELECT
    a.application_id,
    c.name AS company_name,
    a.role,
    a.follow_up_date
FROM applications a
INNER JOIN companies c ON a.company_id = c.company_id
WHERE a.follow_up_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
ORDER BY a.follow_up_date ASC;

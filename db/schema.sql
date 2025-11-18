-- ==========================================
-- DATABASE SCHEMA SCRIPT
-- ==========================================

-- 1. Drop old databases (in the correct order to avoid FK issues)
DROP DATABASE IF EXISTS erp_db;
DROP DATABASE IF EXISTS auth_db;

-- 2. Create the databases
CREATE DATABASE auth_db;
CREATE DATABASE erp_db;

-- 3. Create 'auth_db' tables
USE auth_db;
CREATE TABLE users_auth (
    user_id INT NOT NULL AUTO_INCREMENT,
    login_name VARCHAR(100) NOT NULL UNIQUE,
    user_pass_hash VARCHAR(255) NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id)
);

-- 4. Create 'erp_db' tables
USE erp_db;

CREATE TABLE students (
    student_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    program VARCHAR(100),
    PRIMARY KEY (student_id),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) ON DELETE CASCADE
);

CREATE TABLE instructors (
    instructor_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    full_name VARCHAR(255),
    department VARCHAR(100),
    PRIMARY KEY (instructor_id),
    FOREIGN KEY (user_id) REFERENCES auth_db.users_auth(user_id) ON DELETE CASCADE
);

CREATE TABLE courses (
    course_id INT NOT NULL AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    credits INT NOT NULL,
    PRIMARY KEY (course_id)
);

CREATE TABLE sections (
    section_id INT NOT NULL AUTO_INCREMENT,
    course_id INT NOT NULL,
    instructor_id INT,
    day_time VARCHAR(100),
    room VARCHAR(50),
    capacity INT NOT NULL,
    semester VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    PRIMARY KEY (section_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (instructor_id) REFERENCES instructors(instructor_id)
);

CREATE TABLE enrollments (
    enrollment_id INT NOT NULL AUTO_INCREMENT,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'enrolled',
    PRIMARY KEY (enrollment_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id),
    UNIQUE KEY uk_student_section (student_id, section_id)
);

CREATE TABLE grades (
    grade_id INT NOT NULL AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    score DECIMAL(5, 2),
    final_grade VARCHAR(2),
    PRIMARY KEY (grade_id),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id)
);

CREATE TABLE settings (
    setting_id INT NOT NULL AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value VARCHAR(100),
    PRIMARY KEY (setting_id)
);

-- Initial Settings
INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_mode', 'false');
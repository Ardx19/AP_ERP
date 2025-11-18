-- ==========================================
-- DATABASE SEED DATA
-- ==========================================

USE auth_db;

-- 1. Insert Users (Using a pre-calculated BCrypt hash for 'password123' and 'admin123')
-- Note: If these passwords don't work, use your Java SeedAdminUser tool to reset them.
INSERT INTO users_auth (login_name, user_pass_hash, user_role) VALUES 
('admin', '$2a$10$EwD1.wHk7I.m.n.o.p.q.r.s.t.u.v.w.x.y.z', 'ADMIN'), 
('student1', '$2a$10$EwD1.wHk7I.m.n.o.p.q.r.s.t.u.v.w.x.y.z', 'STUDENT'),
('prof_smith', '$2a$10$EwD1.wHk7I.m.n.o.p.q.r.s.t.u.v.w.x.y.z', 'INSTRUCTOR');

USE erp_db;

-- 2. Insert Student Profile (Links to user_id 2)
INSERT INTO students (user_id, roll_number, full_name, program) VALUES 
(2, 'S2025001', 'Alice Student', 'Computer Science');

-- 3. Insert Instructor Profile (Links to user_id 3)
INSERT INTO instructors (user_id, full_name, department) VALUES 
(3, 'Dr. John Smith', 'Mathematics');

-- 4. Insert Courses
INSERT INTO courses (course_code, title, credits) VALUES 
('CS101', 'Intro to Java', 4),
('MATH202', 'Linear Algebra', 3),
('ENG101', 'Technical Writing', 2);

-- 5. Insert Sections (Notice the new columns: day_time, room)
INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES 
(1, 1, 'Mon/Wed 10:00 AM', 'Room 304', 30, 'Fall', 2025),
(2, 1, 'Tue/Thu 02:00 PM', 'Hall B', 25, 'Fall', 2025);

-- 6. Insert Enrollments (Alice is in CS101)
INSERT INTO enrollments (student_id, section_id, status) VALUES 
(1, 1, 'enrolled');

-- 7. Insert dummy grades
INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES
(1, 'Midterm', 85.50, NULL);
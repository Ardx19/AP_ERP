package edu.univ.erp.auth;

import edu.univ.erp.model.UserDisplay; 
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.model.Course;
import edu.univ.erp.model.InstructorDisplay;
import edu.univ.erp.model.SectionDisplay;
import edu.univ.erp.model.StudentProfile;
import edu.univ.erp.model.InstructorProfile;

public class AdminService {

    /**
     * Fetches all users from the auth_db to be displayed in a table.
     * @return A list of UserDisplay objects.
     */
    public List<UserDisplay> getAllUsers() {
        List<UserDisplay> users = new ArrayList<>();
        String sql = "SELECT user_id, login_name, user_role FROM users_auth";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserDisplay user = new UserDisplay(
                    rs.getInt("user_id"),
                    rs.getString("login_name"),
                    rs.getString("user_role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Checks if a login_name is already in use.
     * @param loginName The name to check.
     * @return true if the name is taken, false otherwise.
     */
    public boolean isLoginNameTaken(String loginName) {
        String sql = "SELECT COUNT(*) FROM users_auth WHERE login_name = ?";
        
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, loginName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /**
     * Creates a new user in BOTH auth_db and erp_db.
     * This is a transaction.
     * @param loginName The new user's login name
     * @param plainPassword The new user's plain password
     * @param role The new user's role
     * @return true if creation is successful, false otherwise
     */
    /**
     * Creates a new user. This version is simpler and does not use 
     * setAutoCommit(false), avoiding lock errors. It inserts into auth_db,
     * then tries to insert into erp_db. If the second step fails,
     * it deletes the first user (a "compensating transaction").
     */
    public boolean createNewUser(String loginName, String plainPassword, String role) {
        
        // 1. Hash the password
        String salt = BCrypt.gensalt(12);
        String passwordHash = BCrypt.hashpw(plainPassword, salt);
        
        String authSql = "INSERT INTO users_auth (login_name, user_pass_hash, user_role) VALUES (?, ?, ?)";
        int newUserId = -1;

        // --- STEP 1: Insert into auth_db (with auto-commit) ---
        // We use try-with-resources to auto-close connections/statements
        try (Connection authConn = DatabaseConnection.getAuthConnection();
             PreparedStatement authPstmt = authConn.prepareStatement(authSql, 
                                             PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            authPstmt.setString(1, loginName);
            authPstmt.setString(2, passwordHash);
            authPstmt.setString(3, role);
            authPstmt.executeUpdate();

            // Get the new user_id that was auto-generated
            try (ResultSet generatedKeys = authPstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newUserId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            // If this fails, the user was not created. Stop.
            System.err.println("Error on Step 1 (Auth DB): " + e.getMessage());
            return false; 
        }

        // If we are here, newUserId > 0 and the auth user was created.

        // --- STEP 2: Insert into erp_db (if student or instructor) ---
        if (role.equals("student")) {
            String erpSql = "INSERT INTO students (user_id, roll_number) VALUES (?, ?)";
            try (Connection erpConn = DatabaseConnection.getErpConnection();
                 PreparedStatement erpPstmt = erpConn.prepareStatement(erpSql)) {
                
                erpPstmt.setInt(1, newUserId);
                erpPstmt.setString(2, loginName); // Default roll number
                erpPstmt.executeUpdate();
                
            } catch (SQLException e) {
                System.err.println("Error on Step 2 (Student DB): " + e.getMessage());
                // --- COMPENSATING TRANSACTION ---
                // Step 2 failed, so we must delete the user from Step 1.
                System.err.println("Rolling back by deleting orphan auth user...");
                deleteUser(newUserId, role); // Use our existing delete method
                return false;
            }
        } else if (role.equals("instructor")) {
            String erpSql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";
            try (Connection erpConn = DatabaseConnection.getErpConnection();
                 PreparedStatement erpPstmt = erpConn.prepareStatement(erpSql)) {
                
                erpPstmt.setInt(1, newUserId);
                erpPstmt.setString(2, "Default"); // Default department
                erpPstmt.executeUpdate();

            } catch (SQLException e) {
                System.err.println("Error on Step 2 (Instructor DB): " + e.getMessage());
                // --- COMPENSATING TRANSACTION ---
                System.err.println("Rolling back by deleting orphan auth user...");
                deleteUser(newUserId, role); 
                return false;
            }
        }
        
        // If we reach here, both steps were successful (or it was just an admin)
        return true;
    }

    /**
     * Deletes a user from both the erp_db and auth_db.
     * @param userId The ID of the user to delete.
     * @param role The role of the user (to know which erp_db table to check).
     * @return true if deletion is successful, false otherwise.
     */
    public boolean deleteUser(int userId, String role) {
        
        // The ON DELETE CASCADE in the SQL setup handles this.
        // We only need to delete from the 'auth_db', and the
        // foreign keys will automatically delete the 'students'
        // or 'instructors' row.
        
        String sql = "DELETE FROM auth_db.users_auth WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    // --- Course Management Methods ---

    /**
     * Fetches all courses from the erp_db.
     * @return A list of Course objects.
     */
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_id, course_code, title, credits FROM erp_db.courses";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getInt("credits")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    /**
     * Creates a new course in the erp_db.
     * @param newCourse A Course object (without an ID)
     * @return true if successful, false otherwise.
     */
    public boolean createNewCourse(Course newCourse) {
        String sql = "INSERT INTO erp_db.courses (course_code, title, credits) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newCourse.getCourseCode());
            pstmt.setString(2, newCourse.getTitle());
            pstmt.setInt(3, newCourse.getCredits());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error creating new course: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a course from the erp_db.
     * @param courseId The ID of the course to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteCourse(int courseId) {
        // Note: The database's ON DELETE CASCADE will handle enrollments/sections
        // But, we should add a check here in a real app to prevent deleting
        // a course with active sections. For now, we'll keep it simple.
        
        String sql = "DELETE FROM erp_db.courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            // This will fail if a section is still linked (foreign key)
            return false;
        }
    }
    // --- Instructor Methods ---

    /**
     * Fetches all instructors from the erp_db for dropdowns.
     * @return A list of InstructorDisplay objects.
     */
    public List<InstructorDisplay> getAllInstructors() {
        List<InstructorDisplay> instructors = new ArrayList<>();
        // We get full_name from the instructors table
        String sql = "SELECT instructor_id, full_name, department FROM erp_db.instructors";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                InstructorDisplay instructor = new InstructorDisplay(
                    rs.getInt("instructor_id"),
                    rs.getString("full_name"),
                    rs.getString("department")
                );
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    // --- Section Management Methods ---

    /**
     * Fetches all sections with course and instructor names. (Complex JOIN)
     * @return A list of SectionDisplay objects.
     */
    public List<SectionDisplay> getAllSections() {
        List<SectionDisplay> sections = new ArrayList<>();
        
        // This SQL joins 4 tables to get all the info we need
        String sql = "SELECT s.section_id, c.course_code, c.title, i.full_name, " +
                     "s.semester, s.year, s.capacity " +
                     "FROM erp_db.sections s " +
                     "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id " +
                     "ORDER BY s.year DESC, s.semester, c.course_code";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                sections.add(new SectionDisplay(
                    rs.getInt("section_id"),
                    rs.getString("course_code"),
                    rs.getString("title"),
                    rs.getString("full_name"), // Can be null
                    rs.getString("semester"),
                    rs.getInt("year"),
                    rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    /**
     * Creates a new section in the erp_db.
     * @param courseId The ID of the course.
     * @param instructorId The ID of the instructor (can be null).
     * @param semester E.g., "Fall"
     * @param year E.g., 2025
     * @param capacity The number of seats.
     * @return true if successful, false otherwise.
     */
    public boolean createNewSection(int courseId, Integer instructorId, String semester, int year, int capacity) {
        String sql = "INSERT INTO erp_db.sections (course_id, instructor_id, semester, year, capacity) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            
            // Handle optional instructor
            if (instructorId != null) {
                pstmt.setInt(2, instructorId);
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);
            pstmt.setInt(5, capacity);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error creating new section: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a section from the erp_db.
     * @param sectionId The ID of the section to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM erp_db.sections WHERE section_id = ?";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            // This will fail if enrollments are linked (foreign key)
            System.err.println("Error deleting section: " + e.getMessage());
            return false;
        }
    }
    // --- User Profile Getters (for the edit dialog) ---

    public StudentProfile getStudentProfile(int userId) {
        StudentProfile profile = null;
        String sql = "SELECT student_id, user_id, roll_number, full_name, program " +
                     "FROM erp_db.students WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    profile = new StudentProfile(
                        rs.getInt("student_id"), rs.getInt("user_id"),
                        rs.getString("roll_number"), rs.getString("full_name"),
                        rs.getString("program")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return profile;
    }

    public InstructorProfile getInstructorProfile(int userId) {
        InstructorProfile profile = null;
        String sql = "SELECT instructor_id, user_id, full_name, department " +
                     "FROM erp_db.instructors WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    profile = new InstructorProfile(
                        rs.getInt("instructor_id"), rs.getInt("user_id"),
                        rs.getString("full_name"), rs.getString("department")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return profile;
    }

    // --- User Profile Updaters ---

    public boolean updateStudentProfile(int studentId, String newName, String newProgram) {
        String sql = "UPDATE erp_db.students SET full_name = ?, program = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newProgram);
            pstmt.setInt(3, studentId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInstructorProfile(int instructorId, String newName, String newDept) {
        String sql = "UPDATE erp_db.instructors SET full_name = ?, department = ? WHERE instructor_id = ?";
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newDept);
            pstmt.setInt(3, instructorId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Password Reset Method ---

    public boolean resetUserPassword(int userId, String newPlainPassword) {
        // Hash the new password
        String salt = BCrypt.gensalt(12);
        String passwordHash = BCrypt.hashpw(newPlainPassword, salt);
        
        String sql = "UPDATE auth_db.users_auth SET user_pass_hash = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, passwordHash);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return false;
        }
    }
}

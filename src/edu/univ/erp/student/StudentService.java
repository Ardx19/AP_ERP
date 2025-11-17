package edu.univ.erp.student;

import edu.univ.erp.auth.DatabaseConnection;
import edu.univ.erp.model.StudentProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.univ.erp.model.EnrolledCourseDisplay;
import edu.univ.erp.model.SectionDisplay; // We re-use this from the admin
import edu.univ.erp.model.GradeDisplay;
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.model.FeeItem;
import java.math.BigDecimal;
import java.sql.Date;

public class StudentService {

    /**
     * Fetches a student's profile from the erp_db using their user_id.
     * @param userId The user_id from the auth_db.
     * @return A StudentProfile object, or null if not found.
     */
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
                        rs.getInt("student_id"),
                        rs.getInt("user_id"),
                        rs.getString("roll_number"),
                        rs.getString("full_name"),
                        rs.getString("program")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }
    /**
     * Updates a student's profile information in the erp_db.
     * @param studentId The student_id (from the profile object)
     * @param newFullName The new full name
     * @param newProgram The new program
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateStudentProfile(int studentId, String newFullName, String newProgram) {
        String sql = "UPDATE erp_db.students SET full_name = ?, program = ? WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newFullName);
            pstmt.setString(2, newProgram);
            pstmt.setInt(3, studentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1; // Success if 1 row was changed

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Fetches all sections the student is *currently* enrolled in.
     * @param studentId The student's ID (from erp_db.students)
     * @return A list of EnrolledCourseDisplay objects.
     */
    public List<EnrolledCourseDisplay> getEnrolledCourses(int studentId) {
        List<EnrolledCourseDisplay> enrolledCourses = new ArrayList<>();
        
        // This query joins 5 tables!
        String sql = "SELECT e.enrollment_id, s.section_id, c.course_code, c.title, i.full_name, e.status " +
                     "FROM erp_db.enrollments e " +
                     "JOIN erp_db.sections s ON e.section_id = s.section_id " +
                     "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id " +
                     "WHERE e.student_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrolledCourses.add(new EnrolledCourseDisplay(
                        rs.getInt("enrollment_id"),
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("full_name"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrolledCourses;
    }

    /**
     * Fetches all available sections a student is *not* already enrolled in.
     * @param studentId The student's ID (from erp_db.students)
     * @return A list of SectionDisplay objects.
     */
    public List<SectionDisplay> getAvailableSections(int studentId) {
        List<SectionDisplay> sections = new ArrayList<>();
        
        // This is a complex query that finds sections the student is NOT in
        String sql = "SELECT s.section_id, c.course_code, c.title, i.full_name, " +
                     "s.semester, s.year, s.capacity " +
                     "FROM erp_db.sections s " +
                     "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN erp_db.instructors i ON s.instructor_id = i.instructor_id " +
                     "WHERE s.section_id NOT IN ( " +
                     "    SELECT e.section_id FROM erp_db.enrollments e WHERE e.student_id = ? " +
                     ")";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId); // Set the studentId for the subquery
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(new SectionDisplay(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("full_name"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getInt("capacity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    /**
     * Enrolls a student into a specific section.
     * @param studentId The student's ID.
     * @param sectionId The section's ID.
     * @return true if successful, false otherwise.
     */
    public boolean enrollInSection(int studentId, int sectionId) {
        String sql = "INSERT INTO erp_db.enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, sectionId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            // This will fail if they try to enroll in the same class twice (unique key)
            System.err.println("Error enrolling in section: " + e.getMessage());
            return false;
        }
    }

    /**
     * Drops a student from a course.
     * @param enrollmentId The ID of the enrollment record to delete.
     * @return true if successful, false otherwise.
     */
    public boolean dropEnrolledSection(int enrollmentId) {
        String sql = "DELETE FROM erp_db.enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollmentId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1;

        } catch (SQLException e) {
            System.err.println("Error dropping section: " + e.getMessage());
            return false;
        }
    }
    /**
     * Fetches all grades for a specific student.
     * @param studentId The student's ID (from erp_db.students)
     * @return A list of GradeDisplay objects.
     */
    public List<GradeDisplay> getGrades(int studentId) {
        List<GradeDisplay> grades = new ArrayList<>();
        
        // This query joins 4 tables to get all the info
        String sql = "SELECT c.course_code, c.title, g.component, g.score, g.final_grade " +
                     "FROM erp_db.grades g " +
                     "JOIN erp_db.enrollments e ON g.enrollment_id = e.enrollment_id " +
                     "JOIN erp_db.sections s ON e.section_id = s.section_id " +
                     "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                     "WHERE e.student_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(new GradeDisplay(
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getString("component"),
                        rs.getDouble("score"),
                        rs.getString("final_grade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }
    /**
     * Fetches all fee items for a specific student.
     * @param studentId The student's ID (from erp_db.students)
     * @return A list of FeeItem objects.
     */
    public List<FeeItem> getFeeItems(int studentId) {
        List<FeeItem> fees = new ArrayList<>();
        String sql = "SELECT fee_id, item_description, amount, status, due_date " +
                     "FROM erp_db.fees WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fees.add(new FeeItem(
                        rs.getInt("fee_id"),
                        rs.getString("item_description"),
                        rs.getBigDecimal("amount"),
                        rs.getString("status"),
                        rs.getDate("due_date")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fees;
    }

    /**
     * "Pays" a fee item by updating its status.
     * @param feeId The ID of the fee to pay.
     * @return true if successful, false otherwise.
     */
    public boolean payFeeItem(int feeId) {
        String sql = "UPDATE erp_db.fees SET status = 'Paid' WHERE fee_id = ? AND status = 'Pending'";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, feeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected == 1; // Success if 1 row was changed

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- TODO ---
    // Later we will add more methods here:
    // public List<EnrolledCourse> getEnrolledCourses(int studentId)
    // public List<Grade> getGrades(int studentId)
}
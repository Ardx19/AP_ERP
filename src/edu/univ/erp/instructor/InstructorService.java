package edu.univ.erp.instructor;

import edu.univ.erp.auth.DatabaseConnection;
import edu.univ.erp.model.InstructorProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import edu.univ.erp.model.SectionDisplay; // Re-use the model from admin
import java.util.ArrayList;
import java.util.List;
import edu.univ.erp.model.StudentRosterDisplay;

public class InstructorService {

    /**
     * Fetches an instructor's profile from the erp_db using their user_id.
     * @param userId The user_id from the auth_db.
     * @return An InstructorProfile object, or null if not found.
     */
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
                        rs.getInt("instructor_id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("department")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }
    /**
     * Fetches all sections assigned to a specific instructor.
     * @param instructorId The instructor's ID (from erp_db.instructors)
     * @return A list of SectionDisplay objects.
     */
    public List<SectionDisplay> getAssignedSections(int instructorId) {
        List<SectionDisplay> sections = new ArrayList<>();
        
        // This query joins sections and courses
        String sql = "SELECT s.section_id, c.course_code, c.title, " +
                     "s.semester, s.year, s.capacity " +
                     "FROM erp_db.sections s " +
                     "JOIN erp_db.courses c ON s.course_id = c.course_id " +
                     "WHERE s.instructor_id = ? " +
                     "ORDER BY s.year DESC, s.semester, c.course_code";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, instructorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(new SectionDisplay(
                        rs.getInt("section_id"),
                        rs.getString("course_code"),
                        rs.getString("title"),
                        null, // We don't need instructor name, we are the instructor
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
     * Fetches the roster of students for a given section, including their current grade.
     * @param sectionId The section's ID.
     * @return A list of StudentRosterDisplay objects.
     */
    public List<StudentRosterDisplay> getSectionRoster(int sectionId) {
        List<StudentRosterDisplay> roster = new ArrayList<>();
        
        // This query joins students, enrollments, and (LEFT JOIN) grades
        String sql = "SELECT e.enrollment_id, s.student_id, s.full_name, s.roll_number, " +
                     "g.component, g.score, g.final_grade " +
                     "FROM erp_db.enrollments e " +
                     "JOIN erp_db.students s ON e.student_id = s.student_id " +
                     "LEFT JOIN erp_db.grades g ON e.enrollment_id = g.enrollment_id " +
                     "WHERE e.section_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, sectionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    roster.add(new StudentRosterDisplay(
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getString("full_name"),
                        rs.getString("roll_number"),
                        rs.getString("component"),
                        rs.getDouble("score"), // Will be 0.0 if NULL
                        rs.getString("final_grade") // Will be null if NULL
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roster;
    }

    /**
     * Submits or updates a grade for a student.
     * Uses ON DUPLICATE KEY UPDATE to either INSERT or UPDATE the grade.
     * @param enrollmentId The unique enrollment ID.
     * @param component The name of the grade (e.g., "Final Exam").
     * @param score The numerical score.
     * @param finalGrade The letter grade (e.g., "A-").
     * @return true if successful, false otherwise.
     */
    public boolean submitGrade(int enrollmentId, String component, double score, String finalGrade) {
        // This query is special: it inserts a new grade, but if a grade
        // for that enrollment_id already exists, it updates it instead.
        String sql = "INSERT INTO erp_db.grades (enrollment_id, component, score, final_grade) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE component = ?, score = ?, final_grade = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // For the INSERT part
            pstmt.setInt(1, enrollmentId);
            pstmt.setString(2, component);
            pstmt.setDouble(3, score);
            pstmt.setString(4, finalGrade);
            
            // For the UPDATE part
            pstmt.setString(5, component);
            pstmt.setDouble(6, score);
            pstmt.setString(7, finalGrade);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 1 = insert, 2 = update

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
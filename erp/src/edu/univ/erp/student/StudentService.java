package edu.univ.erp.student;

import edu.univ.erp.auth.DatabaseConnection;
import edu.univ.erp.model.SectionDisplay; // Ensure you have this model class
import edu.univ.erp.model.StudentProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentService {

    /**
     * Fetches a student's profile from the erp_db using their user_id.
     * @param userId The user_id from the auth_db.
     * @return A StudentProfile object, or null if not found.
     */
    public StudentProfile getStudentProfile(int userId) {
        StudentProfile profile = null;
        String sql = "SELECT student_id, user_id, roll_number, full_name, program " +
                     "FROM students WHERE user_id = ?";

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
     * Fetches sections that are open for registration.
     * Logic:
     * 1. Must have space (enrolled_count < capacity).
     * 2. Student must NOT already be enrolled in it.
     * * @param studentId The ID of the student currently logged in.
     * @return List of available sections.
     */
    public List<SectionDisplay> getAvailableSections(int studentId) {
        List<SectionDisplay> sections = new ArrayList<>();
        
        String sql = "SELECT s.section_id, c.course_code, c.title, i.full_name, " +
                     "s.day_time, s.room, s.capacity, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) as enrolled_count " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "LEFT JOIN instructors i ON s.instructor_id = i.instructor_id " +
                     "WHERE (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) < s.capacity " +
                     "AND s.section_id NOT IN (SELECT section_id FROM enrollments WHERE student_id = ?)";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int capacity = rs.getInt("capacity");
                int enrolled = rs.getInt("enrolled_count");
                
                // Format strings for display
                String courseName = rs.getString("course_code") + " - " + rs.getString("title");
                String instructor = rs.getString("full_name");
                if (instructor == null) instructor = "TBA";
                
                String seatsInfo = enrolled + "/" + capacity;
                String scheduleInfo = rs.getString("day_time") + " (" + rs.getString("room") + ")";

                // Combine seats and schedule into one string for the table
                String displayInfo = seatsInfo + " | " + scheduleInfo;

                sections.add(new SectionDisplay(
                    rs.getInt("section_id"),
                    courseName,
                    instructor,
                    displayInfo
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    /**
     * Registers a student for a specific section.
     * * @param studentId The student's ID.
     * @param sectionId The section to join.
     * @return A success or error message string.
     */
    public String registerStudent(int studentId, int sectionId) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";
        
        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            
            int rows = stmt.executeUpdate();
            return rows > 0 ? "Success" : "Registration failed.";
            
        } catch (SQLException e) {
            // Check for duplicate entry violation (based on UNIQUE key in DB)
            if (e.getMessage().contains("Duplicate entry") || e.getErrorCode() == 1062) {
                return "You are already enrolled in this section.";
            }
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
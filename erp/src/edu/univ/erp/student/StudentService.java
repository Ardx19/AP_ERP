package edu.univ.erp.student;

import edu.univ.erp.auth.DatabaseConnection;
import edu.univ.erp.model.StudentProfile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // --- TODO ---
    // Later we will add more methods here:
    // public List<EnrolledCourse> getEnrolledCourses(int studentId)
    // public List<Grade> getGrades(int studentId)
}
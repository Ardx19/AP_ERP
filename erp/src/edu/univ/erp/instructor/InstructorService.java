package edu.univ.erp.instructor;

import edu.univ.erp.auth.DatabaseConnection;
import edu.univ.erp.model.InstructorDisplay; // We can reuse or create a profile model
import edu.univ.erp.model.SectionDisplay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstructorService {

    // 1. Fetch Instructor Profile by User ID
    public InstructorDisplay getInstructorProfile(int userId) {
        InstructorDisplay instructor = null;
        String sql = "SELECT instructor_id, full_name, department FROM instructors WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // We are reusing the InstructorDisplay model, but you might want a dedicated InstructorProfile class later
                instructor = new InstructorDisplay(
                    rs.getInt("instructor_id"),
                    rs.getString("full_name"),
                    rs.getString("department")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructor;
    }

    // 2. Fetch Sections assigned to this instructor
    public List<SectionDisplay> getAssignedSections(int instructorId) {
        List<SectionDisplay> sections = new ArrayList<>();
        // We join with courses to get the code/title
        String sql = "SELECT s.section_id, c.course_code, c.title, s.day_time, s.room, s.capacity, " +
                     "(SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) as enrolled_count " +
                     "FROM sections s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "WHERE s.instructor_id = ?";

        try (Connection conn = DatabaseConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String courseName = rs.getString("course_code") + " - " + rs.getString("title");
                String schedule = rs.getString("day_time") + " (" + rs.getString("room") + ")";
                String count = rs.getInt("enrolled_count") + "/" + rs.getInt("capacity");
                
                // Using SectionDisplay(id, courseName, instructorName, details)
                // We put "Student Count" in the 3rd field since "Instructor Name" is redundant here
                sections.add(new SectionDisplay(
                    rs.getInt("section_id"),
                    courseName,
                    "Enrolled: " + count, 
                    schedule
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }
}
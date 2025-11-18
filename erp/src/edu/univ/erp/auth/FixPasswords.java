package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class FixPasswords {

    public static void main(String[] args) {
        System.out.println("=== Resetting All User Passwords ===");

        // 1. Update Admin
        updatePassword("admin", "admin123");

        // 2. Update Instructor
        updatePassword("prof_smith", "password123");

        // 3. Update Student
        updatePassword("student1", "password123");
        
        System.out.println("====================================");
        System.out.println("All passwords have been fixed.");
        System.out.println("Try logging in now!");
    }

    private static void updatePassword(String username, String plainPassword) {
        // Generate a REAL, valid BCrypt hash
        String salt = BCrypt.gensalt(12);
        String hash = BCrypt.hashpw(plainPassword, salt);

        String sql = "UPDATE auth_db.users_auth SET user_pass_hash = ? WHERE login_name = ?";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hash);
            pstmt.setString(2, username);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Success: " + username + " -> " + plainPassword);
            } else {
                System.out.println("❌ Failed: User '" + username + "' not found in DB.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error updating " + username + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
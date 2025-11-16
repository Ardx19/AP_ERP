package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SeedAdminUser {

    public static void main(String[] args) {
        String adminLogin = "admin";
        String adminPass = "admin123";
        String role = "admin";

        // 1. Hash the password
        String salt = BCrypt.gensalt(12);
        String passHash = BCrypt.hashpw(adminPass, salt);
        
        System.out.println("Seeding admin user...");

        // 2. SQL to insert (or update if 'admin' login already exists)
        String sql = "INSERT INTO auth_db.users_auth (login_name, user_pass_hash, user_role) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE user_pass_hash = ?, user_role = ?";

        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, adminLogin);
            pstmt.setString(2, passHash);
            pstmt.setString(3, role);
            pstmt.setString(4, passHash); // for the ON DUPLICATE part
            pstmt.setString(5, role);     // for the ON DUPLICATE part
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Success! Admin user created/updated.");
                System.out.println("Username: " + adminLogin);
                System.out.println("Password: " + adminPass);
            } else {
                System.out.println("Admin user was not created.");
            }

        } catch (Exception e) {
            System.err.println("ERROR: Could not seed admin user.");
            System.err.println("Check your 'DatabaseConnection.java' password!");
            e.printStackTrace();
        }
    }
}
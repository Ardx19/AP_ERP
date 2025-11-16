package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
/**
 * Attempts to log a user in.
 * @param loginName The entered username
 * @param plainPassword The entered password
 * @return A 'User' object with userId and role if successful, or null if it fails.
 */
public User login(String loginName, String plainPassword) {

    String storedHash = null;
    String storedRole = null;
    int storedUserId = -1; // --- NEW ---

    // Get user_id, hash, and role
    String sql = "SELECT user_id, user_pass_hash, user_role FROM users_auth WHERE login_name = ?";

    try (Connection conn = DatabaseConnection.getAuthConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, loginName);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                storedUserId = rs.getInt("user_id"); // --- NEW ---
                storedHash = rs.getString("user_pass_hash");
                storedRole = rs.getString("user_role");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }

    if (storedHash == null) {
        System.out.println("Login attempt: User '" + loginName + "' not found.");
        return null; 
    }

    if (BCrypt.checkpw(plainPassword, storedHash)) {
        System.out.println("Login success: User '" + loginName + "' Role: " + storedRole);
        return new User(storedUserId, storedRole); // Return the new User object
    } else {
        System.out.println("Login attempt: Invalid password for user '" + loginName + "'.");
        return null;
    }
}
/**
 * A simple inner class to hold login data.
 * Making it 'public' makes it visible to other packages.
 */
public class User { // <--- THIS IS THE FIXED LINE
    public int userId;
    public String role;
    public User(int userId, String role) {
        this.userId = userId;
        this.role = role;
    }
}
}

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SeedAdminUser {

    // This will be our single source of database connections
    private static HikariDataSource dataSource;

    public static void main(String[] args) {
        
        // --- 1. CONFIGURE DATABASE CONNECTION (HikariCP) ---
        HikariConfig config = new HikariConfig();
        
        // We are connecting to the 'auth_db'
        config.setJdbcUrl("jdbc:mysql://localhost:3306/auth_db");
        
        // !! IMPORTANT !!
        // Change "root" to your MySQL username
        config.setUsername("root");
        // Change "your_password" to your actual MySQL password
        config.setPassword("ShrishtiAnsh2103");
        
        // Some standard settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            // Initialize the connection pool
            dataSource = new HikariDataSource(config);

            // --- 2. DEFINE THE NEW ADMIN USER ---
            String loginName = "admin";
            String passwordToHash = "admin123";
            String userRole = "admin";

            // --- 3. HASH THE PASSWORD (jBCrypt) ---
            // We are creating a "salt" for hashing.
            // A "log_rounds" of 12 is a good, secure standard.
            String salt = BCrypt.gensalt(12);
            String passwordHash = BCrypt.hashpw(passwordToHash, salt);

            System.out.println("Seeding user: " + loginName);
            System.out.println("Password: " + passwordToHash);
            System.out.println("Hashed to: " + passwordHash);

            // --- 4. INSERT THE USER INTO THE DATABASE ---
            // We use a "try-with-resources" block to auto-close the connection
            try (Connection conn = dataSource.getConnection()) {
                
                // The SQL query to insert a new user
                // Using "?" as placeholders is a "PreparedStatement"
                // This prevents SQL injection attacks.
                String sql = "INSERT INTO users_auth (login_name, user_pass_hash, user_role) VALUES (?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, loginName);
                    pstmt.setString(2, passwordHash);
                    pstmt.setString(3, userRole);

                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("\nSuccessfully seeded admin user!");
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database connection error or SQL error!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred:");
            e.printStackTrace();
        } finally {
            // Close the connection pool when the program is done
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }
}
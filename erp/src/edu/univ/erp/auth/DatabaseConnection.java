package edu.univ.erp.auth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static HikariDataSource authDataSource;

    static {
        // --- Configuration for the Auth DB ---
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl("jdbc:mysql://localhost:3306/auth_db");
        
        // !! IMPORTANT: SET YOUR USERNAME AND PASSWORD HERE !!
        authConfig.setUsername("root");
        authConfig.setPassword("your_password"); 
        
        authConfig.addDataSourceProperty("cachePrepStmts", "true");
        authConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        
        authDataSource = new HikariDataSource(authConfig);

        // TODO: You will also add a configuration for the 'erp_db' here later
    }

    /**
     * Gets a connection to the Auth DB.
     */
    public static Connection getAuthConnection() throws SQLException {
        return authDataSource.getConnection();
    }
}
package edu.univ.erp.auth;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static HikariDataSource authDataSource;
    private static HikariDataSource erpDataSource; 

    static {
        // --- 1. Configuration for the Auth DB ---
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl("jdbc:mysql://localhost:3306/auth_db");
        
        // !! IMPORTANT: SET YOUR USERNAME AND PASSWORD HERE !!
        authConfig.setUsername("root");
        authConfig.setPassword("ShrishtiAnsh2103"); // <-- SET PASSWORD HERE
        
        authConfig.addDataSourceProperty("cachePrepStmts", "true");
        authConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        
        authDataSource = new HikariDataSource(authConfig);

        // --- 2. Configuration for the ERP DB ---
        HikariConfig erpConfig = new HikariConfig();
        erpConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_db");
        
        // !! IMPORTANT: SET YOUR USERNAME AND PASSWORD HERE !!
        erpConfig.setUsername("root");
        erpConfig.setPassword("ShrishtiAnsh2103"); // <-- SET PASSWORD HERE
        
        erpConfig.addDataSourceProperty("cachePrepStmts", "true");
        erpConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        
        erpDataSource = new HikariDataSource(erpConfig);
    }

    /**
     * Gets a connection to the Auth DB.
     */
    public static Connection getAuthConnection() throws SQLException {
        return authDataSource.getConnection();
    }

    /**
     * Gets a connection to the ERP DB.
     */
    public static Connection getErpConnection() throws SQLException {
        return erpDataSource.getConnection();
    }
}
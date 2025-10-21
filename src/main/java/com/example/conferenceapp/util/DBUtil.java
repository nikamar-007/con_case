package com.example.conferenceapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:mysql://localhost:3306/conference_db?serverTimezone=UTC&useSSL=false");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "conference_user");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "your_password");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
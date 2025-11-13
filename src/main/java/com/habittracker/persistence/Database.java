package com.habittracker.persistence;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {
    private static final String DB_DIR = "data";
    private static final String DB_PATH = DB_DIR + "/habits.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;

    public static void initialize() {
        try {
            File dir = new File(DB_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (Connection conn = DriverManager.getConnection(JDBC_URL);
                 Statement st = conn.createStatement()) {

                st.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL
                    );
                """);

                st.execute("""
                    CREATE TABLE IF NOT EXISTS habits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT,
                        created_at TEXT NOT NULL,
                        target_per_week INTEGER NOT NULL,
                        FOREIGN KEY(user_id) REFERENCES users(id)
                    );
                """);

                st.execute("""
                    CREATE TABLE IF NOT EXISTS completions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        habit_id INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        FOREIGN KEY(habit_id) REFERENCES habits(id)
                    );
                """);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get DB connection", e);
        }
    }
}

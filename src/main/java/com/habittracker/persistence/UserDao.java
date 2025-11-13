package com.habittracker.persistence;

import com.habittracker.model.User;

import java.sql.*;

public class UserDao {
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed", e);
        }
        return null;
    }

    public User create(String username, String passwordHash) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new User(keys.getInt(1), username, passwordHash);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("create user failed", e);
        }
        throw new RuntimeException("Failed to create user, no key returned");
    }
}

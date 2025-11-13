package com.habittracker.persistence;

import com.habittracker.model.Habit;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitDao {

    public Habit create(int userId, String name, String description, int targetPerWeek) {
        String sql = "INSERT INTO habits (user_id, name, description, created_at, target_per_week) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setString(4, LocalDate.now().toString());
            ps.setInt(5, targetPerWeek);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Habit(keys.getInt(1), userId, name, description, LocalDate.now(), targetPerWeek);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("create habit failed", e);
        }
        throw new RuntimeException("Failed to create habit, no key");
    }

    public List<Habit> findByUser(int userId) {
        String sql = "SELECT id, user_id, name, description, created_at, target_per_week FROM habits WHERE user_id = ?";
        List<Habit> list = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Habit(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            LocalDate.parse(rs.getString("created_at")),
                            rs.getInt("target_per_week")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUser failed", e);
        }
        return list;
    }

    public void delete(int habitId) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM habits WHERE id = ?")) {
            ps.setInt(1, habitId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete habit failed", e);
        }
    }

    public void addCompletion(int habitId, LocalDate date) {
        String sql = "INSERT INTO completions (habit_id, date) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, habitId);
            ps.setString(2, date.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("addCompletion failed", e);
        }
    }

    public List<LocalDate> getCompletions(int habitId) {
        String sql = "SELECT date FROM completions WHERE habit_id = ? ORDER BY date ASC";
        List<LocalDate> dates = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, habitId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dates.add(LocalDate.parse(rs.getString("date")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getCompletions failed", e);
        }
        return dates;
    }

    public void deleteCompletion(int completionId) {
        String sql = "DELETE FROM completions WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, completionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteCompletion failed", e);
        }
    }
}

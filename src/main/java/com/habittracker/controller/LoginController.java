package com.habittracker.controller;

import com.habittracker.model.User;
import com.habittracker.persistence.UserDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDao userDao = new UserDao();

    @FXML
    public void onLogin(ActionEvent e) {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            statusLabel.setText("Enter username and password.");
            return;
        }

        String hash = sha256(p);
        User user = userDao.findByUsername(u);
        if (user == null) {
            statusLabel.setText("User not found. Create account?");
        } else if (!user.getPasswordHash().equals(hash)) {
            statusLabel.setText("Invalid credentials.");
        } else {
            openDashboard(user);
        }
    }

    @FXML
    public void onCreateAccount(ActionEvent e) {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        if (u.isEmpty() || p.isEmpty()) {
            statusLabel.setText("Enter username and password.");
            return;
        }
        if (userDao.findByUsername(u) != null) {
            statusLabel.setText("Username already exists.");
            return;
        }
        User user = userDao.create(u, sha256(p));
        openDashboard(user);
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/habittracker/view/DashboardView.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            DashboardController controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
        } catch (Exception ex) {
            statusLabel.setText("Failed to open dashboard.");
            ex.printStackTrace();
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : out) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("hash failed", e);
        }
    }
}

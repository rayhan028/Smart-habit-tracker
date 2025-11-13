package com.habittracker;

import com.habittracker.persistence.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Database.initialize();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/habittracker/view/LoginView.fxml"));
        Scene scene = new Scene(loader.load(), 420, 280);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setTitle("Smart Habit Tracker");
        stage.getIcons().add(new Image("https://via.placeholder.com/64")); // optional icon
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

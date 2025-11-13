package com.habittracker.controller;

import com.habittracker.model.Habit;
import com.habittracker.model.Suggestion;
import com.habittracker.model.RuleEngine;
import com.habittracker.model.User;
import com.habittracker.persistence.HabitDao;
import com.habittracker.util.CsvExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.*;

public class DashboardController {
    @FXML private Label userLabel;
    @FXML private ListView<Habit> habitListView;
    @FXML private TextArea suggestionsArea;
    @FXML private LineChart<String, Number> weeklyLineChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Button addHabitButton;
    @FXML private Button markTodayButton;
    @FXML private Button exportButton;
    @FXML private Button deleteHabitButton;

    private User user;
    private final HabitDao habitDao = new HabitDao();
    private final RuleEngine ruleEngine = new RuleEngine();
    private final ObservableList<Habit> habits = FXCollections.observableArrayList();

    public void setUser(User user) {
        this.user = user;
        userLabel.setText("Welcome, " + user.getUsername());
        loadHabits();
        refreshChartsAndSuggestions();
    }

    @FXML
    public void initialize() {
        habitListView.setItems(habits);
        habitListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Habit item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (target " + item.getTargetPerWeek() + "/week)");
            }
        });
        yAxis.setLabel("Completions");
        xAxis.setLabel("Day");
    }

    @FXML
    public void onAddHabit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/habittracker/view/AddHabitDialog.fxml"));
            DialogPane pane = loader.load();
            AddHabitDialogController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Habit");
            dialog.setDialogPane(pane);
            Optional<ButtonType> res = dialog.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                String name = controller.getName();
                String desc = controller.getDescription();
                int target = controller.getTargetPerWeek();
                if (name != null && !name.isBlank() && target > 0) {
                    Habit h = habitDao.create(user.getId(), name, desc, target);
                    habits.add(h);
                    refreshChartsAndSuggestions();
                }
            }
        } catch (Exception ex) {
            showAlert("Failed to add habit: " + ex.getMessage());
        }
    }

    @FXML
    public void onMarkToday() {
        Habit selected = habitListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a habit first.");
            return;
        }
        habitDao.addCompletion(selected.getId(), LocalDate.now());
        refreshChartsAndSuggestions();
    }

    @FXML
    public void onDeleteHabit() {
        Habit selected = habitListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a habit to delete.");
            return;
        }
        habitDao.delete(selected.getId());
        habits.remove(selected);
        refreshChartsAndSuggestions();
    }

    @FXML
    public void onExport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("habit_data.csv");
        Stage stage = (Stage) exportButton.getScene().getWindow();
        var file = fc.showSaveDialog(stage);
        if (file != null) {
            Map<Habit, List<LocalDate>> data = collectData();
            CsvExporter.export(file.getAbsolutePath(), data);
            showAlert("Exported to " + file.getAbsolutePath());
        }
    }

    private void loadHabits() {
        habits.setAll(habitDao.findByUser(user.getId()));
    }

    private Map<Habit, List<LocalDate>> collectData() {
        Map<Habit, List<LocalDate>> map = new LinkedHashMap<>();
        for (Habit h : habits) {
            map.put(h, habitDao.getCompletions(h.getId()));
        }
        return map;
    }

    private void refreshChartsAndSuggestions() {
        Map<Habit, List<LocalDate>> data = collectData();
        renderWeeklyChart(data);
        renderSuggestions(data);
    }

    private void renderWeeklyChart(Map<Habit, List<LocalDate>> data) {
        weeklyLineChart.getData().clear();
        LocalDate start = LocalDate.now().minusDays(6);
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < 7; i++) labels.add(start.plusDays(i).getDayOfWeek().name().substring(0, 3));
        xAxis.setCategories(FXCollections.observableArrayList(labels));

        for (Map.Entry<Habit, List<LocalDate>> e : data.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(e.getKey().getName());
            for (int i = 0; i < 7; i++) {
                LocalDate d = start.plusDays(i);
                long count = e.getValue().stream().filter(dd -> dd.equals(d)).count();
                series.getData().add(new XYChart.Data<>(labels.get(i), count));
            }
            weeklyLineChart.getData().add(series);
        }
    }

    private void renderSuggestions(Map<Habit, List<LocalDate>> data) {
        List<Suggestion> suggestions = ruleEngine.evaluate(data);
        StringBuilder sb = new StringBuilder();
        for (Suggestion s : suggestions) {
            sb.append("• ").append(s.getMessage()).append("\n");
        }
        suggestionsArea.setText(sb.toString());
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}

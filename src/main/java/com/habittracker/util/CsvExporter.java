package com.habittracker.util;

import com.habittracker.model.Habit;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CsvExporter {
    public static void export(String path, Map<Habit, List<LocalDate>> data) {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("habit_id,habit_name,date\n");
            for (Map.Entry<Habit, List<LocalDate>> e : data.entrySet()) {
                Habit h = e.getKey();
                for (LocalDate d : e.getValue()) {
                    fw.write(h.getId() + "," + escape(h.getName()) + "," + d + "\n");
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("CSV export failed", ex);
        }
    }

    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}

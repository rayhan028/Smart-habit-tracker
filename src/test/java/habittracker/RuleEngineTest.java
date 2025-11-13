package com.habittracker;

import com.habittracker.model.Habit;
import com.habittracker.model.RuleEngine;
import com.habittracker.model.Suggestion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RuleEngineTest {
    @Test
    public void testSuggestions() {
        RuleEngine engine = new RuleEngine();
        Habit h = new Habit(1, 1, "Read", "Read 30 mins", LocalDate.now().minusDays(20), 5);
        List<LocalDate> completions = new ArrayList<>();

        // 7-day streak
        for (int i = 0; i < 7; i++) completions.add(LocalDate.now().minusDays(i));

        Map<Habit, List<LocalDate>> map = new LinkedHashMap<>();
        map.put(h, completions);

        List<Suggestion> s = engine.evaluate(map);
        assertTrue(s.stream().anyMatch(x -> x.getMessage().toLowerCase().contains("great streak")));
    }
}

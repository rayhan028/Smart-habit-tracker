package com.habittracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleEngine {

    public List<Suggestion> evaluate(Map<Habit, List<LocalDate>> habitMap) {
        List<Suggestion> suggestions = new ArrayList<>();

        for (Map.Entry<Habit, List<LocalDate>> e : habitMap.entrySet()) {
            Habit habit = e.getKey();
            List<LocalDate> completions = e.getValue();

            int streak = computeStreak(completions);
            int missedInRow = computeMissedInRow(completions);

            if (streak >= 7) {
                suggestions.add(new Suggestion(habit.getId(),
                        "Great streak on '" + habit.getName() + "'! Consider raising target or adding a reward."));
            }

            if (missedInRow >= 3) {
                suggestions.add(new Suggestion(habit.getId(),
                        "Struggling with '" + habit.getName() + "'. Break it down (smaller steps) or reduce target temporarily."));
            }

            int weeklyCount = countInLastDays(completions, 7);
            if (weeklyCount < habit.getTargetPerWeek()) {
                suggestions.add(new Suggestion(habit.getId(),
                        "You're below the weekly target for '" + habit.getName() + "'. Plan specific slots."));
            }
        }

        return suggestions;
    }

    private int computeStreak(List<LocalDate> completions) {
        int streak = 0;
        LocalDate today = LocalDate.now();
        while (completions.contains(today.minusDays(streak))) {
            streak++;
        }
        return streak;
    }

    private int computeMissedInRow(List<LocalDate> completions) {
        int missed = 0;
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate d = today.minusDays(i);
            if (!completions.contains(d)) missed++;
            else break;
        }
        return missed;
    }

    private int countInLastDays(List<LocalDate> completions, int days) {
        LocalDate threshold = LocalDate.now().minusDays(days - 1);
        int c = 0;
        for (LocalDate d : completions) {
            if (!d.isBefore(threshold)) c++;
        }
        return c;
    }
}

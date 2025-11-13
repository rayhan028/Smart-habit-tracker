package com.habittracker.model;

import java.time.LocalDate;

public class HabitCompletion {
    private final int id;
    private final int habitId;
    private final LocalDate date;

    public HabitCompletion(int id, int habitId, LocalDate date) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    public int getId() { return id; }
    public int getHabitId() { return habitId; }
    public LocalDate getDate() { return date; }
}

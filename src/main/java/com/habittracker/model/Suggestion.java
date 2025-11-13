package com.habittracker.model;

public class Suggestion {
    private final int habitId;
    private final String message;

    public Suggestion(int habitId, String message) {
        this.habitId = habitId;
        this.message = message;
    }

    public int getHabitId() { return habitId; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return message;
    }
}

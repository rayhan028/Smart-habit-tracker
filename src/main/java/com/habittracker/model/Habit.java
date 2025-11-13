package com.habittracker.model;

import java.time.LocalDate;

public class Habit {
    private final int id;
    private final int userId;
    private final String name;
    private final String description;
    private final LocalDate createdAt;
    private final int targetPerWeek; // e.g., 5 days/week

    public Habit(int id, int userId, String name, String description, LocalDate createdAt, int targetPerWeek) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.targetPerWeek = targetPerWeek;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDate getCreatedAt() { return createdAt; }
    public int getTargetPerWeek() { return targetPerWeek; }
}

# Smart Habit Tracker

Local, mid-size JavaFX + SQLite desktop app. No REST, no web. Track habits, visualize streaks, and get simple rule-based suggestions.

## Features
- Local user accounts (SQLite)
- Add habits, mark daily completions
- Streaks and completion rate visualizations
- Rule-based suggestions (break down habits, nudge consistency)
- CSV export

## Build & Run
- Java 17+
- `mvn clean javafx:run`

## First Run
- Creates `data/habits.db` automatically.
- Default view: Login → Dashboard.

## Repo Structure
See top-level tree.

## Notes
- Data stored locally under `data/`.
- No network calls or REST.

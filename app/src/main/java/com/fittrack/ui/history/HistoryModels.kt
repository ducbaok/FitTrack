package com.fittrack.ui.history

/**
 * Data class representing a workout with its associated exercise name.
 * Used for displaying history items.
 */
data class WorkoutWithExercise(
    val workoutId: Int,
    val exerciseId: Int,
    val exerciseName: String,
    val muscleId: Int,
    val timestamp: Long,
    val reps: Int?,
    val weightKg: Float?
)

/**
 * Represents a group of workouts for a specific date.
 * Used for displaying history grouped by day.
 */
data class HistoryDayGroup(
    val dateLabel: String,  // "Today", "Yesterday", "Jan 8, 2026"
    val dateMillis: Long,
    val workouts: List<WorkoutWithExercise>
)

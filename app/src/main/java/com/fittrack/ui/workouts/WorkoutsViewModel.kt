package com.fittrack.ui.workouts

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Data class representing a user's saved workout.
 */
data class Workout(
    val id: Int,
    val name: String,
    val exercises: Int,
    val fatigueLevel: Int,
    val muscles: List<String>
)

/**
 * ViewModel for the Workouts screen.
 */
@HiltViewModel
class WorkoutsViewModel @Inject constructor() : ViewModel() {

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        // TODO: Load workouts from repository
        _workouts.value = listOf(
            Workout(1, "Push Day", 6, 75, listOf("Chest", "Shoulders", "Triceps")),
            Workout(2, "Pull Day", 5, 65, listOf("Back", "Biceps", "Forearms")),
            Workout(3, "Leg Day", 6, 85, listOf("Quads", "Hamstrings", "Calves")),
            Workout(4, "Upper Body", 8, 70, listOf("Chest", "Back", "Shoulders"))
        )
    }
}

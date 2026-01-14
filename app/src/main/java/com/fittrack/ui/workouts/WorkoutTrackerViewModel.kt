package com.fittrack.ui.workouts

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the active Workout Tracker session.
 */
@HiltViewModel
class WorkoutTrackerViewModel @Inject constructor() : ViewModel() {

    private val _exercises = MutableStateFlow<List<TrackerExercise>>(emptyList())
    val exercises: StateFlow<List<TrackerExercise>> = _exercises.asStateFlow()

    init {
        // Mock data for initial testing
        loadMockData()
    }

    private fun loadMockData() {
        val mockData = listOf(
            TrackerExercise(
                id = "1",
                name = "Barbell Squat",
                sets = listOf(
                    TrackerSet(UUID.randomUUID().toString(), 1, "100kg x 5"),
                    TrackerSet(UUID.randomUUID().toString(), 2, "100kg x 5")
                )
            ),
            TrackerExercise(
                id = "2",
                name = "Barbell Bench Press",
                sets = listOf(
                    TrackerSet(UUID.randomUUID().toString(), 1, "80kg x 8")
                )
            )
        )
        _exercises.value = mockData
    }

    fun addSet(exerciseId: String) {
        val currentList = _exercises.value.toMutableList()
        val exerciseIndex = currentList.indexOfFirst { it.id == exerciseId }
        
        if (exerciseIndex != -1) {
            val exercise = currentList[exerciseIndex]
            val newSetNumber = exercise.sets.size + 1
            val newSet = TrackerSet(
                id = UUID.randomUUID().toString(),
                setNumber = newSetNumber,
                previous = "-"
            )
            
            val updatedSets = exercise.sets + newSet
            currentList[exerciseIndex] = exercise.copy(sets = updatedSets)
            _exercises.value = currentList
        }
    }

    fun updateSet(exerciseId: String, setId: String, updatedSet: TrackerSet) {
        // In a real app, we would update the state more efficiently or persist to DB.
        // For local state update:
        val currentList = _exercises.value.toMutableList()
        val exerciseIndex = currentList.indexOfFirst { it.id == exerciseId }
        
        if (exerciseIndex != -1) {
            val exercise = currentList[exerciseIndex]
            val updatedSets = exercise.sets.map { 
                if (it.id == setId) updatedSet else it 
            }
            currentList[exerciseIndex] = exercise.copy(sets = updatedSets)
            _exercises.value = currentList
        }
    }
}

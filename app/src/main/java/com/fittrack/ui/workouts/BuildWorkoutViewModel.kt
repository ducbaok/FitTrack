package com.fittrack.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.data.local.entity.WorkoutTemplateExerciseEntity
import com.fittrack.data.repository.ExerciseRepository
import com.fittrack.data.repository.WorkoutTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class representing an exercise in the workout builder.
 */
data class BuildWorkoutExercise(
    val exerciseId: Int,
    val name: String,
    val muscle: String,
    val equipment: String,
    val sets: Int = 3,
    val reps: Int = 10,
    val weight: Float = 0f
)

/**
 * ViewModel for Build Workout screen.
 */
@HiltViewModel
class BuildWorkoutViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val templateRepository: WorkoutTemplateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BuildWorkoutState())
    val state: StateFlow<BuildWorkoutState> = _state.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val searchResults: StateFlow<List<ExerciseEntity>> = _searchResults.asStateFlow()

    private var allExercises: List<ExerciseEntity> = emptyList()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                allExercises = exercises
            }
        }
    }

    fun setWorkoutName(name: String) {
        _state.value = _state.value.copy(workoutName = name)
    }

    fun searchExercises(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        val filtered = allExercises.filter { exercise ->
            exercise.name.contains(query, ignoreCase = true) ||
            exercise.equipment.contains(query, ignoreCase = true)
        }
        _searchResults.value = filtered.take(10)
    }

    fun addExercise(exercise: ExerciseEntity) {
        val current = _state.value.selectedExercises
        
        // Don't add if already exists
        if (current.any { it.exerciseId == exercise.exerciseId }) {
            return
        }
        
        val newExercise = BuildWorkoutExercise(
            exerciseId = exercise.exerciseId,
            name = exercise.name,
            muscle = getMuscleNameById(exercise.muscleId),
            equipment = exercise.equipment
        )
        
        _state.value = _state.value.copy(
            selectedExercises = current + newExercise,
            searchQuery = "",
        )
        _searchResults.value = emptyList()
    }

    fun removeExercise(exerciseId: Int) {
        val current = _state.value.selectedExercises
        _state.value = _state.value.copy(
            selectedExercises = current.filter { it.exerciseId != exerciseId }
        )
    }

    fun updateExerciseSets(exerciseId: Int, sets: Int) {
        updateExercise(exerciseId) { it.copy(sets = sets.coerceAtLeast(1)) }
    }

    fun updateExerciseReps(exerciseId: Int, reps: Int) {
        updateExercise(exerciseId) { it.copy(reps = reps.coerceAtLeast(1)) }
    }

    fun updateExerciseWeight(exerciseId: Int, weight: Float) {
        updateExercise(exerciseId) { it.copy(weight = weight.coerceAtLeast(0f)) }
    }

    private fun updateExercise(exerciseId: Int, update: (BuildWorkoutExercise) -> BuildWorkoutExercise) {
        val current = _state.value.selectedExercises
        _state.value = _state.value.copy(
            selectedExercises = current.map { 
                if (it.exerciseId == exerciseId) update(it) else it 
            }
        )
    }

    fun saveWorkout() {
        val name = _state.value.workoutName.trim()
        if (name.isBlank() || _state.value.selectedExercises.isEmpty()) {
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            
            val exercises = _state.value.selectedExercises.mapIndexed { index, exercise ->
                WorkoutTemplateExerciseEntity(
                    templateId = 0, // Will be set by DAO
                    exerciseId = exercise.exerciseId,
                    orderIndex = index,
                    targetSets = exercise.sets,
                    targetReps = exercise.reps,
                    targetWeight = if (exercise.weight > 0) exercise.weight else null
                )
            }
            
            templateRepository.createTemplate(
                name = name,
                exercises = exercises
            )
            
            _state.value = _state.value.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }

    private fun getMuscleNameById(muscleId: Int): String {
        // Simplified muscle name mapping
        return when (muscleId) {
            1 -> "Chest"
            2 -> "Back"
            3 -> "Shoulders"
            4 -> "Biceps"
            5 -> "Triceps"
            6 -> "Forearms"
            7 -> "Abs"
            8 -> "Quads"
            9 -> "Hamstrings"
            10 -> "Glutes"
            11 -> "Calves"
            else -> "Other"
        }
    }
}

/**
 * UI state for Build Workout screen.
 */
data class BuildWorkoutState(
    val workoutName: String = "",
    val searchQuery: String = "",
    val selectedExercises: List<BuildWorkoutExercise> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) {
    val canSave: Boolean
        get() = workoutName.isNotBlank() && selectedExercises.isNotEmpty() && !isSaving
}

package com.fittrack.ui.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.data.repository.ExerciseRepository
import com.fittrack.data.repository.SettingsRepository
import com.fittrack.data.repository.StreakRepository
import com.fittrack.data.repository.WorkoutRepository
import com.fittrack.domain.usecase.HeatmapUseCase
import com.fittrack.ui.home.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Exercise Detail screen.
 * Handles marking exercises as done and undo functionality.
 */
@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val streakRepository: StreakRepository,
    private val heatmapUseCase: HeatmapUseCase,
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId: Int = savedStateHandle["exerciseId"] ?: 0

    private val _exercise = MutableStateFlow<ExerciseEntity?>(null)
    val exercise: StateFlow<ExerciseEntity?> = _exercise.asStateFlow()

    private val _uiState = MutableStateFlow(ExerciseDetailUiState())
    val uiState: StateFlow<ExerciseDetailUiState> = _uiState.asStateFlow()

    // Store last workout ID for undo
    private var lastWorkoutId: Long? = null

    // Muscle name mapping
    private val muscleNames = mapOf(
        1 to "Chest",
        2 to "Back",
        3 to "Shoulders",
        4 to "Biceps",
        5 to "Triceps",
        6 to "Legs",
        7 to "Glutes",
        8 to "Core",
        9 to "Forearms"
    )

    init {
        loadExercise()
        observeGender()
    }
    
    private fun observeGender() {
        viewModelScope.launch {
            settingsRepository.gender.collectLatest { gender ->
                _uiState.value = _uiState.value.copy(gender = gender)
            }
        }
    }

    private fun loadExercise() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val exerciseEntity = exerciseRepository.getExerciseById(exerciseId)
            _exercise.value = exerciseEntity
            
            exerciseEntity?.let { exercise ->
                // Get muscle name
                val muscleName = muscleNames[exercise.muscleId] ?: "Unknown"
                
                // Get fatigue percentage
                val fatiguePercent = heatmapUseCase.getMuscleFatiguePercentage(exercise.muscleId)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    muscleName = muscleName,
                    fatiguePercent = fatiguePercent,
                    fatigueColorHex = heatmapUseCase.getFatigueColor(fatiguePercent.toFloat()).hexColor,
                    isFavorite = exercise.isFavorite
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Toggle favorite status for this exercise.
     */
    fun toggleFavorite() {
        val currentExercise = _exercise.value ?: return
        
        viewModelScope.launch {
            exerciseRepository.toggleFavorite(currentExercise.exerciseId)
            val newFavoriteState = !currentExercise.isFavorite
            _exercise.value = currentExercise.copy(isFavorite = newFavoriteState)
            _uiState.value = _uiState.value.copy(isFavorite = newFavoriteState)
        }
    }

    /**
     * Mark the current exercise as done with set details.
     * This will:
     * 1. Record the workout with reps/weight
     * 2. Update muscle fatigue to 100%
     * 3. Update daily streak
     */
    fun markAsDone(sets: Int = 1, reps: Int? = null, weightKg: Float? = null) {
        val currentExercise = _exercise.value ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Record workout for each set
                repeat(sets) {
                    lastWorkoutId = workoutRepository.recordWorkout(
                        exerciseId = currentExercise.exerciseId,
                        muscleId = currentExercise.muscleId,
                        regionId = currentExercise.regionId,
                        reps = reps,
                        weightKg = weightKg
                    )
                }

                // Update muscle fatigue to 100%
                heatmapUseCase.markMuscleAsTrained(currentExercise.muscleId)
                currentExercise.regionId?.let { regionId ->
                    heatmapUseCase.markRegionAsTrained(regionId)
                }

                // Update streak
                streakRepository.markTodayCompleted()

                // Update fatigue in UI
                val newFatiguePercent = heatmapUseCase.getMuscleFatiguePercentage(currentExercise.muscleId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workoutLogged = true,
                    fatiguePercent = newFatiguePercent,
                    fatigueColorHex = heatmapUseCase.getFatigueColor(newFatiguePercent.toFloat()).hexColor
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Undo the last workout.
     */
    fun undoLastWorkout() {
        val workoutId = lastWorkoutId ?: return

        viewModelScope.launch {
            try {
                workoutRepository.deleteWorkoutById(workoutId.toInt())
                lastWorkoutId = null
                
                // Refresh fatigue after undo
                val currentExercise = _exercise.value
                val fatiguePercent = currentExercise?.let { 
                    heatmapUseCase.getMuscleFatiguePercentage(it.muscleId)
                } ?: 0
                
                _uiState.value = _uiState.value.copy(
                    workoutLogged = false,
                    undoComplete = true,
                    fatiguePercent = fatiguePercent,
                    fatigueColorHex = heatmapUseCase.getFatigueColor(fatiguePercent.toFloat()).hexColor
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Reset UI state after events are consumed.
     */
    fun consumeEvents() {
        _uiState.value = _uiState.value.copy(
            workoutLogged = false,
            undoComplete = false,
            error = null
        )
    }
}

/**
 * UI state for Exercise Detail screen.
 */
data class ExerciseDetailUiState(
    val isLoading: Boolean = true,
    val muscleName: String = "",
    val fatiguePercent: Int = 0,
    val fatigueColorHex: String = "#E91E63", // Default pink
    val isFavorite: Boolean = false,
    val workoutLogged: Boolean = false,
    val undoComplete: Boolean = false,
    val error: String? = null,
    val gender: Gender = Gender.MALE
)

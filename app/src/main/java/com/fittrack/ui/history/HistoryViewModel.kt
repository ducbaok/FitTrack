package com.fittrack.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.repository.ExerciseRepository
import com.fittrack.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Filter type for history screen.
 */
enum class FilterType {
    DAY,
    RANGE
}

/**
 * ViewModel for History screen.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _historyState = MutableStateFlow(HistoryState())
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(isLoading = true)
            
            workoutRepository.getWorkoutHistory().collect { workouts ->
                // Convert workouts to WorkoutWithExercise by fetching exercise names
                val workoutsWithExercises = workouts.map { workout ->
                    val exercise = exerciseRepository.getExerciseById(workout.exerciseId)
                    WorkoutWithExercise(
                        workoutId = workout.workoutId,
                        exerciseId = workout.exerciseId,
                        exerciseName = exercise?.name ?: "Unknown Exercise",
                        muscleId = workout.muscleId,
                        timestamp = workout.timestamp,
                        reps = workout.reps,
                        weightKg = workout.weightKg
                    )
                }
                
                // Group by date
                val groupedByDate = groupWorkoutsByDate(workoutsWithExercises)
                
                _historyState.value = _historyState.value.copy(
                    isLoading = false,
                    historyGroups = groupedByDate
                )
            }
        }
    }

    private fun groupWorkoutsByDate(workouts: List<WorkoutWithExercise>): List<HistoryDayGroup> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        
        val todayStr = dateFormat.format(today.time)
        val yesterdayStr = dateFormat.format(yesterday.time)
        
        return workouts
            .groupBy { dateFormat.format(Date(it.timestamp)) }
            .map { (dateStr, dayWorkouts) ->
                val dateLabel = when (dateStr) {
                    todayStr -> "Today"
                    yesterdayStr -> "Yesterday"
                    else -> {
                        val date = dateFormat.parse(dateStr)
                        if (date != null) displayFormat.format(date) else dateStr
                    }
                }
                HistoryDayGroup(
                    dateLabel = dateLabel,
                    dateMillis = dayWorkouts.firstOrNull()?.timestamp ?: 0L,
                    workouts = dayWorkouts.sortedByDescending { it.timestamp }
                )
            }
            .sortedByDescending { it.dateMillis }
    }

    fun setFilterType(filterType: FilterType) {
        _historyState.value = _historyState.value.copy(filterType = filterType)
        // TODO: Implement date range filter
    }

    fun setDateRange(startDate: Long, endDate: Long) {
        viewModelScope.launch {
            _historyState.value = _historyState.value.copy(isLoading = true)
            
            workoutRepository.getWorkoutHistoryByDateRange(startDate, endDate).collect { workouts ->
                val workoutsWithExercises = workouts.map { workout ->
                    val exercise = exerciseRepository.getExerciseById(workout.exerciseId)
                    WorkoutWithExercise(
                        workoutId = workout.workoutId,
                        exerciseId = workout.exerciseId,
                        exerciseName = exercise?.name ?: "Unknown Exercise",
                        muscleId = workout.muscleId,
                        timestamp = workout.timestamp,
                        reps = workout.reps,
                        weightKg = workout.weightKg
                    )
                }
                
                val groupedByDate = groupWorkoutsByDate(workoutsWithExercises)
                
                _historyState.value = _historyState.value.copy(
                    isLoading = false,
                    historyGroups = groupedByDate
                )
            }
        }
    }

    fun refresh() {
        loadHistory()
    }
}

/**
 * UI state for History screen.
 */
data class HistoryState(
    val isLoading: Boolean = true,
    val historyGroups: List<HistoryDayGroup> = emptyList(),
    val filterType: FilterType = FilterType.DAY
)

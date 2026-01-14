package com.fittrack.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * Time period for stats aggregation.
 */
enum class StatsPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * ViewModel for Track Workout screen.
 * Aggregates workout statistics for different time periods.
 */
@HiltViewModel
class TrackWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _statsState = MutableStateFlow(TrackWorkoutState())
    val statsState: StateFlow<TrackWorkoutState> = _statsState.asStateFlow()

    init {
        loadStats(StatsPeriod.WEEKLY)
    }

    fun setPeriod(period: StatsPeriod) {
        _statsState.value = _statsState.value.copy(selectedPeriod = period)
        loadStats(period)
    }

    private fun loadStats(period: StatsPeriod) {
        viewModelScope.launch {
            _statsState.value = _statsState.value.copy(isLoading = true)
            
            val (startTime, endTime) = getTimeRange(period)
            
            workoutRepository.getWorkoutHistoryByDateRange(startTime, endTime).collect { workouts ->
                // Calculate stats
                val workoutCount = workouts.size
                
                // Estimate duration: ~3 min per exercise logged
                val estimatedMinutes = workoutCount * 3
                val durationText = formatDuration(estimatedMinutes)
                
                // Calculate volume: sum of (reps * weight)
                val volume = workouts.sumOf { workout ->
                    val reps = workout.reps ?: 10
                    val weight = workout.weightKg ?: 0f
                    (reps * weight).toDouble()
                }
                val volumeText = formatVolume(volume)
                
                // Estimate calories: ~5 cal per rep with weight
                val calories = workouts.sumOf { workout ->
                    val reps = workout.reps ?: 10
                    val weight = workout.weightKg ?: 0f
                    if (weight > 0) (reps * 5) else (reps * 2)
                }
                
                // Weekly bar data (for chart)
                val weeklyData = calculateWeeklyData(workouts.map { it.timestamp })
                
                _statsState.value = _statsState.value.copy(
                    isLoading = false,
                    workoutCount = workoutCount,
                    durationText = durationText,
                    volumeText = volumeText,
                    caloriesText = calories.toString(),
                    weeklyBarData = weeklyData
                )
            }
        }
    }

    private fun getTimeRange(period: StatsPeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        when (period) {
            StatsPeriod.DAILY -> {
                // Today
            }
            StatsPeriod.WEEKLY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            }
            StatsPeriod.MONTHLY -> {
                calendar.add(Calendar.MONTH, -1)
            }
            StatsPeriod.YEARLY -> {
                calendar.add(Calendar.YEAR, -1)
            }
        }
        
        return Pair(calendar.timeInMillis, endTime)
    }

    private fun formatDuration(minutes: Int): String {
        return when {
            minutes < 60 -> "${minutes} min"
            else -> {
                val hours = minutes / 60
                val mins = minutes % 60
                if (mins > 0) "${hours}h ${mins}min" else "${hours}h"
            }
        }
    }

    private fun formatVolume(volume: Double): String {
        return when {
            volume >= 1_000_000 -> String.format("%.1fM kg", volume / 1_000_000)
            volume >= 1_000 -> String.format("%.1fK kg", volume / 1_000)
            else -> String.format("%.0f kg", volume)
        }
    }

    private fun calculateWeeklyData(timestamps: List<Long>): List<Int> {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        
        // Create a map of day of week -> count
        val dayCounts = mutableMapOf<Int, Int>()
        for (i in 1..7) dayCounts[i] = 0
        
        for (timestamp in timestamps) {
            calendar.timeInMillis = timestamp
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            dayCounts[dayOfWeek] = (dayCounts[dayOfWeek] ?: 0) + 1
        }
        
        // Return as list starting from Monday (Calendar.MONDAY = 2)
        return listOf(
            dayCounts[Calendar.MONDAY] ?: 0,
            dayCounts[Calendar.TUESDAY] ?: 0,
            dayCounts[Calendar.WEDNESDAY] ?: 0,
            dayCounts[Calendar.THURSDAY] ?: 0,
            dayCounts[Calendar.FRIDAY] ?: 0,
            dayCounts[Calendar.SATURDAY] ?: 0,
            dayCounts[Calendar.SUNDAY] ?: 0
        )
    }
}

/**
 * UI state for Track Workout screen.
 */
data class TrackWorkoutState(
    val isLoading: Boolean = true,
    val selectedPeriod: StatsPeriod = StatsPeriod.WEEKLY,
    val workoutCount: Int = 0,
    val durationText: String = "0 min",
    val volumeText: String = "0 kg",
    val caloriesText: String = "0",
    val weeklyBarData: List<Int> = listOf(0, 0, 0, 0, 0, 0, 0)
)

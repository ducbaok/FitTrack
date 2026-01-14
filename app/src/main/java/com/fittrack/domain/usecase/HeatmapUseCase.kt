package com.fittrack.domain.usecase

import com.fittrack.data.local.entity.MuscleEntity
import com.fittrack.data.local.entity.MuscleRegionEntity
import com.fittrack.data.repository.MuscleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for calculating muscle fatigue and generating heatmap colors.
 * 
 * Fatigue decays linearly over 72 hours:
 * - Rate: 100/72 ≈ 1.39 points per hour
 * - Fresh workout = 100 (Red)
 * - 24 hours later ≈ 67 (Still Red)
 * - 48 hours later ≈ 33 (Yellow)
 * - 72 hours later = 0 (Green)
 */
@Singleton
class HeatmapUseCase @Inject constructor(
    private val muscleRepository: MuscleRepository
) {
    companion object {
        // Full recovery takes 72 hours
        private const val FULL_RECOVERY_HOURS = 72f
        // Maximum fatigue score
        private const val MAX_FATIGUE = 100f
        // Decay rate per hour
        private const val DECAY_RATE_PER_HOUR = MAX_FATIGUE / FULL_RECOVERY_HOURS // ≈ 1.39

        // Color thresholds (fatigue percentage)
        private const val RED_THRESHOLD = 66f
        private const val YELLOW_THRESHOLD = 33f
    }

    /**
     * Get all muscles with their current fatigue levels calculated.
     * Fatigue decays over time since last workout.
     */
    fun getMusclesWithFatigue(): Flow<List<MuscleWithFatigue>> {
        return muscleRepository.getAllMuscles().map { muscles ->
            muscles.map { muscle ->
                val currentFatigue = calculateCurrentFatigue(
                    muscle.fatigueScore,
                    muscle.lastTrainedAt
                )
                MuscleWithFatigue(
                    muscle = muscle,
                    currentFatigue = currentFatigue,
                    color = getFatigueColor(currentFatigue)
                )
            }
        }
    }

    /**
     * Get regions for a muscle with their current fatigue levels.
     */
    fun getRegionsWithFatigue(muscleId: Int): Flow<List<RegionWithFatigue>> {
        return muscleRepository.getRegionsByMuscle(muscleId).map { regions ->
            regions.map { region ->
                val currentFatigue = calculateCurrentFatigue(
                    region.fatigueScore,
                    region.lastTrainedAt
                )
                RegionWithFatigue(
                    region = region,
                    currentFatigue = currentFatigue,
                    color = getFatigueColor(currentFatigue)
                )
            }
        }
    }

    /**
     * Calculate current fatigue based on time elapsed since last workout.
     * Returns a value between 0 and 100.
     */
    fun calculateCurrentFatigue(storedFatigue: Float, lastTrainedAt: Long?): Float {
        if (lastTrainedAt == null) return 0f
        
        val currentTime = System.currentTimeMillis()
        val hoursElapsed = (currentTime - lastTrainedAt) / (1000f * 60f * 60f)
        
        val decayedFatigue = storedFatigue - (hoursElapsed * DECAY_RATE_PER_HOUR)
        return decayedFatigue.coerceIn(0f, MAX_FATIGUE)
    }

    /**
     * Get the heatmap color based on fatigue level.
     */
    fun getFatigueColor(fatigue: Float): FatigueColor {
        return when {
            fatigue >= RED_THRESHOLD -> FatigueColor.RED
            fatigue >= YELLOW_THRESHOLD -> FatigueColor.YELLOW
            else -> FatigueColor.GREEN
        }
    }

    /**
     * Mark a muscle as freshly trained (100% fatigue).
     */
    suspend fun markMuscleAsTrained(muscleId: Int) {
        muscleRepository.updateMuscleFatigue(
            muscleId = muscleId,
            fatigueScore = MAX_FATIGUE,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Mark a muscle region as freshly trained (100% fatigue).
     */
    suspend fun markRegionAsTrained(regionId: Int) {
        muscleRepository.updateRegionFatigue(
            regionId = regionId,
            fatigueScore = MAX_FATIGUE,
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Get the current fatigue percentage for a specific muscle.
     * Returns a value between 0 and 100.
     */
    suspend fun getMuscleFatiguePercentage(muscleId: Int): Int {
        val muscle = muscleRepository.getMuscleById(muscleId) ?: return 0
        val fatigue = calculateCurrentFatigue(muscle.fatigueScore, muscle.lastTrainedAt)
        return fatigue.toInt()
    }
}

/**
 * Wrapper class for muscle with calculated fatigue.
 */
data class MuscleWithFatigue(
    val muscle: MuscleEntity,
    val currentFatigue: Float,
    val color: FatigueColor
)

/**
 * Wrapper class for region with calculated fatigue.
 */
data class RegionWithFatigue(
    val region: MuscleRegionEntity,
    val currentFatigue: Float,
    val color: FatigueColor
)

/**
 * Enum representing heatmap colors based on fatigue level.
 */
enum class FatigueColor(val hexColor: String) {
    RED("#FF4444"),      // ≥66% fatigue - recently trained
    YELLOW("#FFBB33"),   // 33-66% fatigue - recovering
    GREEN("#99CC00")     // <33% fatigue - ready to train
}

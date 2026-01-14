package com.fittrack.domain.usecase

import com.fittrack.data.local.entity.MuscleEntity
import com.fittrack.data.local.entity.MuscleRegionEntity
import com.fittrack.data.repository.MuscleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for recommending under-trained muscles/regions.
 * Suggests muscles that haven't been worked out recently.
 */
@Singleton
class RecommendationUseCase @Inject constructor(
    private val muscleRepository: MuscleRepository,
    private val heatmapUseCase: HeatmapUseCase
) {
    companion object {
        // Recommend muscles with fatigue below this threshold
        private const val RECOMMENDATION_THRESHOLD = 33f
        // Maximum number of recommendations to return
        private const val MAX_RECOMMENDATIONS = 5
    }

    /**
     * Get recommended muscles to train (those with lowest fatigue).
     * Returns muscles sorted by fatigue (lowest first).
     */
    fun getRecommendedMuscles(): Flow<List<MuscleRecommendation>> {
        return heatmapUseCase.getMusclesWithFatigue().map { musclesWithFatigue ->
            musclesWithFatigue
                .filter { it.currentFatigue < RECOMMENDATION_THRESHOLD }
                .sortedBy { it.currentFatigue }
                .take(MAX_RECOMMENDATIONS)
                .map { muscleWithFatigue ->
                    MuscleRecommendation(
                        muscle = muscleWithFatigue.muscle,
                        fatigue = muscleWithFatigue.currentFatigue,
                        reason = getRecommendationReason(muscleWithFatigue)
                    )
                }
        }
    }

    /**
     * Get recommended regions for a specific muscle.
     */
    fun getRecommendedRegions(muscleId: Int): Flow<List<RegionRecommendation>> {
        return heatmapUseCase.getRegionsWithFatigue(muscleId).map { regionsWithFatigue ->
            regionsWithFatigue
                .filter { it.currentFatigue < RECOMMENDATION_THRESHOLD }
                .sortedBy { it.currentFatigue }
                .map { regionWithFatigue ->
                    RegionRecommendation(
                        region = regionWithFatigue.region,
                        fatigue = regionWithFatigue.currentFatigue,
                        reason = getRegionRecommendationReason(regionWithFatigue)
                    )
                }
        }
    }

    /**
     * Get the muscles that haven't been trained in the longest time.
     * Returns muscles sorted by lastTrainedAt (oldest first), null values first.
     */
    fun getMostNeglectedMuscles(): Flow<List<MuscleEntity>> {
        return muscleRepository.getAllMuscles().map { muscles ->
            muscles.sortedWith(compareBy(nullsFirst()) { it.lastTrainedAt })
                .take(MAX_RECOMMENDATIONS)
        }
    }

    private fun getRecommendationReason(muscleWithFatigue: MuscleWithFatigue): String {
        val muscle = muscleWithFatigue.muscle
        return when {
            muscle.lastTrainedAt == null -> "Never trained before"
            muscleWithFatigue.currentFatigue == 0f -> "Fully recovered"
            muscleWithFatigue.currentFatigue < 10f -> "Almost fully recovered"
            else -> "Ready for training"
        }
    }

    private fun getRegionRecommendationReason(regionWithFatigue: RegionWithFatigue): String {
        val region = regionWithFatigue.region
        return when {
            region.lastTrainedAt == null -> "Never trained before"
            regionWithFatigue.currentFatigue == 0f -> "Fully recovered"
            regionWithFatigue.currentFatigue < 10f -> "Almost fully recovered"
            else -> "Ready for training"
        }
    }
}

/**
 * Data class representing a muscle recommendation.
 */
data class MuscleRecommendation(
    val muscle: MuscleEntity,
    val fatigue: Float,
    val reason: String
)

/**
 * Data class representing a region recommendation.
 */
data class RegionRecommendation(
    val region: MuscleRegionEntity,
    val fatigue: Float,
    val reason: String
)

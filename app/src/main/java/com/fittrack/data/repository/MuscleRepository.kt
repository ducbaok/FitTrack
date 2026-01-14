package com.fittrack.data.repository

import com.fittrack.data.local.dao.MuscleDao
import com.fittrack.data.local.dao.MuscleRegionDao
import com.fittrack.data.local.entity.MuscleEntity
import com.fittrack.data.local.entity.MuscleRegionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for muscle and muscle region data operations.
 * Acts as the single source of truth for muscle-related data.
 */
@Singleton
class MuscleRepository @Inject constructor(
    private val muscleDao: MuscleDao,
    private val muscleRegionDao: MuscleRegionDao
) {
    /**
     * Get all muscles as a Flow for reactive updates.
     */
    fun getAllMuscles(): Flow<List<MuscleEntity>> = muscleDao.getAll()

    /**
     * Get a specific muscle by ID.
     */
    suspend fun getMuscleById(id: Int): MuscleEntity? = muscleDao.getById(id)

    /**
     * Get all regions for a specific muscle.
     */
    fun getRegionsByMuscle(muscleId: Int): Flow<List<MuscleRegionEntity>> = 
        muscleRegionDao.getByMuscle(muscleId)

    /**
     * Get a specific region by ID.
     */
    suspend fun getRegionById(id: Int): MuscleRegionEntity? = muscleRegionDao.getById(id)

    /**
     * Update the fatigue score and last trained timestamp for a muscle.
     * Called when a workout is completed for this muscle.
     */
    suspend fun updateMuscleFatigue(muscleId: Int, fatigueScore: Float, timestamp: Long) {
        muscleDao.updateFatigue(muscleId, fatigueScore, timestamp)
    }

    /**
     * Update the fatigue score and last trained timestamp for a muscle region.
     */
    suspend fun updateRegionFatigue(regionId: Int, fatigueScore: Float, timestamp: Long) {
        muscleRegionDao.updateFatigue(regionId, fatigueScore, timestamp)
    }

    /**
     * Insert or update a muscle.
     */
    suspend fun insertMuscle(muscle: MuscleEntity) = muscleDao.insert(muscle)

    /**
     * Insert multiple muscles.
     */
    suspend fun insertAllMuscles(muscles: List<MuscleEntity>) = muscleDao.insertAll(muscles)

    /**
     * Insert multiple muscle regions.
     */
    suspend fun insertAllRegions(regions: List<MuscleRegionEntity>) = 
        muscleRegionDao.insertAll(regions)
}

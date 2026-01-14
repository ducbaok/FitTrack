package com.fittrack.data.repository

import com.fittrack.data.local.dao.ExerciseDao
import com.fittrack.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for exercise data operations.
 * Provides filtering capabilities for exercises by muscle, region, equipment, and difficulty.
 */
@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    /**
     * Get all exercises as a Flow.
     */
    fun getAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getAll()

    /**
     * Get exercises for a specific muscle.
     */
    fun getExercisesByMuscle(muscleId: Int): Flow<List<ExerciseEntity>> = 
        exerciseDao.getByMuscle(muscleId)

    /**
     * Get exercises for a specific muscle region.
     */
    fun getExercisesByRegion(regionId: Int): Flow<List<ExerciseEntity>> = 
        exerciseDao.getByRegion(regionId)

    /**
     * Get a specific exercise by ID.
     */
    suspend fun getExerciseById(id: Int): ExerciseEntity? = exerciseDao.getById(id)

    /**
     * Get all favorite exercises.
     */
    fun getFavorites(): Flow<List<ExerciseEntity>> = exerciseDao.getFavorites()

    /**
     * Filter exercises by multiple criteria.
     * Pass null for any parameter to skip that filter.
     */
    fun filterExercises(
        muscleId: Int? = null,
        regionId: Int? = null,
        equipment: String? = null,
        difficulty: String? = null,
        type: String? = null,
        searchQuery: String? = null,
        favoritesOnly: Boolean = false
    ): Flow<List<ExerciseEntity>> = exerciseDao.filter(
        muscleId, regionId, equipment, difficulty, type, searchQuery, favoritesOnly
    )

    /**
     * Toggle favorite status for an exercise.
     */
    suspend fun toggleFavorite(exerciseId: Int) = exerciseDao.toggleFavorite(exerciseId)

    /**
     * Set favorite status for an exercise.
     */
    suspend fun setFavorite(exerciseId: Int, isFavorite: Boolean) = 
        exerciseDao.setFavorite(exerciseId, isFavorite)

    /**
     * Insert a new exercise.
     */
    suspend fun insertExercise(exercise: ExerciseEntity) = exerciseDao.insert(exercise)

    /**
     * Insert multiple exercises.
     */
    suspend fun insertAllExercises(exercises: List<ExerciseEntity>) = 
        exerciseDao.insertAll(exercises)
}

package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fittrack.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for exercise operations.
 */
@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises")
    fun getAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE muscleId = :muscleId")
    fun getByMuscle(muscleId: Int): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE regionId = :regionId")
    fun getByRegion(regionId: Int): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE exerciseId = :id")
    suspend fun getById(id: Int): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ExerciseEntity>>

    @Query("""
        SELECT * FROM exercises 
        WHERE (:muscleId IS NULL OR muscleId = :muscleId)
        AND (:regionId IS NULL OR regionId = :regionId)
        AND (:equipment IS NULL OR equipment = :equipment)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:type IS NULL OR type = :type)
        AND (:searchQuery IS NULL OR name LIKE '%' || :searchQuery || '%')
        AND (:favoritesOnly = 0 OR isFavorite = 1)
        ORDER BY name ASC
    """)
    fun filter(
        muscleId: Int? = null,
        regionId: Int? = null,
        equipment: String? = null,
        difficulty: String? = null,
        type: String? = null,
        searchQuery: String? = null,
        favoritesOnly: Boolean = false
    ): Flow<List<ExerciseEntity>>

    @Query("UPDATE exercises SET isFavorite = NOT isFavorite WHERE exerciseId = :exerciseId")
    suspend fun toggleFavorite(exerciseId: Int)

    @Query("UPDATE exercises SET isFavorite = :isFavorite WHERE exerciseId = :exerciseId")
    suspend fun setFavorite(exerciseId: Int, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}

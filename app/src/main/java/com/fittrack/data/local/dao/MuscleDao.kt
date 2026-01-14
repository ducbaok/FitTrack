package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.data.local.entity.MuscleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for muscle operations.
 */
@Dao
interface MuscleDao {

    @Query("SELECT * FROM muscles")
    fun getAll(): Flow<List<MuscleEntity>>

    @Query("SELECT * FROM muscles WHERE muscleId = :id")
    suspend fun getById(id: Int): MuscleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(muscle: MuscleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(muscles: List<MuscleEntity>)

    @Update
    suspend fun update(muscle: MuscleEntity)

    @Query("UPDATE muscles SET fatigueScore = :score, lastTrainedAt = :timestamp WHERE muscleId = :muscleId")
    suspend fun updateFatigue(muscleId: Int, score: Float, timestamp: Long)

    @Query("DELETE FROM muscles")
    suspend fun deleteAll()
}

package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.data.local.entity.MuscleRegionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for muscle region operations.
 */
@Dao
interface MuscleRegionDao {

    @Query("SELECT * FROM muscle_regions")
    fun getAll(): Flow<List<MuscleRegionEntity>>

    @Query("SELECT * FROM muscle_regions WHERE muscleId = :muscleId")
    fun getByMuscle(muscleId: Int): Flow<List<MuscleRegionEntity>>

    @Query("SELECT * FROM muscle_regions WHERE regionId = :id")
    suspend fun getById(id: Int): MuscleRegionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(region: MuscleRegionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(regions: List<MuscleRegionEntity>)

    @Update
    suspend fun update(region: MuscleRegionEntity)

    @Query("UPDATE muscle_regions SET fatigueScore = :score, lastTrainedAt = :timestamp WHERE regionId = :regionId")
    suspend fun updateFatigue(regionId: Int, score: Float, timestamp: Long)

    @Query("DELETE FROM muscle_regions")
    suspend fun deleteAll()
}

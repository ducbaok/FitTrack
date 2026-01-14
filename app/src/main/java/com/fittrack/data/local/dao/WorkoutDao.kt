package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.data.local.entity.WorkoutEntity
import com.fittrack.data.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for workout operations.
 * Sync-aware: filters deleted items and provides sync operations.
 */
@Dao
interface WorkoutDao {

    // ========== QUERIES (filter deleted items) ==========
    
    @Query("SELECT * FROM workouts WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE muscleId = :muscleId AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getHistoryByMuscle(muscleId: Int): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE timestamp BETWEEN :startTime AND :endTime AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE workoutId = :id AND isDeleted = 0")
    suspend fun getById(id: Int): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE syncId = :syncId")
    suspend fun getBySyncId(syncId: String): WorkoutEntity?

    // ========== WRITE OPERATIONS ==========
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity): Long
    
    @Update
    suspend fun update(workout: WorkoutEntity)

    /** Soft delete - marks as deleted but keeps for sync */
    @Query("UPDATE workouts SET isDeleted = 1, syncStatus = 'PENDING', updatedAt = :timestamp WHERE workoutId = :id")
    suspend fun softDelete(id: Int, timestamp: Long = System.currentTimeMillis())

    /** Hard delete - only after sync confirms deletion */
    @Query("DELETE FROM workouts WHERE workoutId = :id")
    suspend fun hardDeleteById(id: Int)

    @Query("DELETE FROM workouts")
    suspend fun deleteAll()

    // ========== SYNC OPERATIONS ==========
    
    /** Get all workouts pending sync */
    @Query("SELECT * FROM workouts WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSync(): List<WorkoutEntity>
    
    /** Get workouts modified after a timestamp (for incremental sync) */
    @Query("SELECT * FROM workouts WHERE updatedAt > :since")
    suspend fun getModifiedSince(since: Long): List<WorkoutEntity>
    
    /** Mark workout as synced */
    @Query("UPDATE workouts SET syncStatus = 'SYNCED' WHERE workoutId = :id")
    suspend fun markSynced(id: Int)
    
    /** Update sync status */
    @Query("UPDATE workouts SET syncStatus = :status WHERE workoutId = :id")
    suspend fun updateSyncStatus(id: Int, status: SyncStatus)
    
    /** Clear deleted items that have been synced */
    @Query("DELETE FROM workouts WHERE isDeleted = 1 AND syncStatus = 'SYNCED'")
    suspend fun clearSyncedDeletes()
}


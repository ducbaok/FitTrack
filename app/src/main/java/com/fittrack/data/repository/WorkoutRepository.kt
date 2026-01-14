package com.fittrack.data.repository

import com.fittrack.data.local.dao.WorkoutDao
import com.fittrack.data.local.entity.WorkoutEntity
import com.fittrack.data.remote.dto.WorkoutDto
import com.fittrack.data.sync.SyncManager
import com.fittrack.data.sync.SyncOperation
import com.fittrack.data.sync.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for workout history operations.
 * Handles recording workouts, retrieving history, and sync-aware operations.
 * Automatically queues changes for cloud sync.
 */
@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val syncManager: SyncManager
) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Get all workout history, ordered by most recent first.
     * Automatically filters out soft-deleted items.
     */
    fun getWorkoutHistory(): Flow<List<WorkoutEntity>> = workoutDao.getHistory()

    /**
     * Get workout history filtered by muscle.
     */
    fun getWorkoutHistoryByMuscle(muscleId: Int): Flow<List<WorkoutEntity>> = 
        workoutDao.getHistoryByMuscle(muscleId)

    /**
     * Get workout history within a date range.
     */
    fun getWorkoutHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<WorkoutEntity>> = 
        workoutDao.getHistoryByDateRange(startTime, endTime)

    /**
     * Get a specific workout by ID.
     */
    suspend fun getWorkoutById(id: Int): WorkoutEntity? = workoutDao.getById(id)
    
    /**
     * Get a specific workout by sync ID.
     */
    suspend fun getWorkoutBySyncId(syncId: String): WorkoutEntity? = workoutDao.getBySyncId(syncId)

    /**
     * Record a new workout. Returns the generated workout ID.
     * This should be called when user marks an exercise as done.
     * Automatically queues for cloud sync.
     */
    suspend fun recordWorkout(workout: WorkoutEntity): Long {
        val id = workoutDao.insert(workout)
        
        // Queue for sync
        queueWorkoutSync(workout.copy(workoutId = id.toInt()), SyncOperation.CREATE)
        
        return id
    }

    /**
     * Record a workout with simplified parameters.
     * Returns the generated workout ID.
     * Automatically queues for cloud sync.
     */
    suspend fun recordWorkout(
        exerciseId: Int,
        muscleId: Int,
        regionId: Int? = null,
        reps: Int? = null,
        weightKg: Float? = null
    ): Long {
        val workout = WorkoutEntity(
            exerciseId = exerciseId,
            muscleId = muscleId,
            regionId = regionId,
            timestamp = System.currentTimeMillis(),
            reps = reps,
            weightKg = weightKg
        )
        return recordWorkout(workout)
    }

    /**
     * Soft delete a workout (marks as deleted for sync, then removes after cloud confirms).
     * Use this for undo functionality.
     * Automatically queues DELETE for cloud sync.
     */
    suspend fun deleteWorkoutById(id: Int) {
        val workout = workoutDao.getById(id) ?: return
        workoutDao.softDelete(id)
        
        // Queue DELETE for sync
        queueWorkoutSync(workout.copy(isDeleted = true), SyncOperation.DELETE)
    }

    // ========== SYNC OPERATIONS ==========
    
    /**
     * Queue a workout for cloud sync.
     */
    private suspend fun queueWorkoutSync(workout: WorkoutEntity, operation: SyncOperation) {
        val dto = WorkoutDto.fromEntity(workout, workout.userId ?: "anonymous")
        val payload = json.encodeToString(dto)
        
        syncManager.queueOperation(
            entityType = "workout",
            entityId = workout.syncId,
            operation = operation,
            payload = payload
        )
    }
    
    /**
     * Get all workouts pending sync.
     */
    suspend fun getPendingSync(): List<WorkoutEntity> = workoutDao.getPendingSync()
    
    /**
     * Mark a workout as synced.
     */
    suspend fun markSynced(id: Int) = workoutDao.markSynced(id)
    
    /**
     * Update workout and mark as pending sync.
     */
    suspend fun updateWorkout(workout: WorkoutEntity) {
        val updated = workout.copy(
            syncStatus = SyncStatus.PENDING,
            updatedAt = System.currentTimeMillis()
        )
        workoutDao.update(updated)
        
        // Queue UPDATE for sync
        queueWorkoutSync(updated, SyncOperation.UPDATE)
    }
    
    /**
     * Clean up soft-deleted workouts that have been synced.
     */
    suspend fun clearSyncedDeletes() = workoutDao.clearSyncedDeletes()
}



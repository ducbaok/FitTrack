package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fittrack.data.sync.SyncStatus
import java.util.UUID

/**
 * Entity representing a completed workout.
 * Includes sync metadata for offline-first cloud synchronization.
 */
@Entity(
    tableName = "workouts",
    indices = [Index("syncId"), Index("userId")]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) 
    val workoutId: Int = 0,
    
    // User ownership
    /** User ID from Supabase Auth - null for anonymous/local-only workouts */
    val userId: String? = null,
    
    // Workout data
    val exerciseId: Int,
    val muscleId: Int,
    val regionId: Int? = null,
    val timestamp: Long,
    val reps: Int? = null,
    val weightKg: Float? = null,
    
    // Sync metadata
    /** UUID for cloud sync - unique across devices */
    val syncId: String = UUID.randomUUID().toString(),
    
    /** Current sync status */
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    
    /** Last modification timestamp for conflict resolution */
    val updatedAt: Long = System.currentTimeMillis(),
    
    /** Soft delete flag - entity is hidden but not removed until synced */
    val isDeleted: Boolean = false
)



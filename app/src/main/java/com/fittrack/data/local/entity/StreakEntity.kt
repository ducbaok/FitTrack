package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fittrack.data.sync.SyncStatus
import java.util.UUID

/**
 * Entity representing daily streak tracking.
 * Includes sync metadata for offline-first cloud synchronization.
 */
@Entity(
    tableName = "streaks",
    indices = [Index("syncId")]
)
data class StreakEntity(
    @PrimaryKey 
    val date: String, // yyyy-MM-dd format
    val completed: Boolean = false,
    
    // Sync metadata
    val syncId: String = UUID.randomUUID().toString(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val updatedAt: Long = System.currentTimeMillis()
)


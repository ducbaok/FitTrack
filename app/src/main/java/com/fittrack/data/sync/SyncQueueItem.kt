package com.fittrack.data.sync

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/**
 * Entity representing a queued sync operation.
 * Used for offline-first sync - operations are queued locally
 * and processed when network is available.
 */
@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /** Type of entity: "workout", "streak", etc. */
    val entityType: String,
    
    /** ID of the entity being synced */
    val entityId: String,
    
    /** Operation type: CREATE, UPDATE, DELETE */
    val operation: SyncOperation,
    
    /** JSON serialized entity data */
    val payload: String,
    
    /** Timestamp when operation was queued */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Number of retry attempts */
    val retryCount: Int = 0,
    
    /** Last error message if sync failed */
    val lastError: String? = null
)

/**
 * Type converters for SyncOperation enum.
 */
class SyncConverters {
    @TypeConverter
    fun fromSyncOperation(operation: SyncOperation): String = operation.name
    
    @TypeConverter
    fun toSyncOperation(value: String): SyncOperation = SyncOperation.valueOf(value)
    
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name
    
    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}

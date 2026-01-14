package com.fittrack.data.sync

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing the sync queue.
 */
@Dao
interface SyncQueueDao {
    
    /**
     * Get all pending sync items, ordered by creation time.
     */
    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    suspend fun getAllPending(): List<SyncQueueItem>
    
    /**
     * Observe all pending sync items.
     */
    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<SyncQueueItem>>
    
    /**
     * Get count of pending sync items.
     */
    @Query("SELECT COUNT(*) FROM sync_queue")
    suspend fun getPendingCount(): Int
    
    /**
     * Observe count of pending sync items.
     */
    @Query("SELECT COUNT(*) FROM sync_queue")
    fun observePendingCount(): Flow<Int>
    
    /**
     * Get items for a specific entity type.
     */
    @Query("SELECT * FROM sync_queue WHERE entityType = :entityType ORDER BY createdAt ASC")
    suspend fun getByEntityType(entityType: String): List<SyncQueueItem>
    
    /**
     * Get a specific item by entity type and ID.
     */
    @Query("SELECT * FROM sync_queue WHERE entityType = :entityType AND entityId = :entityId LIMIT 1")
    suspend fun getByEntity(entityType: String, entityId: String): SyncQueueItem?
    
    /**
     * Insert a sync item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueItem): Long
    
    /**
     * Update a sync item (e.g., increment retry count).
     */
    @Update
    suspend fun update(item: SyncQueueItem)
    
    /**
     * Delete a sync item after successful sync.
     */
    @Delete
    suspend fun delete(item: SyncQueueItem)
    
    /**
     * Delete by ID.
     */
    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    /**
     * Delete all items for an entity (e.g., when entity is deleted).
     */
    @Query("DELETE FROM sync_queue WHERE entityType = :entityType AND entityId = :entityId")
    suspend fun deleteByEntity(entityType: String, entityId: String)
    
    /**
     * Clear all sync queue items.
     */
    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
    
    /**
     * Get items with retry count less than max.
     */
    @Query("SELECT * FROM sync_queue WHERE retryCount < :maxRetries ORDER BY createdAt ASC")
    suspend fun getRetryable(maxRetries: Int = 3): List<SyncQueueItem>
}

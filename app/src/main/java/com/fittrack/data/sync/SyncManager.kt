package com.fittrack.data.sync

import android.util.Log
import com.fittrack.data.remote.SupabaseClient
import com.fittrack.data.remote.dto.WorkoutDto
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages synchronization between local Room database and Supabase cloud.
 * Implements offline-first architecture with background sync.
 */
@Singleton
class SyncManager @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val connectivityObserver: ConnectivityObserver
) {
    
    companion object {
        private const val TAG = "SyncManager"
        private const val MAX_RETRIES = 3
    }
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()
    
    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount.asStateFlow()
    
    init {
        // Observe connectivity and trigger sync when connected
        scope.launch {
            connectivityObserver.observe().collect { state ->
                if (state == ConnectionState.CONNECTED) {
                    Log.d(TAG, "Network connected - triggering sync")
                    syncAll()
                }
            }
        }
        
        // Observe pending count
        scope.launch {
            syncQueueDao.observePendingCount().collect { count ->
                _pendingCount.value = count
            }
        }
    }
    
    /**
     * Get current user ID from Supabase Auth, or null if not logged in.
     */
    private suspend fun getCurrentUserId(): String? {
        return try {
            val client = withContext(Dispatchers.Main) { SupabaseClient.client }
            client.auth.currentSessionOrNull()?.user?.id
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user ID", e)
            null
        }
    }
    
    /**
     * Queue an operation for sync.
     * This is called when local data changes.
     */
    suspend fun queueOperation(
        entityType: String,
        entityId: String,
        operation: SyncOperation,
        payload: String
    ) {
        // Check if there's already a pending operation for this entity
        val existing = syncQueueDao.getByEntity(entityType, entityId)
        
        if (existing != null) {
            // Merge operations: if existing is CREATE and new is UPDATE, keep CREATE
            // If existing is anything and new is DELETE, replace with DELETE
            val finalOperation = when {
                operation == SyncOperation.DELETE -> SyncOperation.DELETE
                existing.operation == SyncOperation.CREATE -> SyncOperation.CREATE
                else -> operation
            }
            
            syncQueueDao.update(existing.copy(
                operation = finalOperation,
                payload = payload,
                createdAt = System.currentTimeMillis()
            ))
        } else {
            syncQueueDao.insert(SyncQueueItem(
                entityType = entityType,
                entityId = entityId,
                operation = operation,
                payload = payload
            ))
        }
        
        // Try to sync immediately if connected
        if (connectivityObserver.isConnected()) {
            syncAll()
        }
    }
    
    /**
     * Sync all pending operations.
     */
    suspend fun syncAll(): SyncResult = withContext(Dispatchers.IO) {
        if (_syncState.value == SyncState.Syncing) {
            Log.d(TAG, "Sync already in progress")
            return@withContext SyncResult(success = false, message = "Sync already in progress")
        }
        
        if (!connectivityObserver.isConnected()) {
            Log.d(TAG, "No network connection")
            return@withContext SyncResult(success = false, message = "No network connection")
        }
        
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.d(TAG, "User not authenticated - skipping sync")
            return@withContext SyncResult(success = false, message = "User not authenticated")
        }
        
        _syncState.value = SyncState.Syncing
        
        try {
            val pendingItems = syncQueueDao.getRetryable(MAX_RETRIES)
            Log.d(TAG, "Syncing ${pendingItems.size} items for user $userId")
            
            var successCount = 0
            var errorCount = 0
            
            for (item in pendingItems) {
                try {
                    syncItem(item, userId)
                    syncQueueDao.delete(item)
                    successCount++
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync item ${item.id}: ${e.message}")
                    syncQueueDao.update(item.copy(
                        retryCount = item.retryCount + 1,
                        lastError = e.message
                    ))
                    errorCount++
                }
            }
            
            _syncState.value = SyncState.Idle
            
            SyncResult(
                success = errorCount == 0,
                message = "Synced $successCount items, $errorCount errors",
                syncedCount = successCount,
                errorCount = errorCount
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            _syncState.value = SyncState.Error(e.message ?: "Unknown error")
            SyncResult(success = false, message = e.message ?: "Unknown error")
        }
    }
    
    /**
     * Sync a single item to Supabase.
     */
    private suspend fun syncItem(item: SyncQueueItem, userId: String) {
        val client = withContext(Dispatchers.Main) { SupabaseClient.client }
        val tableName = getTableName(item.entityType)
        
        when (item.entityType) {
            "workout" -> syncWorkout(item, userId, client)
            else -> {
                Log.w(TAG, "Unknown entity type: ${item.entityType}")
            }
        }
    }
    
    /**
     * Sync a workout item to Supabase.
     */
    private suspend fun syncWorkout(
        item: SyncQueueItem,
        userId: String,
        client: io.github.jan.supabase.SupabaseClient
    ) {
        val table = client.postgrest["workouts"]
        
        when (item.operation) {
            SyncOperation.CREATE -> {
                Log.d(TAG, "CREATE workout ${item.entityId}")
                val dto = json.decodeFromString<WorkoutDto>(item.payload)
                table.insert(dto.copy(userId = userId))
            }
            SyncOperation.UPDATE -> {
                Log.d(TAG, "UPDATE workout ${item.entityId}")
                val dto = json.decodeFromString<WorkoutDto>(item.payload)
                table.update(dto.copy(userId = userId)) {
                    filter { eq("id", item.entityId) }
                }
            }
            SyncOperation.DELETE -> {
                Log.d(TAG, "DELETE workout ${item.entityId}")
                // Soft delete - update is_deleted flag
                table.update(mapOf("is_deleted" to true)) {
                    filter { eq("id", item.entityId) }
                }
            }
        }
    }
    
    /**
     * Map entity type to Supabase table name.
     */
    private fun getTableName(entityType: String): String {
        return when (entityType) {
            "workout" -> "workouts"
            "streak" -> "streaks"
            "exercise" -> "exercise_logs"
            else -> entityType
        }
    }
    
    /**
     * Get pending sync count.
     */
    suspend fun getPendingCount(): Int = syncQueueDao.getPendingCount()
    
    /**
     * Clear all pending sync items.
     */
    suspend fun clearQueue() {
        syncQueueDao.clearAll()
    }
}

/**
 * Represents the current sync state.
 */
sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    data class Error(val message: String) : SyncState()
}

/**
 * Result of a sync operation.
 */
data class SyncResult(
    val success: Boolean,
    val message: String,
    val syncedCount: Int = 0,
    val errorCount: Int = 0
)


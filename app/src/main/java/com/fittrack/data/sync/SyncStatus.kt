package com.fittrack.data.sync

/**
 * Represents the synchronization status of an entity.
 */
enum class SyncStatus {
    /** Entity is in sync with cloud */
    SYNCED,
    
    /** Entity has local changes not yet synced */
    PENDING,
    
    /** Merge conflict detected, requires resolution */
    CONFLICT,
    
    /** Entity is currently being synced */
    SYNCING,
    
    /** Sync failed with an error */
    ERROR
}

/**
 * Represents the type of sync operation queued.
 */
enum class SyncOperation {
    CREATE,
    UPDATE,
    DELETE
}

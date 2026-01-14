package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a workout routine (weekly/monthly schedule).
 * A routine maps days of the week to workout templates.
 * Examples: "My 4-Day Split", "PPL Routine"
 */
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val routineId: Int = 0,
    
    /** Name of the routine */
    val name: String,
    
    /** Schedule type: "weekly" or "monthly" */
    val scheduleType: String = "weekly",
    
    /** Whether this is the currently active routine */
    val isActive: Boolean = false,
    
    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Timestamp when last modified */
    val updatedAt: Long = System.currentTimeMillis()
)

package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a saved workout template.
 * A workout template is a reusable collection of exercises with target sets/reps/weight.
 * Examples: "Push Day", "Leg Day", "Upper Body"
 */
@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val templateId: Int = 0,
    
    /** Name of the workout template */
    val name: String,
    
    /** Optional description */
    val description: String = "",
    
    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis(),
    
    /** Timestamp when last modified */
    val updatedAt: Long = System.currentTimeMillis()
)

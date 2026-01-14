package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an exercise with complete details.
 */
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val exerciseId: Int,
    val name: String,
    val muscleId: Int,
    val regionId: Int? = null,
    val equipment: String,    // Dumbbell, Bodyweight, Barbell, Cable, Machine, etc.
    val difficulty: String,   // Beginner, Intermediate, Advanced
    val type: String = "Compound", // Compound, Isolation
    val instructions: String,
    val proTips: String = "",  // Pro tips for better form
    val imageUrl: String = "", // URL or resource name for exercise image
    val videoUrl: String = "",  // URL for exercise demo video
    val isFavorite: Boolean = false // Whether this exercise is favorited
)

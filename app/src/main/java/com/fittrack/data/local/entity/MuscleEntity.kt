package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a muscle group.
 */
@Entity(tableName = "muscles")
data class MuscleEntity(
    @PrimaryKey val muscleId: Int,
    val name: String,
    val svgPathId: String,
    val lastTrainedAt: Long? = null,
    val fatigueScore: Float = 0f
)

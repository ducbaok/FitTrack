package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing a region within a muscle group.
 */
@Entity(
    tableName = "muscle_regions",
    foreignKeys = [
        ForeignKey(
            entity = MuscleEntity::class,
            parentColumns = ["muscleId"],
            childColumns = ["muscleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MuscleRegionEntity(
    @PrimaryKey val regionId: Int,
    val muscleId: Int,
    val name: String,
    val svgPathId: String,
    val lastTrainedAt: Long? = null,
    val fatigueScore: Float = 0f
)

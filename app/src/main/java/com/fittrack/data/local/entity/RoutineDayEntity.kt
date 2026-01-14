package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a day within a routine.
 * Maps a day of the week to a workout template.
 */
@Entity(
    tableName = "routine_days",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routineId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["templateId"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("routineId"),
        Index("templateId")
    ]
)
data class RoutineDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    /** FK to RoutineEntity */
    val routineId: Int,
    
    /** Day of week: 1=Monday, 2=Tuesday, ..., 7=Sunday */
    val dayOfWeek: Int,
    
    /** FK to WorkoutTemplateEntity (null = rest day) */
    val templateId: Int? = null
)

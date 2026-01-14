package com.fittrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing an exercise within a workout template.
 * Links an exercise to a template with target sets, reps, and weight.
 */
@Entity(
    tableName = "workout_template_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["templateId"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("templateId"),
        Index("exerciseId")
    ]
)
data class WorkoutTemplateExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    /** FK to WorkoutTemplateEntity */
    val templateId: Int,
    
    /** FK to ExerciseEntity */
    val exerciseId: Int,
    
    /** Order of exercise in the workout (0-indexed) */
    val orderIndex: Int,
    
    /** Target number of sets */
    val targetSets: Int = 3,
    
    /** Target number of reps per set */
    val targetReps: Int = 10,
    
    /** Target weight in kg (null for bodyweight exercises) */
    val targetWeight: Float? = null
)

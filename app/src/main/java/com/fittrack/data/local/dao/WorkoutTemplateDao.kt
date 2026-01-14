package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fittrack.data.local.entity.WorkoutTemplateEntity
import com.fittrack.data.local.entity.WorkoutTemplateExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for workout template operations.
 */
@Dao
interface WorkoutTemplateDao {

    // ========== TEMPLATE OPERATIONS ==========
    
    @Query("SELECT * FROM workout_templates ORDER BY updatedAt DESC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_templates WHERE templateId = :templateId")
    suspend fun getTemplateById(templateId: Int): WorkoutTemplateEntity?

    @Query("SELECT * FROM workout_templates WHERE name LIKE '%' || :query || '%'")
    fun searchTemplates(query: String): Flow<List<WorkoutTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: WorkoutTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplateEntity)

    @Query("DELETE FROM workout_templates WHERE templateId = :templateId")
    suspend fun deleteTemplateById(templateId: Int)

    // ========== TEMPLATE EXERCISE OPERATIONS ==========

    @Query("SELECT * FROM workout_template_exercises WHERE templateId = :templateId ORDER BY orderIndex")
    fun getExercisesForTemplate(templateId: Int): Flow<List<WorkoutTemplateExerciseEntity>>

    @Query("SELECT * FROM workout_template_exercises WHERE templateId = :templateId ORDER BY orderIndex")
    suspend fun getExercisesForTemplateSync(templateId: Int): List<WorkoutTemplateExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercise(exercise: WorkoutTemplateExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTemplateExercises(exercises: List<WorkoutTemplateExerciseEntity>)

    @Update
    suspend fun updateTemplateExercise(exercise: WorkoutTemplateExerciseEntity)

    @Delete
    suspend fun deleteTemplateExercise(exercise: WorkoutTemplateExerciseEntity)

    @Query("DELETE FROM workout_template_exercises WHERE templateId = :templateId")
    suspend fun deleteAllExercisesForTemplate(templateId: Int)

    // ========== COMBINED OPERATIONS ==========

    /**
     * Save a complete workout template with all its exercises.
     * Deletes existing exercises and replaces with new ones.
     */
    @Transaction
    suspend fun saveTemplateWithExercises(
        template: WorkoutTemplateEntity,
        exercises: List<WorkoutTemplateExerciseEntity>
    ): Long {
        val templateId = insertTemplate(template)
        
        // Update exercises with the new template ID and insert
        val exercisesWithId = exercises.mapIndexed { index, exercise ->
            exercise.copy(
                templateId = templateId.toInt(),
                orderIndex = index
            )
        }
        insertAllTemplateExercises(exercisesWithId)
        
        return templateId
    }

    /**
     * Update an existing template and its exercises.
     */
    @Transaction
    suspend fun updateTemplateWithExercises(
        template: WorkoutTemplateEntity,
        exercises: List<WorkoutTemplateExerciseEntity>
    ) {
        updateTemplate(template.copy(updatedAt = System.currentTimeMillis()))
        deleteAllExercisesForTemplate(template.templateId)
        
        val exercisesWithId = exercises.mapIndexed { index, exercise ->
            exercise.copy(
                templateId = template.templateId,
                orderIndex = index
            )
        }
        insertAllTemplateExercises(exercisesWithId)
    }
}

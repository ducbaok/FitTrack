package com.fittrack.data.repository

import com.fittrack.data.local.dao.WorkoutTemplateDao
import com.fittrack.data.local.entity.WorkoutTemplateEntity
import com.fittrack.data.local.entity.WorkoutTemplateExerciseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for workout template operations.
 */
@Singleton
class WorkoutTemplateRepository @Inject constructor(
    private val templateDao: WorkoutTemplateDao
) {
    
    /**
     * Get all workout templates.
     */
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>> = templateDao.getAllTemplates()

    /**
     * Get a template by ID.
     */
    suspend fun getTemplateById(templateId: Int): WorkoutTemplateEntity? = 
        templateDao.getTemplateById(templateId)

    /**
     * Search templates by name.
     */
    fun searchTemplates(query: String): Flow<List<WorkoutTemplateEntity>> = 
        templateDao.searchTemplates(query)

    /**
     * Get exercises for a template.
     */
    fun getExercisesForTemplate(templateId: Int): Flow<List<WorkoutTemplateExerciseEntity>> = 
        templateDao.getExercisesForTemplate(templateId)

    /**
     * Get exercises for a template (suspend version).
     */
    suspend fun getExercisesForTemplateSync(templateId: Int): List<WorkoutTemplateExerciseEntity> = 
        templateDao.getExercisesForTemplateSync(templateId)

    /**
     * Create a new workout template with exercises.
     * Returns the new template ID.
     */
    suspend fun createTemplate(
        name: String,
        description: String = "",
        exercises: List<WorkoutTemplateExerciseEntity>
    ): Long {
        val template = WorkoutTemplateEntity(
            name = name,
            description = description
        )
        return templateDao.saveTemplateWithExercises(template, exercises)
    }

    /**
     * Update an existing template with exercises.
     */
    suspend fun updateTemplate(
        template: WorkoutTemplateEntity,
        exercises: List<WorkoutTemplateExerciseEntity>
    ) {
        templateDao.updateTemplateWithExercises(template, exercises)
    }

    /**
     * Delete a template.
     */
    suspend fun deleteTemplate(templateId: Int) {
        templateDao.deleteTemplateById(templateId)
    }
}

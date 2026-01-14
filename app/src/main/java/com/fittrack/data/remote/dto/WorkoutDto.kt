package com.fittrack.data.remote.dto

import com.fittrack.data.local.entity.WorkoutEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DTO for workouts table in Supabase.
 * Matches the local WorkoutEntity structure for simple sync.
 */
@Serializable
data class WorkoutDto(
    /** UUID sync ID - primary key in Supabase */
    val id: String,
    
    /** User ID from Supabase Auth */
    @SerialName("user_id")
    val userId: String,
    
    /** Exercise ID reference */
    @SerialName("exercise_id")
    val exerciseId: Int,
    
    /** Muscle ID reference */
    @SerialName("muscle_id")
    val muscleId: Int,
    
    /** Region ID (optional) */
    @SerialName("region_id")
    val regionId: Int? = null,
    
    /** Workout timestamp as ISO date string */
    @SerialName("workout_date")
    val workoutDate: String,
    
    /** Number of reps */
    val reps: Int? = null,
    
    /** Weight in kg */
    @SerialName("weight_kg")
    val weightKg: Float? = null,
    
    /** Soft delete flag */
    @SerialName("is_deleted")
    val isDeleted: Boolean = false,
    
    /** Last update timestamp as ISO date string */
    @SerialName("updated_at")
    val updatedAt: String
) {
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        
        /**
         * Convert WorkoutEntity to DTO for Supabase upload.
         */
        fun fromEntity(entity: WorkoutEntity, userId: String): WorkoutDto {
            return WorkoutDto(
                id = entity.syncId,
                userId = userId,
                exerciseId = entity.exerciseId,
                muscleId = entity.muscleId,
                regionId = entity.regionId,
                workoutDate = dateFormat.format(Date(entity.timestamp)),
                reps = entity.reps,
                weightKg = entity.weightKg,
                isDeleted = entity.isDeleted,
                updatedAt = dateFormat.format(Date(entity.updatedAt))
            )
        }
    }
    
    /**
     * Convert DTO to WorkoutEntity for local storage.
     */
    fun toEntity(): WorkoutEntity {
        return WorkoutEntity(
            userId = userId,
            exerciseId = exerciseId,
            muscleId = muscleId,
            regionId = regionId,
            timestamp = try { dateFormat.parse(workoutDate)?.time ?: 0L } catch (e: Exception) { 0L },
            reps = reps,
            weightKg = weightKg,
            syncId = id,
            syncStatus = com.fittrack.data.sync.SyncStatus.SYNCED,
            updatedAt = try { dateFormat.parse(updatedAt)?.time ?: 0L } catch (e: Exception) { 0L },
            isDeleted = isDeleted
        )
    }
}

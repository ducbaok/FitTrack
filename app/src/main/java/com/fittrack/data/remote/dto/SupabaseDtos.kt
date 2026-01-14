package com.fittrack.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO for workout_logs table in Supabase.
 * Maps to/from WorkoutEntity for sync.
 */
@Serializable
data class WorkoutLogDto(
    val id: String,
    
    @SerialName("user_id")
    val userId: String,
    
    val date: String, // ISO date format "2024-01-12"
    
    @SerialName("duration_minutes")
    val durationMinutes: Int? = null,
    
    val notes: String? = null,
    
    @SerialName("xp_earned")
    val xpEarned: Int = 0,
    
    @SerialName("is_deleted")
    val isDeleted: Boolean = false,
    
    @SerialName("created_at")
    val createdAt: String? = null,
    
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * DTO for exercise_logs table in Supabase.
 */
@Serializable
data class ExerciseLogDto(
    val id: String,
    
    @SerialName("workout_log_id")
    val workoutLogId: String,
    
    @SerialName("exercise_id")
    val exerciseId: Int?,
    
    val sets: String, // JSON array string
    
    @SerialName("total_volume")
    val totalVolume: Int? = null,
    
    @SerialName("is_personal_record")
    val isPersonalRecord: Boolean = false,
    
    @SerialName("pr_type")
    val prType: String? = null,
    
    @SerialName("fatigue_added")
    val fatigueAdded: Float? = null,
    
    @SerialName("is_deleted")
    val isDeleted: Boolean = false,
    
    @SerialName("created_at")
    val createdAt: String? = null,
    
    @SerialName("updated_at")
    val updatedAt: String? = null
)

/**
 * DTO for users table in Supabase.
 */
@Serializable
data class UserDto(
    val id: String,
    val email: String,
    
    @SerialName("display_name")
    val displayName: String? = null,
    
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    
    val level: Int = 1,
    
    @SerialName("total_xp")
    val totalXp: Int = 0,
    
    @SerialName("current_streak")
    val currentStreak: Int = 0,
    
    @SerialName("longest_streak")
    val longestStreak: Int = 0,
    
    @SerialName("created_at")
    val createdAt: String? = null,
    
    @SerialName("updated_at")
    val updatedAt: String? = null
)

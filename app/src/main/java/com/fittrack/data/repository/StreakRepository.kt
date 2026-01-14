package com.fittrack.data.repository

import com.fittrack.data.local.dao.StreakDao
import com.fittrack.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for streak tracking operations.
 * Manages daily workout streaks and badge calculations.
 */
@Singleton
class StreakRepository @Inject constructor(
    private val streakDao: StreakDao
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Get all streak records.
     */
    fun getAllStreaks(): Flow<List<StreakEntity>> = streakDao.getAll()

    /**
     * Check if a workout was completed on a specific date.
     */
    suspend fun getStreakByDate(date: LocalDate): StreakEntity? = 
        streakDao.getByDate(date.format(dateFormatter))

    /**
     * Mark today as completed (workout done).
     */
    suspend fun markTodayCompleted() {
        val today = LocalDate.now().format(dateFormatter)
        streakDao.upsert(StreakEntity(date = today, completed = true))
    }

    /**
     * Mark a specific date as completed.
     */
    suspend fun markDateCompleted(date: LocalDate) {
        streakDao.upsert(StreakEntity(date = date.format(dateFormatter), completed = true))
    }

    /**
     * Get the current consecutive streak count.
     */
    suspend fun getCurrentStreak(): Int {
        val today = LocalDate.now().format(dateFormatter)
        return streakDao.getCurrentStreak(today)
    }

    /**
     * Get the longest streak achieved.
     */
    suspend fun getLongestStreak(): Int = streakDao.getLongestStreak() ?: 0

    /**
     * Check if user has earned the 7-day streak badge.
     */
    suspend fun has7DayBadge(): Boolean = getCurrentStreak() >= 7

    /**
     * Check if user has earned the 30-day streak badge.
     */
    suspend fun has30DayBadge(): Boolean = getCurrentStreak() >= 30

    /**
     * Get all earned badges based on current streak.
     */
    suspend fun getEarnedBadges(): List<StreakBadge> {
        val currentStreak = getCurrentStreak()
        return buildList {
            if (currentStreak >= 7) add(StreakBadge.SEVEN_DAY)
            if (currentStreak >= 30) add(StreakBadge.THIRTY_DAY)
            if (currentStreak >= 100) add(StreakBadge.HUNDRED_DAY)
        }
    }
}

/**
 * Enum representing streak achievement badges.
 */
enum class StreakBadge(val displayName: String, val daysRequired: Int) {
    SEVEN_DAY("7-Day Warrior", 7),
    THIRTY_DAY("Monthly Champion", 30),
    HUNDRED_DAY("Century Club", 100)
}

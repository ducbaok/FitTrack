package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fittrack.data.local.entity.StreakEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for streak operations.
 */
@Dao
interface StreakDao {

    @Query("SELECT * FROM streaks ORDER BY date DESC")
    fun getAll(): Flow<List<StreakEntity>>

    @Query("SELECT * FROM streaks WHERE date = :date")
    suspend fun getByDate(date: String): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(streak: StreakEntity)

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT date FROM streaks 
            WHERE completed = 1 
            AND date <= :today
            ORDER BY date DESC
        ) WHERE date >= date(:today, '-' || (SELECT COUNT(*) - 1 FROM streaks WHERE completed = 1 AND date <= :today) || ' days')
    """)
    suspend fun getCurrentStreak(today: String): Int

    @Query("SELECT MAX(streak_count) FROM (SELECT COUNT(*) as streak_count FROM streaks WHERE completed = 1 GROUP BY date)")
    suspend fun getLongestStreak(): Int?

    @Query("DELETE FROM streaks")
    suspend fun deleteAll()
}

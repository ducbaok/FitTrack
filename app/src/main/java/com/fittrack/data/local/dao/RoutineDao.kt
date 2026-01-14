package com.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fittrack.data.local.entity.RoutineDayEntity
import com.fittrack.data.local.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for routine operations.
 */
@Dao
interface RoutineDao {

    // ========== ROUTINE OPERATIONS ==========
    
    @Query("SELECT * FROM routines ORDER BY updatedAt DESC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE routineId = :routineId")
    suspend fun getRoutineById(routineId: Int): RoutineEntity?

    @Query("SELECT * FROM routines WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveRoutine(): RoutineEntity?

    @Query("SELECT * FROM routines WHERE isActive = 1 LIMIT 1")
    fun getActiveRoutineFlow(): Flow<RoutineEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routines WHERE routineId = :routineId")
    suspend fun deleteRoutineById(routineId: Int)

    @Query("UPDATE routines SET isActive = 0")
    suspend fun deactivateAllRoutines()

    @Query("UPDATE routines SET isActive = 1 WHERE routineId = :routineId")
    suspend fun setActiveRoutine(routineId: Int)

    // ========== ROUTINE DAY OPERATIONS ==========

    @Query("SELECT * FROM routine_days WHERE routineId = :routineId ORDER BY dayOfWeek")
    fun getDaysForRoutine(routineId: Int): Flow<List<RoutineDayEntity>>

    @Query("SELECT * FROM routine_days WHERE routineId = :routineId ORDER BY dayOfWeek")
    suspend fun getDaysForRoutineSync(routineId: Int): List<RoutineDayEntity>

    @Query("SELECT * FROM routine_days WHERE routineId = :routineId AND dayOfWeek = :dayOfWeek")
    suspend fun getDayForRoutine(routineId: Int, dayOfWeek: Int): RoutineDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineDay(day: RoutineDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRoutineDays(days: List<RoutineDayEntity>)

    @Update
    suspend fun updateRoutineDay(day: RoutineDayEntity)

    @Query("DELETE FROM routine_days WHERE routineId = :routineId")
    suspend fun deleteAllDaysForRoutine(routineId: Int)

    // ========== COMBINED OPERATIONS ==========

    /**
     * Save a complete routine with all its days.
     */
    @Transaction
    suspend fun saveRoutineWithDays(
        routine: RoutineEntity,
        days: List<RoutineDayEntity>
    ): Long {
        val routineId = insertRoutine(routine)
        
        val daysWithId = days.map { day ->
            day.copy(routineId = routineId.toInt())
        }
        insertAllRoutineDays(daysWithId)
        
        return routineId
    }

    /**
     * Update an existing routine and its days.
     */
    @Transaction
    suspend fun updateRoutineWithDays(
        routine: RoutineEntity,
        days: List<RoutineDayEntity>
    ) {
        updateRoutine(routine.copy(updatedAt = System.currentTimeMillis()))
        deleteAllDaysForRoutine(routine.routineId)
        
        val daysWithId = days.map { day ->
            day.copy(routineId = routine.routineId)
        }
        insertAllRoutineDays(daysWithId)
    }

    /**
     * Set a routine as active (deactivates all others first).
     */
    @Transaction
    suspend fun activateRoutine(routineId: Int) {
        deactivateAllRoutines()
        setActiveRoutine(routineId)
    }
}

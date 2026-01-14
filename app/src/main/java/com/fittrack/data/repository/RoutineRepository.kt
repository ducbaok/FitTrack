package com.fittrack.data.repository

import com.fittrack.data.local.dao.RoutineDao
import com.fittrack.data.local.entity.RoutineDayEntity
import com.fittrack.data.local.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for routine operations.
 */
@Singleton
class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao
) {
    
    /**
     * Get all routines.
     */
    fun getAllRoutines(): Flow<List<RoutineEntity>> = routineDao.getAllRoutines()

    /**
     * Get a routine by ID.
     */
    suspend fun getRoutineById(routineId: Int): RoutineEntity? = 
        routineDao.getRoutineById(routineId)

    /**
     * Get the active routine.
     */
    suspend fun getActiveRoutine(): RoutineEntity? = routineDao.getActiveRoutine()

    /**
     * Get active routine as Flow.
     */
    fun getActiveRoutineFlow(): Flow<RoutineEntity?> = routineDao.getActiveRoutineFlow()

    /**
     * Get days for a routine.
     */
    fun getDaysForRoutine(routineId: Int): Flow<List<RoutineDayEntity>> = 
        routineDao.getDaysForRoutine(routineId)

    /**
     * Get days for a routine (sync).
     */
    suspend fun getDaysForRoutineSync(routineId: Int): List<RoutineDayEntity> = 
        routineDao.getDaysForRoutineSync(routineId)

    /**
     * Create a new routine with days.
     */
    suspend fun createRoutine(
        name: String,
        days: List<RoutineDayEntity>
    ): Long {
        val routine = RoutineEntity(name = name)
        return routineDao.saveRoutineWithDays(routine, days)
    }

    /**
     * Update a routine with days.
     */
    suspend fun updateRoutine(
        routine: RoutineEntity,
        days: List<RoutineDayEntity>
    ) {
        routineDao.updateRoutineWithDays(routine, days)
    }

    /**
     * Set a routine as active.
     */
    suspend fun activateRoutine(routineId: Int) {
        routineDao.activateRoutine(routineId)
    }

    /**
     * Delete a routine.
     */
    suspend fun deleteRoutine(routineId: Int) {
        routineDao.deleteRoutineById(routineId)
    }
}

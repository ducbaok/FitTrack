package com.fittrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fittrack.data.local.dao.ExerciseDao
import com.fittrack.data.local.dao.MuscleDao
import com.fittrack.data.local.dao.MuscleRegionDao
import com.fittrack.data.local.dao.RoutineDao
import com.fittrack.data.local.dao.StreakDao
import com.fittrack.data.local.dao.WorkoutDao
import com.fittrack.data.local.dao.WorkoutTemplateDao
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.data.local.entity.MuscleEntity
import com.fittrack.data.local.entity.MuscleRegionEntity
import com.fittrack.data.local.entity.RoutineDayEntity
import com.fittrack.data.local.entity.RoutineEntity
import com.fittrack.data.local.entity.StreakEntity
import com.fittrack.data.local.entity.WorkoutEntity
import com.fittrack.data.local.entity.WorkoutTemplateEntity
import com.fittrack.data.local.entity.WorkoutTemplateExerciseEntity
import com.fittrack.data.sync.SyncConverters
import com.fittrack.data.sync.SyncQueueDao
import com.fittrack.data.sync.SyncQueueItem

/**
 * Main Room database for FitTrack app.
 * Contains all entities: Muscles, MuscleRegions, Exercises, Workouts, Streaks, WorkoutTemplates, Routines, and SyncQueue.
 */
@Database(
    entities = [
        MuscleEntity::class,
        MuscleRegionEntity::class,
        ExerciseEntity::class,
        WorkoutEntity::class,
        StreakEntity::class,
        SyncQueueItem::class,
        WorkoutTemplateEntity::class,
        WorkoutTemplateExerciseEntity::class,
        RoutineEntity::class,
        RoutineDayEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(SyncConverters::class)
abstract class FitTrackDatabase : RoomDatabase() {
    
    abstract fun muscleDao(): MuscleDao
    
    abstract fun muscleRegionDao(): MuscleRegionDao
    
    abstract fun exerciseDao(): ExerciseDao
    
    abstract fun workoutDao(): WorkoutDao
    
    abstract fun streakDao(): StreakDao
    
    abstract fun syncQueueDao(): SyncQueueDao
    
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    
    abstract fun routineDao(): RoutineDao
}

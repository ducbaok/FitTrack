package com.fittrack.di

import android.content.Context
import androidx.room.Room
import com.fittrack.data.local.DatabaseCallback
import com.fittrack.data.local.FitTrackDatabase
import com.fittrack.data.local.dao.ExerciseDao
import com.fittrack.data.local.dao.MuscleDao
import com.fittrack.data.local.dao.MuscleRegionDao
import com.fittrack.data.local.dao.StreakDao
import com.fittrack.data.local.dao.WorkoutDao
import com.fittrack.data.local.dao.WorkoutTemplateDao
import com.fittrack.data.local.dao.RoutineDao
import com.fittrack.data.sync.SyncQueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFitTrackDatabase(@ApplicationContext context: Context): FitTrackDatabase {
        return Room.databaseBuilder(
            context,
            FitTrackDatabase::class.java,
            "fittrack_database"
        )
        .addCallback(DatabaseCallback(context))
        .fallbackToDestructiveMigration() // For development - remove in production
        .build()
    }

    @Provides
    @Singleton
    fun provideMuscleDao(database: FitTrackDatabase): MuscleDao {
        return database.muscleDao()
    }

    @Provides
    @Singleton
    fun provideMuscleRegionDao(database: FitTrackDatabase): MuscleRegionDao {
        return database.muscleRegionDao()
    }

    @Provides
    @Singleton
    fun provideExerciseDao(database: FitTrackDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(database: FitTrackDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    @Singleton
    fun provideStreakDao(database: FitTrackDatabase): StreakDao {
        return database.streakDao()
    }

    @Provides
    @Singleton
    fun provideSyncQueueDao(database: FitTrackDatabase): SyncQueueDao {
        return database.syncQueueDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutTemplateDao(database: FitTrackDatabase): WorkoutTemplateDao {
        return database.workoutTemplateDao()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(database: FitTrackDatabase): RoutineDao {
        return database.routineDao()
    }
}

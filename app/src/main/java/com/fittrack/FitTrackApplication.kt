package com.fittrack

import android.app.Application
import com.fittrack.data.local.ExerciseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main Application class for FitTrack.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation.
 */
@HiltAndroidApp
class FitTrackApplication : Application() {
    
    @Inject
    lateinit var exerciseSeeder: ExerciseSeeder
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Supabase Client with context for persistence
        com.fittrack.data.remote.SupabaseClient.init(this)
        
        // Seed exercises on first launch
        applicationScope.launch {
            try {
                exerciseSeeder.seedIfEmpty()
            } catch (e: Exception) {
                android.util.Log.e("FitTrackApp", "Failed to seed exercises", e)
            }
        }
    }
}

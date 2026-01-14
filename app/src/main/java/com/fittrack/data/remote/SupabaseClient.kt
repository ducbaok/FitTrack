package com.fittrack.data.remote

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.fittrack.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionManager
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Singleton Supabase client for FitTrack.
 */
object SupabaseClient {
    
    private const val TAG = "SupabaseClient"
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        Log.d(TAG, "SupabaseClient initialized with context")
    }

    val client by lazy {
        if (!::appContext.isInitialized) {
            throw IllegalStateException("SupabaseClient.init(context) must be called before accessing client")
        }

        Log.d(TAG, "Creating Supabase client instance")

        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            
            install(Auth) {
                scheme = "fittrack"
                host = "login"
                // Switching to standard SharedPreferences for debugging/stability
                sessionManager = StandardSharedPrefsSessionManager(appContext)
            }
            
            install(Realtime)
        }
    }
}

/**
 * Session manager using standard SharedPreferences (for debugging persistence issues).
 */
class StandardSharedPrefsSessionManager(private val context: Context) : SessionManager {

    private val TAG = "SessionManager"
    private val prefs: SharedPreferences = context.getSharedPreferences("supabase_session_plain", Context.MODE_PRIVATE)
    
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveSession(session: UserSession) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Saving session...")
                val sessionJson = json.encodeToString(session)
                prefs.edit().putString("session", sessionJson).apply()
                Log.d(TAG, "Session saved successfully. Length: ${sessionJson.length}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save session", e)
            }
        }
    }

    override suspend fun loadSession(): UserSession? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Loading session...")
                val sessionJson = prefs.getString("session", null)
                if (sessionJson != null) {
                    Log.d(TAG, "Session found in storage.")
                    val session = json.decodeFromString<UserSession>(sessionJson)
                    Log.d(TAG, "Session decoded successfully. User: ${session.user?.id}")
                    session
                } else {
                    Log.d(TAG, "No session found in storage.")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load session", e)
                null
            }
        }
    }

    override suspend fun deleteSession() {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Deleting session")
            prefs.edit().remove("session").apply()
        }
    }
}

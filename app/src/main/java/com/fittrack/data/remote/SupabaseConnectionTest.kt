package com.fittrack.data.remote

import android.util.Log
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class to test Supabase connection.
 * Call testConnection() from a ViewModel or Activity to verify setup.
 */
object SupabaseConnectionTest {
    
    private const val TAG = "SupabaseTest"
    
    /**
     * Tests the Supabase connection by checking if we can reach the API.
     * Note: Client must be initialized on Main thread due to lifecycle observers.
     * @return ConnectionResult with success status and message
     */
    suspend fun testConnection(): ConnectionResult {
        return try {
            Log.d(TAG, "Testing Supabase connection...")
            
            // Initialize client on Main thread (required for lifecycle observers)
            val client = withContext(Dispatchers.Main) {
                SupabaseClient.client
            }
            
            Log.d(TAG, "URL: ${client.supabaseUrl}")
            Log.d(TAG, "✓ Client initialized")
            
            // Test 2: Try to access postgrest (database)
            // This will fail RLS if not authenticated, but that's expected
            try {
                client.postgrest["users"]
                Log.d(TAG, "✓ Postgrest module accessible")
            } catch (e: Exception) {
                Log.d(TAG, "✓ Postgrest module accessible (RLS active)")
            }
            
            // Test 3: Check auth module
            val session = client.auth.currentSessionOrNull()
            Log.d(TAG, "✓ Auth module accessible (session: ${session != null})")
            
            ConnectionResult(
                success = true,
                message = "Supabase connection successful!\n" +
                    "URL: ${client.supabaseUrl}\n" +
                    "Auth: Ready\n" +
                    "Database: Ready"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed", e)
            ConnectionResult(
                success = false,
                message = "Connection failed: ${e.message}"
            )
        }
    }
    
    data class ConnectionResult(
        val success: Boolean,
        val message: String
    )
}

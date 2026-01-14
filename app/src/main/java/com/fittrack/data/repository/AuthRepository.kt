package com.fittrack.data.repository

import android.util.Log
import com.fittrack.data.remote.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling authentication operations with Supabase.
 */
@Singleton
class AuthRepository @Inject constructor() {

    companion object {
        private const val TAG = "AuthRepository"
    }

    private val _isLoggedIn = MutableStateFlow(false)

    /**
     * Check if user is currently logged in.
     * Returns cached state. Updated after signIn/signOut.
     */
    val isLoggedIn: Boolean
        get() = _isLoggedIn.value

    /**
     * Get the current user's UID from Supabase.
     */
    val currentUserId: String?
        get() {
            return try {
                SupabaseClient.client.auth.currentSessionOrNull()?.user?.id
            } catch (e: Exception) {
                null
            }
        }

    /**
     * Observe authentication state changes.
     */
    val authStateFlow: Flow<Boolean> = _isLoggedIn.asStateFlow()

    // Note: Don't access SupabaseClient in init - it may not be initialized yet
    // Login state is checked lazily via currentUserId property

    /**
     * Sign in with email and password.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                val client = withContext(Dispatchers.Main) { SupabaseClient.client }
                
                client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val userId = client.auth.currentSessionOrNull()?.user?.id
                if (userId != null) {
                    _isLoggedIn.value = true
                    Log.d(TAG, "Sign in successful: $userId")
                    Result.success(userId)
                } else {
                    Result.failure(Exception("Sign in failed - no user ID"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
                Result.failure(e)
            }
        }

    /**
     * Create new account with email and password.
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                val client = withContext(Dispatchers.Main) { SupabaseClient.client }
                
                client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val userId = client.auth.currentSessionOrNull()?.user?.id
                if (userId != null) {
                    _isLoggedIn.value = true
                    Log.d(TAG, "Sign up successful: $userId")
                    Result.success(userId)
                } else {
                    // User might need to confirm email
                    Log.d(TAG, "Sign up successful - check email for confirmation")
                    Result.success("pending_confirmation")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign up failed", e)
                Result.failure(e)
            }
        }

    /**
     * Sign in with Google ID token (OAuth).
     * Note: Requires Google OAuth setup in Supabase dashboard.
     */
    suspend fun signInWithGoogle(idToken: String): Result<String> = 
        withContext(Dispatchers.IO) {
            try {
                // TODO: Implement Google OAuth with Supabase
                // This requires additional setup with Google Cloud Console
                Result.failure(Exception("Google Sign-In not yet configured"))
            } catch (e: Exception) {
                Log.e(TAG, "Google sign in failed", e)
                Result.failure(e)
            }
        }

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = 
        withContext(Dispatchers.IO) {
            try {
                val client = withContext(Dispatchers.Main) { SupabaseClient.client }
                client.auth.resetPasswordForEmail(email)
                Log.d(TAG, "Password reset email sent to $email")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Password reset failed", e)
                Result.failure(e)
            }
        }

    /**
     * Sign out current user.
     */
    fun signOut() {
        try {
            // Sign out is blocking in some SDK versions, handle carefully
            kotlinx.coroutines.runBlocking {
                withContext(Dispatchers.Main) {
                    SupabaseClient.client.auth.signOut()
                }
            }
            _isLoggedIn.value = false
            Log.d(TAG, "Sign out successful")
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed", e)
            _isLoggedIn.value = false
        }
    }
    
    /**
     * Refresh the session if needed.
     */
    suspend fun refreshSession(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Attempting to refresh session/check persistence...")
            val client = withContext(Dispatchers.Main) { SupabaseClient.client }
            
            // Wait a brief moment for async load if checking very early
            // (Standard Supabase Auth loads from storage via coroutine in init)
            var attempts = 0
            while (client.auth.currentSessionOrNull() == null && attempts < 3) {
                 // Check if we can detect if it is STILL loading?
                 // For now, just a tiny nap if we are super early.
                 // Ideally, we shouldn't need this if 'install' blocks, but it often doesn't.
                 delay(50)
                 attempts++
            }
            
            // Try explicit refresh
            client.auth.refreshCurrentSession()
            
            val userId = client.auth.currentSessionOrNull()?.user?.id
            _isLoggedIn.value = userId != null
            
            Log.d(TAG, "Session check complete. Is logged in: ${userId != null}")
            userId != null
        } catch (e: Exception) {
            Log.e(TAG, "Session refresh failed", e)
            // If exception, we might simply be not logged in or token invalid
            false
        }
    }
}

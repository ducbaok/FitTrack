package com.fittrack.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.repository.AuthRepository
import com.fittrack.data.repository.WorkoutRepository
import com.fittrack.data.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Profile screen.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _signedOut = MutableStateFlow(false)
    val signedOut: StateFlow<Boolean> = _signedOut.asStateFlow()

    init {
        loadProfile()
        observeSyncStatus()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val email = authRepository.currentUserId?.let { "User: $it" }
            val workouts = workoutRepository.getPendingSync()
            
            _profileState.value = _profileState.value.copy(
                email = email,
                workoutCount = workouts.size
            )
        }
    }

    private fun observeSyncStatus() {
        viewModelScope.launch {
            syncManager.pendingCount.collect { count ->
                _profileState.value = _profileState.value.copy(
                    pendingSyncCount = count
                )
            }
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            syncManager.syncAll()
        }
    }

    fun signOut() {
        authRepository.signOut()
        _signedOut.value = true
    }
}

/**
 * UI state for Profile screen.
 */
data class ProfileState(
    val email: String? = null,
    val userName: String? = "John Doe",
    val workoutCount: Int = 156,
    val hoursCount: Int = 89,
    val currentStreak: Int = 12,
    val level: Int = 24,
    val currentXp: Int = 750,
    val nextLevelXp: Int = 1000,
    val fitnessGoals: String? = "Build Muscle",
    val height: String? = "5'10\"",
    val weight: String? = "175 lbs",
    val pendingSyncCount: Int = 0
)


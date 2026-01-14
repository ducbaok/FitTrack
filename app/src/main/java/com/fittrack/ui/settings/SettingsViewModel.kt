package com.fittrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Data class for settings state.
 */
data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val language: String = "English"
)

/**
 * ViewModel for the Settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(notificationsEnabled = enabled)
        // TODO: Persist to preferences
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        _settingsState.value = _settingsState.value.copy(darkModeEnabled = enabled)
        // TODO: Apply theme and persist
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            _loggedOut.value = true
        }
    }
}

package com.fittrack.ui.auth

/**
 * Sealed class representing authentication UI state.
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Data class representing login form state.
 */
data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isValid: Boolean
        get() = email.isNotBlank() && password.length >= 6 && emailError == null && passwordError == null
}

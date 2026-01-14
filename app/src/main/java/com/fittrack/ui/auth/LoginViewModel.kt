package com.fittrack.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login/Register screen.
 * Handles authentication logic and form validation.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    /**
     * Check if user is already logged in.
     */
    fun checkLoginStatus() {
        if (authRepository.isLoggedIn) {
            authRepository.currentUserId?.let { uid ->
                _authState.value = AuthState.Success(uid)
            }
        }
    }

    /**
     * Update email field.
     */
    fun updateEmail(email: String) {
        _formState.update { it.copy(
            email = email,
            emailError = validateEmail(email)
        )}
    }

    /**
     * Update password field.
     */
    fun updatePassword(password: String) {
        _formState.update { it.copy(
            password = password,
            passwordError = validatePassword(password)
        )}
    }

    /**
     * Update remember me toggle.
     */
    fun updateRememberMe(rememberMe: Boolean) {
        _formState.update { it.copy(rememberMe = rememberMe) }
    }

    /**
     * Sign in with email and password.
     */
    fun signIn() {
        val form = _formState.value
        if (!form.isValid) {
            _authState.value = AuthState.Error("Please fill in all fields correctly")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInWithEmail(form.email, form.password)
                .onSuccess { userId ->
                    _authState.value = AuthState.Success(userId)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Sign in failed"
                    )
                }
        }
    }

    /**
     * Create a new account with email and password.
     */
    fun signUp() {
        val form = _formState.value
        if (!form.isValid) {
            _authState.value = AuthState.Error("Please fill in all fields correctly")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUpWithEmail(form.email, form.password)
                .onSuccess { userId ->
                    _authState.value = AuthState.Success(userId)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Sign up failed"
                    )
                }
        }
    }

    /**
     * Sign in with Google ID token.
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInWithGoogle(idToken)
                .onSuccess { userId ->
                    _authState.value = AuthState.Success(userId)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Google sign in failed"
                    )
                }
        }
    }

    /**
     * Send password reset email.
     */
    fun resetPassword() {
        val email = _formState.value.email
        if (email.isBlank() || validateEmail(email) != null) {
            _authState.value = AuthState.Error("Please enter a valid email")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _authState.value = AuthState.Idle
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(
                        error.message ?: "Failed to send reset email"
                    )
                }
        }
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
        _formState.value = LoginFormState()
    }

    /**
     * Clear error state.
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}

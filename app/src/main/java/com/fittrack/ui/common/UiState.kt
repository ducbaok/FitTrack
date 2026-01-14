package com.fittrack.ui.common

/**
 * Sealed class representing UI state for ViewModels.
 * Provides a consistent way to handle loading, success, and error states.
 */
sealed class UiState<out T> {
    /**
     * Initial state before any data loading.
     */
    data object Initial : UiState<Nothing>()

    /**
     * Loading state while data is being fetched.
     */
    data object Loading : UiState<Nothing>()

    /**
     * Success state with the loaded data.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error state with an error message.
     */
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()

    /**
     * Check if this state represents loading.
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Check if this state represents success.
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Check if this state represents error.
     */
    val isError: Boolean get() = this is Error

    /**
     * Get the data if in success state, null otherwise.
     */
    fun getOrNull(): T? = (this as? Success)?.data
}

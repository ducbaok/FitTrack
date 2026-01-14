package com.fittrack.ui.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base ViewModel class providing common functionality for all ViewModels.
 * Includes standardized UI state management.
 */
abstract class BaseViewModel<T> : ViewModel() {

    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Initial)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    /**
     * Set the UI state to loading.
     */
    protected fun setLoading() {
        _uiState.value = UiState.Loading
    }

    /**
     * Set the UI state to success with data.
     */
    protected fun setSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }

    /**
     * Set the UI state to error with message.
     */
    protected fun setError(message: String, throwable: Throwable? = null) {
        _uiState.value = UiState.Error(message, throwable)
    }

    /**
     * Reset the UI state to initial.
     */
    protected fun resetState() {
        _uiState.value = UiState.Initial
    }
}

package com.fittrack.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.domain.usecase.FatigueColor
import com.fittrack.domain.usecase.HeatmapUseCase
import com.fittrack.domain.usecase.MuscleWithFatigue
import com.fittrack.domain.usecase.RecommendationUseCase
import com.fittrack.data.remote.SupabaseConnectionTest
import com.fittrack.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen with anatomy view.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val heatmapUseCase: HeatmapUseCase,
    private val recommendationUseCase: RecommendationUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _musclesWithFatigue = MutableStateFlow<List<MuscleWithFatigue>>(emptyList())
    val musclesWithFatigue: StateFlow<List<MuscleWithFatigue>> = _musclesWithFatigue.asStateFlow()

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    val selectedMuscle: StateFlow<String?> = _selectedMuscle.asStateFlow()

    // Supabase connection test result
    private val _supabaseTestResult = MutableStateFlow<String?>(null)
    val supabaseTestResult: StateFlow<String?> = _supabaseTestResult.asStateFlow()

    init {
        loadMuscleData()
        observeGender()
    }

    private fun observeGender() {
        viewModelScope.launch {
            settingsRepository.gender.collectLatest { gender ->
                _uiState.value = _uiState.value.copy(gender = gender)
            }
        }
    }

    private fun loadMuscleData() {
        viewModelScope.launch {
            heatmapUseCase.getMusclesWithFatigue().collectLatest { muscles ->
                _musclesWithFatigue.value = muscles
                
                // Build fatigue color map for AnatomyView
                val colorMap = muscles.associate { 
                    it.muscle.svgPathId to it.color 
                }
                _uiState.value = _uiState.value.copy(
                    fatigueColors = colorMap,
                    isLoading = false
                )
            }
        }
    }

    /**
     * Test Supabase connection - call this to verify setup.
     */
    fun testSupabaseConnection() {
        viewModelScope.launch {
            _supabaseTestResult.value = "Testing connection..."
            val result = SupabaseConnectionTest.testConnection()
            _supabaseTestResult.value = if (result.success) {
                "✅ ${result.message}"
            } else {
                "❌ ${result.message}"
            }
        }
    }

    /**
     * Clear Supabase test result.
     */
    fun clearSupabaseTestResult() {
        _supabaseTestResult.value = null
    }

    /**
     * Toggle gender between male and female.
     */
    fun toggleGender() {
        val newGender = if (_uiState.value.gender == Gender.MALE) Gender.FEMALE else Gender.MALE
        settingsRepository.setGender(newGender)
    }

    /**
     * Set specific gender.
     */
    fun setGender(gender: Gender) {
        settingsRepository.setGender(gender)
    }

    /**
     * Toggle body view between front and back.
     */
    fun toggleBodyView() {
        val newView = if (_uiState.value.bodyView == BodyView.FRONT) BodyView.BACK else BodyView.FRONT
        _uiState.value = _uiState.value.copy(bodyView = newView)
    }

    /**
     * Set specific body view.
     */
    fun setBodyView(bodyView: BodyView) {
        _uiState.value = _uiState.value.copy(bodyView = bodyView)
    }

    /**
     * Set detail level (Basic/Advanced).
     */
    fun setDetailLevel(detailLevel: DetailLevel) {
        _uiState.value = _uiState.value.copy(detailLevel = detailLevel)
    }

    /**
     * Toggle fatigue heatmap visibility.
     */
    fun setShowFatigue(show: Boolean) {
        _uiState.value = _uiState.value.copy(showFatigue = show)
    }

    /**
     * Handle muscle selection from AnatomyView.
     */
    fun onMuscleSelected(muscleId: String) {
        _selectedMuscle.value = muscleId
    }

    /**
     * Clear muscle selection.
     */
    fun clearMuscleSelection() {
        _selectedMuscle.value = null
    }
}

/**
 * Detail level for anatomy view.
 */
enum class DetailLevel {
    BASIC,
    ADVANCED
}

/**
 * UI state for Home screen.
 */
data class HomeUiState(
    val gender: Gender = Gender.MALE,
    val bodyView: BodyView = BodyView.FRONT,
    val detailLevel: DetailLevel = DetailLevel.BASIC,
    val fatigueColors: Map<String, FatigueColor> = emptyMap(),
    val showFatigue: Boolean = true,
    val isLoading: Boolean = true
)


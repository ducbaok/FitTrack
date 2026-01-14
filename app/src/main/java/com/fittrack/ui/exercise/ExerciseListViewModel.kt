package com.fittrack.ui.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Exercise List screen.
 * Handles filtering exercises by muscle, equipment, type, difficulty, and search.
 */
@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get muscleId from navigation arguments
    private val muscleId: Int? = savedStateHandle["muscleId"]

    private val _uiState = MutableStateFlow(ExerciseListUiState())
    val uiState: StateFlow<ExerciseListUiState> = _uiState.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntity>> = _exercises.asStateFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val state = _uiState.value
            exerciseRepository.filterExercises(
                muscleId = muscleId,
                equipment = state.selectedCategory,
                difficulty = state.selectedDifficulty,
                type = state.selectedType,
                searchQuery = state.searchQuery.takeIf { it.isNotBlank() },
                favoritesOnly = state.showFavoritesOnly
            ).collectLatest { exerciseList ->
                _exercises.value = exerciseList
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * Filter by category (equipment type) or Favorites.
     */
    fun setCategoryFilter(category: String?) {
        if (category == "Favorites") {
            _uiState.value = _uiState.value.copy(
                selectedCategory = null,
                showFavoritesOnly = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                selectedCategory = category,
                showFavoritesOnly = false
            )
        }
        loadExercises()
    }

    /**
     * Filter by exercise type (Compound/Isolation).
     */
    fun setTypeFilter(type: String?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        loadExercises()
    }

    /**
     * Filter by difficulty level.
     */
    fun setDifficultyFilter(difficulty: String?) {
        _uiState.value = _uiState.value.copy(selectedDifficulty = difficulty)
        loadExercises()
    }

    /**
     * Set search query.
     */
    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadExercises()
    }

    /**
     * Toggle search bar visibility.
     */
    fun toggleSearch() {
        val newShowSearch = !_uiState.value.showSearch
        _uiState.value = _uiState.value.copy(
            showSearch = newShowSearch,
            searchQuery = if (!newShowSearch) "" else _uiState.value.searchQuery
        )
        if (!newShowSearch) {
            loadExercises() // Reload without search filter
        }
    }

    /**
     * Toggle favorite status for an exercise.
     */
    fun toggleFavorite(exerciseId: Int) {
        viewModelScope.launch {
            exerciseRepository.toggleFavorite(exerciseId)
        }
    }

    /**
     * Clear all filters.
     */
    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            selectedType = null,
            selectedDifficulty = null,
            showFavoritesOnly = false
        )
        loadExercises()
    }

    companion object {
        val CATEGORY_OPTIONS = listOf(
            "Favorites",
            "Barbell",
            "Dumbbells",
            "Bodyweight",
            "Machine",
            "Medicine Ball",
            "Kettlebells",
            "Stretches",
            "Cables",
            "Band",
            "Plate",
            "TRX",
            "Yoga",
            "Bosu Ball",
            "Vitruvian",
            "Cardio",
            "Smith Machine",
            "Recovery"
        )
        val TYPE_OPTIONS = listOf("Compound", "Isolation")
        val DIFFICULTY_OPTIONS = listOf("Beginner", "Intermediate", "Advanced")
    }
}

/**
 * UI state for Exercise List screen.
 */
data class ExerciseListUiState(
    val isLoading: Boolean = true,
    val selectedCategory: String? = null,
    val selectedType: String? = null,
    val selectedDifficulty: String? = null,
    val showFavoritesOnly: Boolean = false,
    val searchQuery: String = "",
    val showSearch: Boolean = false
)

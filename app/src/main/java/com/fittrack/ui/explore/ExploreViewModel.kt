package com.fittrack.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Explore screen.
 */
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseEntity>>(emptyList())
    val exercises: StateFlow<List<ExerciseEntity>> = _exercises.asStateFlow()

    private val _equipmentFilter = MutableStateFlow<String?>(null)
    private val _muscleFilter = MutableStateFlow<Int?>(null)

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            combine(
                exerciseRepository.getAllExercises(),
                _searchQuery,
                _equipmentFilter,
                _muscleFilter
            ) { exercises, query, equipment, muscle ->
                exercises.filter { exercise ->
                    val matchesQuery = query.isEmpty() || 
                        exercise.name.contains(query, ignoreCase = true)
                    val matchesEquipment = equipment == null || 
                        exercise.equipment == equipment
                    val matchesMuscle = muscle == null || 
                        exercise.muscleId == muscle
                    matchesQuery && matchesEquipment && matchesMuscle
                }
            }.collectLatest { filtered ->
                _exercises.value = filtered
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setEquipmentFilter(equipment: String?) {
        _equipmentFilter.value = equipment
    }

    fun setMuscleFilter(muscleId: Int?) {
        _muscleFilter.value = muscleId
    }

    fun toggleFavorite(exerciseId: Int) {
        viewModelScope.launch {
            // TODO: Implement favorite toggle
        }
    }
}

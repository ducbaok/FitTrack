package com.fittrack.ui.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.data.local.entity.RoutineDayEntity
import com.fittrack.data.local.entity.WorkoutTemplateEntity
import com.fittrack.data.repository.RoutineRepository
import com.fittrack.data.repository.WorkoutTemplateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents a day in the routine builder.
 */
data class RoutineDayDisplay(
    val dayOfWeek: Int,
    val dayName: String,
    val selectedTemplate: WorkoutTemplateEntity? = null
)

/**
 * ViewModel for Build Routine screen.
 */
@HiltViewModel
class BuildRoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val templateRepository: WorkoutTemplateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BuildRoutineState())
    val state: StateFlow<BuildRoutineState> = _state.asStateFlow()

    private val _templates = MutableStateFlow<List<WorkoutTemplateEntity>>(emptyList())
    val templates: StateFlow<List<WorkoutTemplateEntity>> = _templates.asStateFlow()

    init {
        loadTemplates()
        initializeDays()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            templateRepository.getAllTemplates().collect { templates ->
                _templates.value = templates
            }
        }
    }

    private fun initializeDays() {
        val days = listOf(
            RoutineDayDisplay(1, "Monday"),
            RoutineDayDisplay(2, "Tuesday"),
            RoutineDayDisplay(3, "Wednesday"),
            RoutineDayDisplay(4, "Thursday"),
            RoutineDayDisplay(5, "Friday"),
            RoutineDayDisplay(6, "Saturday"),
            RoutineDayDisplay(7, "Sunday")
        )
        _state.value = _state.value.copy(days = days)
    }

    fun setRoutineName(name: String) {
        _state.value = _state.value.copy(routineName = name)
    }

    fun setTemplateForDay(dayOfWeek: Int, template: WorkoutTemplateEntity?) {
        val updatedDays = _state.value.days.map { day ->
            if (day.dayOfWeek == dayOfWeek) {
                day.copy(selectedTemplate = template)
            } else {
                day
            }
        }
        _state.value = _state.value.copy(days = updatedDays)
    }

    fun clearDayTemplate(dayOfWeek: Int) {
        setTemplateForDay(dayOfWeek, null)
    }

    fun saveRoutine() {
        val name = _state.value.routineName.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            
            val days = _state.value.days
                .filter { it.selectedTemplate != null }
                .map { day ->
                    RoutineDayEntity(
                        routineId = 0, // Will be set by DAO
                        dayOfWeek = day.dayOfWeek,
                        templateId = day.selectedTemplate?.templateId
                    )
                }
            
            routineRepository.createRoutine(name, days)
            
            _state.value = _state.value.copy(
                isSaving = false,
                isSaved = true
            )
        }
    }
}

/**
 * UI state for Build Routine screen.
 */
data class BuildRoutineState(
    val routineName: String = "",
    val days: List<RoutineDayDisplay> = emptyList(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) {
    val canSave: Boolean
        get() = routineName.isNotBlank() && days.any { it.selectedTemplate != null } && !isSaving
}

package com.fittrack.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fittrack.R
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.databinding.FragmentExerciseListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment displaying a list of exercises with filtering capabilities.
 */
@AndroidEntryPoint
class ExerciseListFragment : Fragment() {

    private var _binding: FragmentExerciseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExerciseListViewModel by viewModels()
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
        setupSearch()
        setupFilterButtons()
        observeState()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Set muscle name in title (passed from Home screen)
        val muscleId = arguments?.getInt("muscleId")
        val muscleName = getMuscleNameById(muscleId)
        binding.tvMuscleTitle.text = muscleName ?: "Exercises"

        // Search button toggle
        binding.btnSearch.setOnClickListener {
            viewModel.toggleSearch()
        }
    }

    private fun getMuscleNameById(muscleId: Int?): String? {
        return when (muscleId) {
            1 -> "Chest"
            2 -> "Back"
            3 -> "Shoulders"
            4 -> "Biceps"
            5 -> "Triceps"
            6 -> "Abs"
            7 -> "Quads"
            8 -> "Hamstrings"
            9 -> "Glutes"
            10 -> "Calves"
            11 -> "Forearms"
            else -> null
        }
    }

    private fun setupRecyclerView() {
        adapter = ExerciseAdapter(
            onExerciseClick = { exercise ->
                navigateToExerciseDetail(exercise)
            },
            onFavoriteClick = { exercise ->
                viewModel.toggleFavorite(exercise.exerciseId)
            }
        )
        binding.rvExercises.adapter = adapter
    }

    private fun setupSearch() {
        // Text changed listener for search
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }

        // Handle keyboard search action
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Hide keyboard on search
                binding.etSearch.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun setupFilterButtons() {
        // Category filter (Equipment types + Favorites)
        binding.btnCategoryFilter.setOnClickListener {
            showFilterDialog(
                title = "Category",
                options = ExerciseListViewModel.CATEGORY_OPTIONS,
                selectedOption = if (viewModel.uiState.value.showFavoritesOnly) "Favorites" 
                                else viewModel.uiState.value.selectedCategory
            ) { selected ->
                viewModel.setCategoryFilter(selected)
                updateFilterButtonText(binding.btnCategoryFilter, "Category", selected)
            }
        }
        
        // Type filter (Compound/Isolation)
        binding.btnTypeFilter.setOnClickListener {
            showFilterDialog(
                title = "Type",
                options = ExerciseListViewModel.TYPE_OPTIONS,
                selectedOption = viewModel.uiState.value.selectedType
            ) { selected ->
                viewModel.setTypeFilter(selected)
                updateFilterButtonText(binding.btnTypeFilter, "Type", selected)
            }
        }
        
        // Difficulty filter
        binding.btnDifficultyFilter.setOnClickListener {
            showFilterDialog(
                title = "Difficulty",
                options = ExerciseListViewModel.DIFFICULTY_OPTIONS,
                selectedOption = viewModel.uiState.value.selectedDifficulty
            ) { selected ->
                viewModel.setDifficultyFilter(selected)
                updateFilterButtonText(binding.btnDifficultyFilter, "Difficulty", selected)
            }
        }
    }

    private fun showFilterDialog(
        title: String,
        options: List<String>,
        selectedOption: String?,
        onSelected: (String?) -> Unit
    ) {
        FilterBottomSheetFragment.newInstance(
            title = title,
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = onSelected
        ).show(childFragmentManager, "filter_$title")
    }

    private fun updateFilterButtonText(
        button: com.google.android.material.button.MaterialButton,
        defaultText: String,
        value: String?
    ) {
        button.text = value ?: defaultText
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collectLatest { state ->
                        binding.progressIndicator.isVisible = state.isLoading
                        binding.searchContainer.isVisible = state.showSearch
                        
                        // Update filter button texts
                        updateFilterButtonText(
                            binding.btnCategoryFilter, 
                            "Category",
                            if (state.showFavoritesOnly) "Favorites" else state.selectedCategory
                        )
                        updateFilterButtonText(binding.btnTypeFilter, "Type", state.selectedType)
                        updateFilterButtonText(binding.btnDifficultyFilter, "Difficulty", state.selectedDifficulty)
                    }
                }

                launch {
                    viewModel.exercises.collectLatest { exercises ->
                        updateExerciseList(exercises)
                    }
                }
            }
        }
    }

    private fun updateExerciseList(exercises: List<ExerciseEntity>) {
        adapter.submitList(exercises)
        
        // Show/hide empty state
        val isEmpty = exercises.isEmpty() && !viewModel.uiState.value.isLoading
        binding.emptyState.isVisible = isEmpty
        binding.rvExercises.isVisible = !isEmpty
    }

    private fun navigateToExerciseDetail(exercise: ExerciseEntity) {
        val bundle = Bundle().apply {
            putInt("exerciseId", exercise.exerciseId)
        }
        findNavController().navigate(
            R.id.action_exerciseList_to_exerciseDetail,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

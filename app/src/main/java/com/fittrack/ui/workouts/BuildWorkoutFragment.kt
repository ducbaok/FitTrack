package com.fittrack.ui.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fittrack.databinding.FragmentBuildWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Build Workout screen for creating custom workout templates.
 */
@AndroidEntryPoint
class BuildWorkoutFragment : Fragment() {

    private var _binding: FragmentBuildWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuildWorkoutViewModel by viewModels()
    
    private lateinit var searchAdapter: ExerciseSearchAdapter
    private lateinit var exerciseAdapter: WorkoutExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuildWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupInputListeners()
        setupClickListeners()
        observeState()
    }

    private fun setupRecyclerViews() {
        // Search results adapter
        searchAdapter = ExerciseSearchAdapter { exercise ->
            viewModel.addExercise(exercise)
        }
        binding.recyclerSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
        
        // Selected exercises adapter
        exerciseAdapter = WorkoutExerciseAdapter(
            onRemove = { exerciseId -> viewModel.removeExercise(exerciseId) },
            onSetsChange = { exerciseId, sets -> viewModel.updateExerciseSets(exerciseId, sets) },
            onRepsChange = { exerciseId, reps -> viewModel.updateExerciseReps(exerciseId, reps) },
            onWeightChange = { exerciseId, weight -> viewModel.updateExerciseWeight(exerciseId, weight) }
        )
        binding.recyclerSelectedExercises.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exerciseAdapter
        }
    }

    private fun setupInputListeners() {
        binding.etWorkoutName.doAfterTextChanged { text ->
            viewModel.setWorkoutName(text?.toString() ?: "")
        }
        
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.searchExercises(text?.toString() ?: "")
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnSaveWorkout.setOnClickListener {
            viewModel.saveWorkout()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe main state
                launch {
                    viewModel.state.collectLatest { state ->
                        // Update selected exercises list
                        exerciseAdapter.submitList(state.selectedExercises)
                        
                        // Show/hide sections based on state
                        binding.tvAddedExercisesLabel.isVisible = state.selectedExercises.isNotEmpty()
                        binding.emptyState.isVisible = state.selectedExercises.isEmpty()
                        
                        // Update save button
                        binding.btnSaveWorkout.isEnabled = state.canSave
                        
                        // Navigate back if saved
                        if (state.isSaved) {
                            findNavController().navigateUp()
                        }
                        
                        // Clear search when exercise added
                        if (state.searchQuery.isEmpty() && binding.etSearch.text?.isNotEmpty() == true) {
                            binding.etSearch.text?.clear()
                        }
                    }
                }
                
                // Observe search results
                launch {
                    viewModel.searchResults.collectLatest { results ->
                        searchAdapter.submitList(results)
                        binding.recyclerSearchResults.isVisible = results.isNotEmpty()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

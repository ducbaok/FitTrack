package com.fittrack.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fittrack.R
import com.fittrack.databinding.FragmentExploreBinding
import com.fittrack.ui.exercise.ExerciseAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Explore screen for searching and filtering all exercises.
 */
@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExploreViewModel by viewModels()
    private lateinit var exerciseAdapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchAndFilters()
        observeState()
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter(
            onExerciseClick = { exercise ->
                val bundle = Bundle().apply {
                    putInt("exerciseId", exercise.exerciseId)
                }
                findNavController().navigate(R.id.action_explore_to_exerciseDetail, bundle)
            }
        )

        binding.rvExercises.apply {
            adapter = exerciseAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchAndFilters() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.onSearchQueryChanged(text?.toString() ?: "")
        }

        binding.btnEquipmentFilter.setOnClickListener {
            // TODO: Show equipment filter popup
        }

        binding.btnMuscleFilter.setOnClickListener {
            // TODO: Show muscle filter popup
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exercises.collectLatest { exercises ->
                    exerciseAdapter.submitList(exercises)
                    binding.emptyState.visibility = if (exercises.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

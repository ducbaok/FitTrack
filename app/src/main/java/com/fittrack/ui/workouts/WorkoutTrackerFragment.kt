package com.fittrack.ui.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fittrack.databinding.FragmentWorkoutTrackerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutTrackerFragment : Fragment() {

    private var _binding: FragmentWorkoutTrackerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutTrackerViewModel by viewModels()
    private lateinit var adapter: WorkoutTrackerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = WorkoutTrackerAdapter(
            onAddSet = { exerciseId -> viewModel.addSet(exerciseId) },
            onSetUpdate = { exId, setId, set -> viewModel.updateSet(exId, setId, set) }
        )
        
        binding.recyclerExercises.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WorkoutTrackerFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnSave.setOnClickListener {
            // TODO: Implement save logic
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exercises.collectLatest { exercises ->
                    adapter.submitList(exercises)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

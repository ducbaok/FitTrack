package com.fittrack.ui.workouts

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
import com.fittrack.databinding.FragmentBuildRoutineBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Build Routine screen for creating weekly workout schedules.
 */
@AndroidEntryPoint
class BuildRoutineFragment : Fragment() {

    private var _binding: FragmentBuildRoutineBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BuildRoutineViewModel by viewModels()
    
    private lateinit var daysAdapter: RoutineDayAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuildRoutineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupInputListeners()
        setupClickListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        daysAdapter = RoutineDayAdapter(
            onTemplateSelected = { dayOfWeek, template ->
                viewModel.setTemplateForDay(dayOfWeek, template)
            },
            onClear = { dayOfWeek ->
                viewModel.clearDayTemplate(dayOfWeek)
            }
        )
        
        binding.recyclerDays.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = daysAdapter
        }
    }

    private fun setupInputListeners() {
        binding.etRoutineName.doAfterTextChanged { text ->
            viewModel.setRoutineName(text?.toString() ?: "")
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.btnSaveRoutine.setOnClickListener {
            viewModel.saveRoutine()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe view state
                launch {
                    viewModel.state.collectLatest { state ->
                        daysAdapter.submitList(state.days)
                        
                        binding.btnSaveRoutine.isEnabled = state.canSave
                        
                        if (state.isSaved) {
                            findNavController().navigateUp()
                        }
                    }
                }
                
                // Observe available templates
                launch {
                    viewModel.templates.collectLatest { templates ->
                        daysAdapter.updateTemplates(templates)
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

package com.fittrack.ui.history

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fittrack.R
import com.fittrack.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * History screen showing list of past workouts grouped by date.
 */
@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        setupFilterToggle()
        observeState()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { workout ->
            // Navigate to exercise detail when workout item is clicked
            val bundle = Bundle().apply {
                putInt("exerciseId", workout.exerciseId)
            }
            findNavController().navigate(R.id.action_history_to_exerciseDetail, bundle)
        }
        
        binding.recyclerHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCalendar.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Set to start of selected day
                val startCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                // Set to end of selected day
                val endCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 23, 59, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                
                viewModel.setFilterType(FilterType.DAY)
                viewModel.setDateRange(startCal.timeInMillis, endCal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupFilterToggle() {
        binding.btnByDay.setOnClickListener {
            viewModel.setFilterType(FilterType.DAY)
            viewModel.refresh()
            updateFilterButtonStyles(FilterType.DAY)
        }

        binding.btnDateRange.setOnClickListener {
            viewModel.setFilterType(FilterType.RANGE)
            showDatePicker()
            updateFilterButtonStyles(FilterType.RANGE)
        }
    }

    private fun updateFilterButtonStyles(selected: FilterType) {
        when (selected) {
            FilterType.DAY -> {
                binding.btnByDay.setBackgroundResource(R.drawable.bg_toggle_active)
                binding.btnDateRange.setBackgroundResource(R.drawable.bg_chip)
            }
            FilterType.RANGE -> {
                binding.btnByDay.setBackgroundResource(R.drawable.bg_chip)
                binding.btnDateRange.setBackgroundResource(R.drawable.bg_toggle_active)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.historyState.collectLatest { state ->
                    // Convert grouped data to flat list for adapter
                    val listItems = state.historyGroups.toListItems()
                    historyAdapter.submitList(listItems)
                    
                    // Show empty state if no history
                    // binding.emptyState.isVisible = listItems.isEmpty() && !state.isLoading
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

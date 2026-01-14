package com.fittrack.ui.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fittrack.R
import com.fittrack.databinding.FragmentTrackWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Track Workout screen showing workout statistics with period selector.
 */
@AndroidEntryPoint
class TrackWorkoutFragment : Fragment() {

    private var _binding: FragmentTrackWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrackWorkoutViewModel by viewModels()
    
    private lateinit var periodButtons: List<TextView>
    private lateinit var barViews: List<View>
    private lateinit var dayLabels: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        observeState()
    }

    private fun setupViews() {
        periodButtons = listOf(
            binding.btnDaily,
            binding.btnWeekly,
            binding.btnMonthly,
            binding.btnYearly
        )
        
        // Get bar views and labels from included layouts
        val chartContainer = binding.chartContainer
        barViews = mutableListOf()
        dayLabels = mutableListOf()
        
        val dayLabelTexts = listOf("M", "T", "W", "T", "F", "S", "S")
        
        for (i in 0 until chartContainer.childCount) {
            val barLayout = chartContainer.getChildAt(i) as? LinearLayout
            barLayout?.let {
                val barView = it.findViewById<View>(R.id.barView)
                val tvLabel = it.findViewById<TextView>(R.id.tvDayLabel)
                
                if (barView != null) (barViews as MutableList).add(barView)
                if (tvLabel != null) {
                    tvLabel.text = dayLabelTexts.getOrElse(i) { "" }
                    (dayLabels as MutableList).add(tvLabel)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCalendar.setOnClickListener {
            // TODO: Show date range picker
        }

        // Period selector
        binding.btnDaily.setOnClickListener {
            viewModel.setPeriod(StatsPeriod.DAILY)
            updatePeriodButtonStyles(0)
        }
        binding.btnWeekly.setOnClickListener {
            viewModel.setPeriod(StatsPeriod.WEEKLY)
            updatePeriodButtonStyles(1)
        }
        binding.btnMonthly.setOnClickListener {
            viewModel.setPeriod(StatsPeriod.MONTHLY)
            updatePeriodButtonStyles(2)
        }
        binding.btnYearly.setOnClickListener {
            viewModel.setPeriod(StatsPeriod.YEARLY)
            updatePeriodButtonStyles(3)
        }
    }

    private fun updatePeriodButtonStyles(selectedIndex: Int) {
        periodButtons.forEachIndexed { index, button ->
            if (index == selectedIndex) {
                button.setBackgroundResource(R.drawable.bg_toggle_active)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorOnSurface))
            } else {
                button.background = null
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorMutedForeground))
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statsState.collectLatest { state ->
                    // Update stats
                    binding.tvWorkoutCount.text = state.workoutCount.toString()
                    binding.tvDuration.text = state.durationText
                    binding.tvVolume.text = state.volumeText
                    binding.tvCalories.text = state.caloriesText
                    
                    // Update chart bars
                    updateChart(state.weeklyBarData)
                }
            }
        }
    }

    private fun updateChart(data: List<Int>) {
        val maxValue = data.maxOrNull()?.takeIf { it > 0 } ?: 1
        val maxBarHeight = 120 // dp
        val density = resources.displayMetrics.density
        
        data.forEachIndexed { index, value ->
            if (index < barViews.size) {
                val barHeight = ((value.toFloat() / maxValue) * maxBarHeight * density).toInt()
                    .coerceAtLeast((8 * density).toInt()) // Minimum height
                
                val params = barViews[index].layoutParams
                params.height = barHeight
                barViews[index].layoutParams = params
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

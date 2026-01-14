package com.fittrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fittrack.R
import com.fittrack.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * Home screen fragment displaying the interactive anatomy view.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToggleButtons()
        setupAnatomyView()
        setupClickListeners()
        observeState()
    }

    private fun setupToggleButtons() {
        // Set initial selection
        binding.genderToggle.check(R.id.btnMale)
        binding.viewToggle.check(R.id.btnFront)

        // Gender toggle listener
        binding.genderToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnMale -> viewModel.setGender(Gender.MALE)
                    R.id.btnFemale -> viewModel.setGender(Gender.FEMALE)
                }
            }
        }

        // View toggle listener
        binding.viewToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnFront -> viewModel.setBodyView(BodyView.FRONT)
                    R.id.btnBack -> viewModel.setBodyView(BodyView.BACK)
                }
            }
        }

        // Detail level toggle listener
        binding.detailToggle.check(R.id.btnBasic)
        binding.detailToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnBasic -> viewModel.setDetailLevel(DetailLevel.BASIC)
                    R.id.btnAdvanced -> viewModel.setDetailLevel(DetailLevel.ADVANCED)
                }
            }
        }

        // Fatigue toggle listener
        binding.fatigueToggle.check(R.id.btnFatigueOn)
        binding.fatigueToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnFatigueOn -> viewModel.setShowFatigue(true)
                    R.id.btnFatigueOff -> viewModel.setShowFatigue(false)
                }
            }
        }
    }

    private fun setupAnatomyView() {
        binding.anatomyView.onMuscleClickListener = { muscleId ->
            viewModel.onMuscleSelected(muscleId)
        }
    }

    private fun setupClickListeners() {
        binding.btnViewExercises.setOnClickListener {
            viewModel.selectedMuscle.value?.let { muscleId ->
                navigateToExerciseList(muscleId)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe UI state
                launch {
                    viewModel.uiState.collectLatest { state ->
                        updateUi(state)
                    }
                }

                // Observe selected muscle
                launch {
                    viewModel.selectedMuscle.collectLatest { muscleId ->
                        updateMuscleSelection(muscleId)
                    }
                }
                
                // Observe Supabase test result
                launch {
                    viewModel.supabaseTestResult.filterNotNull().collectLatest { result ->
                        showSupabaseTestResult(result)
                    }
                }
            }
        }
    }
    
    private fun showSupabaseTestResult(result: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Supabase Connection Test")
            .setMessage(result)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                viewModel.clearSupabaseTestResult()
            }
            .show()
    }

    private fun updateUi(state: HomeUiState) {
        // Update loading state
        binding.progressIndicator.isVisible = state.isLoading
        binding.anatomyView.isVisible = !state.isLoading

        // Update anatomy view
        binding.anatomyView.setGender(state.gender)
        binding.anatomyView.setBodyView(state.bodyView)
        
        // Pass fatigue colors only if showFatigue is enabled
        if (state.showFatigue) {
            binding.anatomyView.setMuscleFatigueColors(state.fatigueColors)
        } else {
            binding.anatomyView.setMuscleFatigueColors(emptyMap())
        }

        // Update toggle selections
        when (state.gender) {
            Gender.MALE -> binding.genderToggle.check(R.id.btnMale)
            Gender.FEMALE -> binding.genderToggle.check(R.id.btnFemale)
        }
        when (state.bodyView) {
            BodyView.FRONT -> binding.viewToggle.check(R.id.btnFront)
            BodyView.BACK -> binding.viewToggle.check(R.id.btnBack)
        }
        
        // Update fatigue toggle
        if (state.showFatigue) {
            binding.fatigueToggle.check(R.id.btnFatigueOn)
        } else {
            binding.fatigueToggle.check(R.id.btnFatigueOff)
        }
    }

    private fun updateMuscleSelection(muscleId: String?) {
        if (muscleId != null) {
            binding.btnViewExercises.isEnabled = true
            binding.tvHint.text = getString(R.string.muscle_selected, formatMuscleId(muscleId))
        } else {
            binding.btnViewExercises.isEnabled = false
            binding.tvHint.text = getString(R.string.tap_muscle_hint)
        }
    }

    private fun formatMuscleId(muscleId: String): String {
        // Convert "muscle_chest_left" to "Chest Left"
        return muscleId
            .removePrefix("muscle_")
            .replace("_", " ")
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
    }

    private fun navigateToExerciseList(muscleId: String) {
        // Map SVG muscle ID to database muscle ID
        val dbMuscleId = mapMuscleIdToDbId(muscleId)
        
        if (dbMuscleId != null) {
            val bundle = Bundle().apply {
                putInt("muscleId", dbMuscleId)
            }
            findNavController().navigate(R.id.action_home_to_exerciseList, bundle)
        } else {
            Snackbar.make(
                binding.root,
                "No exercises found for: ${formatMuscleId(muscleId)}",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * Maps SVG path muscle IDs to database muscle IDs.
     * Returns null if no mapping exists.
     */
    private fun mapMuscleIdToDbId(muscleId: String): Int? {
        return when {
            muscleId.contains("chest") -> 1      // Chest
            muscleId.contains("lat") || muscleId.contains("trap") || muscleId.contains("lower_back") -> 2  // Back
            muscleId.contains("deltoid") -> 3    // Shoulders
            muscleId.contains("bicep") -> 4      // Biceps
            muscleId.contains("tricep") -> 5     // Triceps
            muscleId.contains("abs") -> 6        // Abs
            muscleId.contains("quad") -> 7       // Quads
            muscleId.contains("hamstring") -> 8  // Hamstrings
            muscleId.contains("glute") -> 9      // Glutes
            muscleId.contains("calf") -> 10      // Calves
            muscleId.contains("forearm") -> 11   // Forearms
            muscleId.contains("hand") -> null    // Hand - no exercises typically
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

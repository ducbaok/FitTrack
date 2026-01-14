package com.fittrack.ui.exercise

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.fittrack.R
import com.fittrack.data.local.entity.ExerciseEntity
import com.fittrack.databinding.FragmentExerciseDetailBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment displaying exercise details with Mark as Done functionality.
 */
@AndroidEntryPoint
class ExerciseDetailFragment : Fragment() {

    private var _binding: FragmentExerciseDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExerciseDetailViewModel by viewModels()

    private var currentMuscleId: Int = 0

    // Auto-hide button handler
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideAddButton() }
    private val hideDelayMs = 3000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupClickListeners()
        setupScrollBehavior()
        observeState()
        
        // Initially hide the button
        binding.btnAddToWorkout.apply {
            alpha = 0f
            isVisible = false
            isClickable = false
        }
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun setupClickListeners() {
        binding.btnAddToWorkout.setOnClickListener {
            showLogWorkoutDialog()
        }
    }

    private fun setupScrollBehavior() {
        // Show button when user scrolls, hide after delay
        binding.scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            showAddButton()
            resetHideTimer()
        }
    }

    private fun showAddButton() {
        if (binding.btnAddToWorkout.alpha == 1f && binding.btnAddToWorkout.isVisible) return
        
        binding.btnAddToWorkout.apply {
            isVisible = true
            animate()
                .alpha(1f)
                .setDuration(200)
                .withStartAction { isClickable = true }
                .start()
        }
    }

    private fun hideAddButton() {
        if (binding.btnAddToWorkout.alpha == 0f) return

        binding.btnAddToWorkout.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction { 
                binding.btnAddToWorkout.isVisible = false 
                binding.btnAddToWorkout.isClickable = false
            }
            .start()
    }

    private fun resetHideTimer() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, hideDelayMs)
    }

    private fun showLogWorkoutDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_log_workout, null)
        
        val etSets = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSets)
        val etReps = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etReps)
        val etWeight = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etWeight)
        
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Log Workout") { _, _ ->
                val sets = etSets.text?.toString()?.toIntOrNull() ?: 1
                val reps = etReps.text?.toString()?.toIntOrNull()
                val weight = etWeight.text?.toString()?.toFloatOrNull()
                
                viewModel.markAsDone(sets, reps, weight)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.exercise.collectLatest { exercise ->
                        exercise?.let { bindExercise(it) }
                    }
                }

                launch {
                    viewModel.uiState.collectLatest { state ->
                        handleUiState(state)
                    }
                }
            }
        }
    }

    private fun bindExercise(exercise: ExerciseEntity) {
        binding.tvExerciseName.text = exercise.name
        binding.tvEquipment.text = exercise.equipment
        binding.tvDifficulty.text = exercise.difficulty
        binding.tvType.text = exercise.type
        
        // Update difficulty badge color based on level
        updateDifficultyBadge(exercise.difficulty)
        
        // Display instructions
        displayInstructions(exercise.instructions)
        
        // Display pro tips
        displayProTips(exercise.proTips)
        
        // Store muscle ID for anatomy view setup
        currentMuscleId = exercise.muscleId
    }

    private fun handleUiState(state: ExerciseDetailUiState) {
        binding.btnAddToWorkout.isEnabled = !state.isLoading
        
        // Update muscle name
        if (state.muscleName.isNotEmpty()) {
            binding.tvMuscle.text = state.muscleName
            binding.tvMuscleTag.text = state.muscleName
        }
        
        // Update fatigue bar
        updateFatigueBar(state.fatiguePercent)
        
        // Update favorite icon
        updateFavoriteIcon(state.isFavorite)
        
        // Update anatomy colors based on fatigue
        if (currentMuscleId != 0 && state.fatigueColorHex.isNotEmpty()) {
            setupMiniAnatomyViews(currentMuscleId, state.fatigueColorHex)
        }
        
        // Update anatomy gender
        binding.miniAnatomyFront.setGender(state.gender)
        binding.miniAnatomyBack.setGender(state.gender)

        if (state.workoutLogged) {
            showWorkoutLoggedSnackbar()
            viewModel.consumeEvents()
        }

        if (state.undoComplete) {
            Snackbar.make(binding.root, "Workout undone", Snackbar.LENGTH_SHORT).show()
            viewModel.consumeEvents()
        }

        state.error?.let { error ->
            Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
            viewModel.consumeEvents()
        }
    }

    private fun setupMiniAnatomyViews(muscleId: Int, colorHex: String) {
        if (muscleId == 0) return
        
        val color = android.graphics.Color.parseColor(colorHex)
        
        // Configure front body view
        binding.miniAnatomyFront.setBodySide(MiniAnatomyView.BodySide.FRONT)
        
        // Configure back body view
        binding.miniAnatomyBack.setBodySide(MiniAnatomyView.BodySide.BACK)
        
        // Get muscle IDs to highlight for this exercise
        val (frontMuscles, backMuscles) = MiniAnatomyView.getMuscleIdsForMuscle(muscleId)
        
        // Highlight primary muscle on front view with dynamic color
        val frontHighlights = frontMuscles.associateWith { color }
        binding.miniAnatomyFront.setHighlightedMuscles(frontHighlights)
        
        // Highlight primary muscle on back view with dynamic color
        val backHighlights = backMuscles.associateWith { color }
        binding.miniAnatomyBack.setHighlightedMuscles(backHighlights)
    }

    private fun updateDifficultyBadge(difficulty: String) {
        val (bgRes, textColorRes) = when (difficulty.lowercase()) {
            "beginner" -> Pair(R.drawable.bg_badge_beginner, R.color.colorBeginner)
            "advanced" -> Pair(R.drawable.bg_badge_advanced, R.color.colorAdvanced)
            else -> Pair(R.drawable.bg_badge_intermediate, R.color.colorIntermediate)
        }
        binding.tvDifficulty.setBackgroundResource(bgRes)
        binding.tvDifficulty.setTextColor(ContextCompat.getColor(requireContext(), textColorRes))
    }

    private fun updateFatigueBar(percent: Int) {
        binding.tvFatiguePercent.text = "$percent%"
        
        // Move marker to correct position
        binding.fatigueMarker.post {
            val parentWidth = (binding.fatigueMarker.parent as View).width
            val markerWidth = binding.fatigueMarker.width
            val availableWidth = parentWidth - markerWidth
            
            val translationX = (availableWidth * percent / 100f)
            binding.fatigueMarker.translationX = translationX
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star
        val tintColor = if (isFavorite) R.color.colorFavorite else R.color.colorMutedForeground
        binding.btnFavorite.setImageResource(iconRes)
        binding.btnFavorite.imageTintList = ContextCompat.getColorStateList(requireContext(), tintColor)
    }

    private fun displayInstructions(instructions: String) {
        binding.instructionsContainer.removeAllViews()
        
        if (instructions.isBlank()) return
        
        // Split by newline and strip any existing numbering
        val steps = instructions.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { step ->
                // Remove leading numbers like "1.", "1)", "1:" etc.
                step.replace(Regex("^\\d+[.):]+\\s*"), "")
            }
        
        steps.forEachIndexed { index, step ->
            val stepView = TextView(requireContext()).apply {
                text = "${index + 1}. $step"
                setTextColor(ContextCompat.getColor(context, R.color.colorOnSurface))
                textSize = 14f
                setPadding(0, 8, 0, 8)
            }
            binding.instructionsContainer.addView(stepView)
        }
    }

    private fun displayProTips(proTips: String) {
        binding.tipsContainer.removeAllViews()
        
        if (proTips.isBlank()) {
            binding.tipsCard.isVisible = false
            return
        }
        
        binding.tipsCard.isVisible = true
        
        // Split by newline
        val tips = proTips.split("\n")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        
        tips.forEach { tip ->
            val tipView = TextView(requireContext()).apply {
                text = "• $tip"
                setTextColor(ContextCompat.getColor(context, R.color.colorOnSurface))
                textSize = 14f
                setPadding(0, 4, 0, 4)
            }
            binding.tipsContainer.addView(tipView)
        }
    }

    private fun showWorkoutLoggedSnackbar() {
        Snackbar.make(binding.root, R.string.workout_logged, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                viewModel.undoLastWorkout()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideHandler.removeCallbacks(hideRunnable)
        _binding = null
    }
}

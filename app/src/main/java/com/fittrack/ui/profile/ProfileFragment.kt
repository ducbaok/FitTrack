package com.fittrack.ui.profile

import android.content.Intent
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
import com.fittrack.AuthActivity
import com.fittrack.R
import com.fittrack.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Profile screen showing user info, stats, and sign out option.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeState()
    }

    private fun setupClickListeners() {
        binding.btnEditInfo.setOnClickListener {
            // TODO: Navigate to edit personal info
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settings)
        }

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out") { _, _ ->
                    viewModel.signOut()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profileState.collectLatest { state ->
                        bindProfile(state)
                    }
                }

                launch {
                    viewModel.signedOut.collectLatest { signedOut ->
                        if (signedOut) {
                            navigateToAuth()
                        }
                    }
                }
            }
        }
    }

    private fun bindProfile(state: ProfileState) {
        binding.tvUserName.text = state.userName ?: "User"
        binding.tvLevel.text = "Level ${state.level} Athlete"
        binding.tvWorkoutsCount.text = state.workoutCount.toString()
        binding.tvHoursCount.text = state.hoursCount.toString()
        binding.tvStreakCount.text = state.currentStreak.toString()
        binding.tvLevelCount.text = state.level.toString()
        binding.tvXpProgress.text = "${state.currentXp} / ${state.nextLevelXp} XP"
        
        // Update XP progress bar width
        val progressPercent = (state.currentXp.toFloat() / state.nextLevelXp * 100).toInt()
        val layoutParams = binding.xpProgressBar.layoutParams
        // Progress bar width would be set programmatically based on percentage
        
        // Personal info
        binding.tvName.text = state.userName ?: "Not set"
        binding.tvGoals.text = state.fitnessGoals ?: "Not set"
        binding.tvHeight.text = state.height ?: "Not set"
        binding.tvWeight.text = state.weight ?: "Not set"
    }

    private fun navigateToAuth() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.fittrack.ui.auth

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
import com.fittrack.R
import com.fittrack.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Login/Register screen fragment.
 * Handles user authentication via email/password and Google Sign-In.
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTextWatchers()
        setupClickListeners()
        observeState()
        
        // Check if user is already logged in
        viewModel.checkLoginStatus()
    }

    private fun setupTextWatchers() {
        binding.etEmail.doAfterTextChanged { text ->
            viewModel.updateEmail(text?.toString() ?: "")
        }

        binding.etPassword.doAfterTextChanged { text ->
            viewModel.updatePassword(text?.toString() ?: "")
        }

        binding.cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateRememberMe(isChecked)
        }
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            viewModel.signIn()
        }

        binding.btnSignUp.setOnClickListener {
            viewModel.signUp()
        }

        binding.btnForgotPassword.setOnClickListener {
            viewModel.resetPassword()
        }

        binding.btnGoogleSignIn.setOnClickListener {
            // TODO: Implement Google Sign-In flow
            // This requires Google Sign-In client setup
            showMessage("Google Sign-In coming soon")
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.authState.collectLatest { state ->
                        handleAuthState(state)
                    }
                }

                launch {
                    viewModel.formState.collectLatest { formState ->
                        handleFormState(formState)
                    }
                }
            }
        }
    }

    private fun handleAuthState(state: AuthState) {
        when (state) {
            is AuthState.Idle -> {
                setLoading(false)
            }
            is AuthState.Loading -> {
                setLoading(true)
            }
            is AuthState.Success -> {
                setLoading(false)
                navigateToHome()
            }
            is AuthState.Error -> {
                setLoading(false)
                showMessage(state.message)
                viewModel.clearError()
            }
        }
    }

    private fun handleFormState(formState: LoginFormState) {
        binding.tilEmail.error = formState.emailError
        binding.tilPassword.error = formState.passwordError
        binding.cbRememberMe.isChecked = formState.rememberMe
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressIndicator.isVisible = isLoading
        binding.btnSignIn.isEnabled = !isLoading
        binding.btnSignUp.isEnabled = !isLoading
        binding.btnGoogleSignIn.isEnabled = !isLoading
        binding.btnForgotPassword.isEnabled = !isLoading
        
        // Hide button text when loading
        binding.btnSignIn.text = if (isLoading) "" else getString(com.fittrack.R.string.sign_in)
    }

    private fun navigateToHome() {
        // Navigate to MainActivity through AuthActivity
        (requireActivity() as? com.fittrack.AuthActivity)?.navigateToMain()
    }

    private fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

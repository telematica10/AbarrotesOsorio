package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ajo.abarrotesOsorio.databinding.FragmentSignUpBinding
import com.ajo.abarrotesOsorio.viewmodel.SignUpViewModel
import kotlinx.coroutines.launch

/**
 * SignUpFragment: Handles the user interface for new user registration.
 * It uses a ViewModel to manage the state and interact with the AuthRepository.
 */
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    // Use viewModels() delegate to get a ViewModel instance.
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding.
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the click listener for the sign-up button.
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Basic input validation.
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Por favor, completa ambos campos.")
                return@setOnClickListener
            }

            // Call the ViewModel's signUp function.
            viewModel.signUp(email, password)
        }

        // Configura el clic del texto "Crear una cuenta" para navegar a la pantalla de registro
        binding.tvLogin.setOnClickListener {
            // Usa la acción definida en nav_graph para navegar a SignUpFragment
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }

        // Use a coroutine to observe the state from the ViewModel.
        lifecycleScope.launch {
            viewModel.signUpState.collect { state ->
                // Handle different states of the registration process.
                when (state) {
                    is SignUpViewModel.SignUpState.Loading -> {
                        // Show the progress bar when loading.
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is SignUpViewModel.SignUpState.Success -> {
                        // Hide the progress bar and show a success message.
                        binding.progressBar.visibility = View.GONE
                        showToast("¡Registro exitoso! Ahora puedes iniciar sesión.")
                        // Usa la acción definida en nav_graph para navegar a LoginFragment
                        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                    }
                    is SignUpViewModel.SignUpState.Error -> {
                        // Hide the progress bar and show an error message.
                        binding.progressBar.visibility = View.GONE
                        showToast("Error al registrar: ${state.message}")
                    }
                    is SignUpViewModel.SignUpState.Idle -> {
                        // Hide the progress bar when in the idle state.
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * Helper function to display a short message to the user.
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

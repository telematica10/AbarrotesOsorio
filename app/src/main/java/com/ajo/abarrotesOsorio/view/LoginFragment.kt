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
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.databinding.FragmentLoginBinding
import com.ajo.abarrotesOsorio.view.ui.LoginState
import com.ajo.abarrotesOsorio.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    // ViewBinding for accessing views
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // ViewModel instance for handling login logic
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the click listener for the login button
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            loginViewModel.signIn(email, password)
        }
        // Configura el clic del texto "Crear una cuenta" para navegar a la pantalla de registro
        binding.tvSignUp.setOnClickListener {
            // Usa la acción definida en nav_graph para navegar a SignUpFragment
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        // Observe the login state from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        // Show a progress bar or disable the button
                        binding.buttonLogin.isEnabled = false
                        // You can also show a progress bar here
                    }
                    is LoginState.Success -> {
                        // Login successful, navigate to the main fragment
                        binding.buttonLogin.isEnabled = true
                        Toast.makeText(requireContext(), getString(R.string.welcome_app), Toast.LENGTH_SHORT).show()
                        val action = LoginFragmentDirections.actionLoginFragmentToVentasFragment()
                        findNavController().navigate(action)
                    }
                    is LoginState.Error -> {
                        // Login failed, show an error message
                        binding.buttonLogin.isEnabled = true
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        // Initial or idle state
                        binding.buttonLogin.isEnabled = true
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

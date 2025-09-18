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

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            loginViewModel.signIn(email, password)
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Loading -> {
                        binding.buttonLogin.isEnabled = false
                    }
                    is LoginState.Success -> {
                        Toast.makeText(requireContext(), getString(R.string.welcome_app), Toast.LENGTH_SHORT).show()
                        view.post {
                            if (isAdded) {
                                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToVentasFragment())
                            }
                        }
                    }
                    is LoginState.Error -> {
                        binding.buttonLogin.isEnabled = true
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> {
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

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

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Por favor, completa ambos campos.")
                return@setOnClickListener
            }

            viewModel.signUp(email, password)
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }

        lifecycleScope.launch {
            viewModel.signUpState.collect { state ->
                when (state) {
                    is SignUpViewModel.SignUpState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is SignUpViewModel.SignUpState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("¡Registro exitoso! Ahora puedes iniciar sesión.")
                        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
                    }
                    is SignUpViewModel.SignUpState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showToast("Error al registrar: ${state.message}")
                    }
                    is SignUpViewModel.SignUpState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

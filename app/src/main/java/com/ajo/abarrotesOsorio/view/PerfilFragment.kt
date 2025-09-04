package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.databinding.FragmentPerfilBinding
import com.ajo.abarrotesOsorio.viewmodel.PerfilViewModel
import com.ajo.abarrotesOsorio.viewmodel.PerfilViewModel.PerfilUiState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PerfilFragment : Fragment() {

    private lateinit var binding: FragmentPerfilBinding

    private val viewModel: PerfilViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_perfil, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        observeUiState()
    }

    private fun observeUiState() {
        viewModel.uiState.onEach { state ->
            when (state) {
                is PerfilUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnEditProfile.isEnabled = false // Deshabilitar botones, por ejemplo
                    binding.btnLogout.isEnabled = false
                }
                is PerfilUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnEditProfile.isEnabled = true
                    binding.btnLogout.isEnabled = true
                    val user = state.user
                    binding.tvUserName.text = user?.displayName ?: "Usuario"
                    binding.tvUserEmail.text = user?.email ?: "Sin correo electrónico"
                    binding.ivProfilePic.setImageResource(R.drawable.ic_usuario)
                }
                is PerfilUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnEditProfile.isEnabled = true
                    binding.btnLogout.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is PerfilUiState.LoggedOut -> {
                    findNavController().navigate(R.id.action_perfilFragment_to_loginFragment)
                }
                is PerfilUiState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
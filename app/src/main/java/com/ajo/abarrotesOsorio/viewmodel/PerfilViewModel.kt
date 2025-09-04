package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.repository.AuthRepository
import com.ajo.abarrotesOsorio.data.repository.AuthResultState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    sealed class PerfilUiState {
        object Idle : PerfilUiState()
        object Loading : PerfilUiState()
        data class Success(val user: FirebaseUser?) : PerfilUiState()
        data class Error(val message: String) : PerfilUiState()
        object LoggedOut : PerfilUiState()
    }

    private val _uiState = MutableStateFlow<PerfilUiState>(PerfilUiState.Idle)

    val uiState: StateFlow<PerfilUiState> = _uiState

    init {
        getCurrentUser()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _uiState.value = PerfilUiState.Loading
            val user = authRepository.getCurrentUser()
            if (user != null) {
                _uiState.value = PerfilUiState.Success(user)
            } else {
                _uiState.value = PerfilUiState.Error("No se encontró usuario autenticado.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = PerfilUiState.Loading

            authRepository.signOut().collect { result ->
                when (result) {
                    is AuthResultState.Success -> {
                        _uiState.value = PerfilUiState.LoggedOut
                    }
                    is AuthResultState.Error -> {
                        _uiState.value = PerfilUiState.Error(result.message)
                    }
                    is AuthResultState.Loading -> {
                    }
                }
            }
        }
    }
}
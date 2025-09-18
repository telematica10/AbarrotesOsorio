package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.repository.AuthRepository
import com.ajo.abarrotesOsorio.data.repository.AuthResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    sealed class SignUpState {
        object Idle : SignUpState()
        object Loading : SignUpState()
        object Success : SignUpState()
        data class Error(val message: String) : SignUpState()
    }

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)

    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signUp(email, password).collect { result ->
                when (result) {
                    is AuthResultState.Loading -> {
                        _signUpState.value = SignUpState.Loading
                    }
                    is AuthResultState.Success -> {
                        _signUpState.value = SignUpState.Success
                    }
                    is AuthResultState.Error -> {
                        _signUpState.value = SignUpState.Error(result.message)
                    }
                }
            }
        }
    }
}
package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.repository.AuthRepository
import com.ajo.abarrotesOsorio.data.repository.AuthResultState
import com.ajo.abarrotesOsorio.view.ui.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)

    val loginState: StateFlow<LoginState> = _loginState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signIn(email, password).collect { result ->
                when (result) {
                    is AuthResultState.Loading -> {
                        _loginState.value = LoginState.Loading
                    }
                    is AuthResultState.Success -> {
                        _loginState.value = LoginState.Success
                    }
                    is AuthResultState.Error -> {
                        _loginState.value = LoginState.Error(result.message)
                    }
                }
            }
        }
    }
}

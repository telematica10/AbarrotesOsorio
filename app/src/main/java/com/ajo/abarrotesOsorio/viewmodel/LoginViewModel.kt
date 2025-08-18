package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.AuthRepository
import com.ajo.abarrotesOsorio.data.AuthResultState
import com.ajo.abarrotesOsorio.view.ui.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * LoginViewModel: Handles the UI-related logic for the login screen.
 * It observes a stream of authentication results from the AuthRepository and updates the UI state accordingly.
 */
class LoginViewModel : ViewModel() {

    // Instance of AuthRepository to handle data operations.
    // In a real application, this would be injected using a framework like Hilt or Koin.
    private val authRepository = AuthRepository()

    // MutableStateFlow to hold the current state of the login UI.
    // Private to prevent direct modification from outside the ViewModel.
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)

    // Public StateFlow to expose the UI state to the Fragment.
    val loginState: StateFlow<LoginState> = _loginState

    /**
     * Initiates the sign-in process.
     * It launches a coroutine to call the signIn method of the repository.
     * @param email The user's email.
     * @param password The user's password.
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            // The ViewModel collects the results emitted by the AuthRepository's Flow
            authRepository.signIn(email, password).collect { result ->
                // A 'when' expression is used to handle each possible state from the repository.
                when (result) {
                    is AuthResultState.Loading -> {
                        // The repository is busy, so we update the UI state to Loading.
                        _loginState.value = LoginState.Loading
                    }
                    is AuthResultState.Success -> {
                        // The login was successful, so we update the UI state to Success.
                        _loginState.value = LoginState.Success
                    }
                    is AuthResultState.Error -> {
                        // The login failed, so we update the UI state to Error with the message.
                        _loginState.value = LoginState.Error(result.message)
                    }
                }
            }
        }
    }
}

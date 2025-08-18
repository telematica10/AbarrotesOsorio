package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.AuthRepository
import com.ajo.abarrotesOsorio.data.AuthResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * SignUpViewModel: Handles the UI-related logic for the user registration screen.
 * It observes a stream of authentication results from the AuthRepository and updates the UI state accordingly.
 */
class SignUpViewModel : ViewModel() {

    // Instance of AuthRepository to handle data operations.
    // This would ideally be injected using a dependency injection framework.
    private val authRepository = AuthRepository()

    /**
     * Sealed class to represent the different states of the sign-up UI.
     * This makes the state changes explicit and easy to handle in the Fragment.
     */
    sealed class SignUpState {
        object Idle : SignUpState()
        object Loading : SignUpState()
        object Success : SignUpState()
        data class Error(val message: String) : SignUpState()
    }

    // MutableStateFlow to hold the current state of the sign-up UI.
    // It's private to ensure that only the ViewModel can modify the state.
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)

    // Public StateFlow to expose the UI state to the Fragment.
    val signUpState: StateFlow<SignUpState> = _signUpState

    /**
     * Initiates the user registration process.
     * It launches a coroutine to call the signUp method of the repository.
     * @param email The new user's email.
     * @param password The new user's password.
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            // The ViewModel collects the results emitted by the AuthRepository's Flow.
            authRepository.signUp(email, password).collect { result ->
                // A 'when' expression is used to handle each possible state from the repository.
                when (result) {
                    is AuthResultState.Loading -> {
                        // The repository is busy, so we update the UI state to Loading.
                        _signUpState.value = SignUpState.Loading
                    }
                    is AuthResultState.Success -> {
                        // The registration was successful, so we update the UI state to Success.
                        _signUpState.value = SignUpState.Success
                    }
                    is AuthResultState.Error -> {
                        // The registration failed, so we update the UI state to Error with the message.
                        _signUpState.value = SignUpState.Error(result.message)
                    }
                }
            }
        }
    }
}

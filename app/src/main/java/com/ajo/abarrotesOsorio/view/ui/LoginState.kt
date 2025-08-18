package com.ajo.abarrotesOsorio.view.ui

// Sealed class to represent different login states
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
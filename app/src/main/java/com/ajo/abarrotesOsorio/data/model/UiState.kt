package com.ajo.abarrotesOsorio.data.model

sealed class UiState(val isSuccess: Boolean = false, val isError: Boolean = false, val message: String = "") {
    object Loading : UiState()
    class Success(message: String) : UiState(isSuccess = true, message = message)
    class Error(message: String) : UiState(isError = true, message = message)
}

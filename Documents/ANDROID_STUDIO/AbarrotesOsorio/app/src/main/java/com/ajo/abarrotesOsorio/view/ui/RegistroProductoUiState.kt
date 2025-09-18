package com.ajo.abarrotesOsorio.view.ui

sealed class RegistroProductoUiState {
    object Idle : RegistroProductoUiState()
    object Loading : RegistroProductoUiState()
    object Success : RegistroProductoUiState()
    data class Error(val message: String) : RegistroProductoUiState()
}
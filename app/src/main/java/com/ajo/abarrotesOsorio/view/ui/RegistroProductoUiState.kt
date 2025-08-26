package com.ajo.abarrotesOsorio.view.ui

/**
 * Sellada para representar los diferentes estados de la UI.
 */
sealed class RegistroProductoUiState {
    object Idle : RegistroProductoUiState()
    object Loading : RegistroProductoUiState()
    object Success : RegistroProductoUiState()
    data class Error(val message: String) : RegistroProductoUiState()
}
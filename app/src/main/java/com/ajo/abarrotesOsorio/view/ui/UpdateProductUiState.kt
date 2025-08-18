package com.ajo.abarrotesOsorio.view.ui

sealed class UpdateProductUiState {
    object Idle : UpdateProductUiState()
    object Loading : UpdateProductUiState()
    data class Success(val message: String) : UpdateProductUiState()
    data class Error(val errorMessage: String) : UpdateProductUiState()
}
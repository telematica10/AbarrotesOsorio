package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.repository.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.view.ui.RegistroProductoUiState
import kotlinx.coroutines.launch

class RegistroProductoViewModel(private val repository: InventarioRepository) : ViewModel() {

    private val _guardarProductoUiState = MutableLiveData<RegistroProductoUiState>()
    val guardarProductoUiState: LiveData<RegistroProductoUiState> get() = _guardarProductoUiState

    fun guardarProducto(producto: Producto) {
        viewModelScope.launch {
            _guardarProductoUiState.value = RegistroProductoUiState.Loading // Indica que la operación está en curso
            val exito = repository.guardarProducto(producto)
            if (exito) {
                _guardarProductoUiState.value = RegistroProductoUiState.Success // Notifica al fragmento que fue exitoso
            } else {
                _guardarProductoUiState.value = RegistroProductoUiState.Error("Error al guardar el producto") // Notifica el error
            }
        }
    }

    fun resetUiState() {
        _guardarProductoUiState.value = RegistroProductoUiState.Idle
    }
}

class RegistroProductoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroProductoViewModel::class.java)) {
            val repository = InventarioRepository(FirestoreHelper.firestoreInstance)
            @Suppress("UNCHECKED_CAST")
            return RegistroProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida: " + modelClass.name)
    }
}
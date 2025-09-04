package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.data.repository.ProductoEditRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoEditViewModel(
    private val repository: ProductoEditRepository
) : ViewModel() {
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()
    fun actualizarProducto(producto: Producto, esPropietario: Boolean) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading
            val success = repository.actualizarProductoConPermisos(producto, esPropietario)
            if (success) {
                _saveState.value = SaveState.Success("Producto actualizado correctamente")
            } else {
                _saveState.value = SaveState.Error("No se pudo actualizar el producto")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

}

class ProductoEditViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repository = ProductoEditRepository(FirestoreHelper.firestoreInstance)
            return ProductoEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class SaveState {
    object Idle : SaveState()
    object Loading : SaveState()
    data class Success(val message: String) : SaveState()
    data class Error(val message: String) : SaveState()
}

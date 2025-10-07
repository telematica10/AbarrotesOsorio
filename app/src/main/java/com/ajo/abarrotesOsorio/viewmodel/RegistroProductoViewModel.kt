package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.repository.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.data.repository.ProveedorRepository
import com.ajo.abarrotesOsorio.view.ui.RegistroProductoUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegistroProductoViewModel(private val repository: InventarioRepository,
                                private val proveedorRepository: ProveedorRepository
) : ViewModel() {

    val proveedores: StateFlow<List<Proveedor>> = proveedorRepository.getProveedores()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _guardarProductoUiState = MutableLiveData<RegistroProductoUiState>()
    val guardarProductoUiState: LiveData<RegistroProductoUiState> get() = _guardarProductoUiState

    fun guardarProducto(producto: Producto) {
        viewModelScope.launch {
            _guardarProductoUiState.value = RegistroProductoUiState.Loading
            try {
                val resultado = repository.guardarNuevoProductoConValidacion(producto)
                when (resultado) {
                    is InventarioRepository.GuardarProductoResult.Success -> {
                        _guardarProductoUiState.value = RegistroProductoUiState.Success
                    }
                    is InventarioRepository.GuardarProductoResult.DuplicateProduct -> {
                        val proveedor = repository.getProveedorById(resultado.producto.id_proveedor)
                        val proveedorNombre = proveedor?.nombre ?: "desconocido"
                        val mensaje = "El producto ya se encuentra registrado: ${resultado.producto.nombre_producto} con el proveedor: $proveedorNombre"
                        _guardarProductoUiState.value = RegistroProductoUiState.Error(mensaje)
                    }
                }
            } catch (e: Exception) {
                _guardarProductoUiState.value = RegistroProductoUiState.Error("Error al registrar el producto: ${e.message}")
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
            @Suppress("UNCHECKED_CAST")
            val firestore = FirestoreHelper.firestoreInstance
            val inventarioRepo = InventarioRepository(firestore)
            val proveedorRepo = ProveedorRepository(firestore)
            return RegistroProductoViewModel(inventarioRepo,proveedorRepo) as T
        }
        throw IllegalArgumentException("Clase de ViewModel desconocida: " + modelClass.name)
    }
}
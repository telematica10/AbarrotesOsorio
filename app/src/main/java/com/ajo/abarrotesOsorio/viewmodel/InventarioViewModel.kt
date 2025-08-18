package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.view.ui.UpdateProductUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class InventarioViewModel(private val repository: InventarioRepository) : ViewModel() {

    private val _productosLiveData = MutableLiveData<List<Producto>>()
    val productosLiveData: LiveData<List<Producto>> get() = _productosLiveData

    // Nuevo StateFlow para manejar el estado de la UI durante la actualización.
    private val _updateProductUiState = MutableStateFlow<UpdateProductUiState>(UpdateProductUiState.Idle)
    val updateProductUiState: StateFlow<UpdateProductUiState> = _updateProductUiState.asStateFlow()

    fun iniciarObservacionInventario(categoriaId: String?) {
        viewModelScope.launch {
            try {
                repository.getAllProductos(categoriaId).collect { productos ->
                    _productosLiveData.postValue(productos)
                }
            } catch (e: Exception) {
                _updateProductUiState.value = UpdateProductUiState.Error("Error al obtener inventario: ${e.message}")
            }
        }
    }

    /**
     * Ahora el método del repositorio retorna un booleano que se asigna a 'exito'.
     * Esto permite que la lógica del 'if' sea correcta.
     */
    fun actualizarStock(idProducto: String, nuevoStock: Int) {
        viewModelScope.launch {
            _updateProductUiState.value = UpdateProductUiState.Loading
            try {
                // 🔹 LÍNEA CORREGIDA: Asignamos el resultado de la función a 'exito'
                val exito: Boolean = repository.actualizarStock(idProducto, nuevoStock)

                if (exito) {
                    _updateProductUiState.value = UpdateProductUiState.Success("Stock actualizado con éxito.")
                } else {
                    _updateProductUiState.value = UpdateProductUiState.Error("No se pudo encontrar el producto para actualizar el stock.")
                }
            } catch (e: Exception) {
                // Manejo de errores a nivel de red o base de datos.
                _updateProductUiState.value = UpdateProductUiState.Error("Error de conexión o inesperado: ${e.message}")
            }
        }
    }

    /**
     * Restablece el estado de la UI a 'Idle' después de una operación.
     */
    fun resetUiState() {
        _updateProductUiState.value = UpdateProductUiState.Idle
    }
}

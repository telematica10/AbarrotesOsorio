package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.repository.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.ProductoConProveedor
import com.ajo.abarrotesOsorio.view.ui.UpdateProductUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class InventarioViewModel(private val repository: InventarioRepository) : ViewModel() {

    private val _productosConProveedorLiveData = MutableLiveData<List<ProductoConProveedor>>()
    val productosConProveedorLiveData: LiveData<List<ProductoConProveedor>> get() = _productosConProveedorLiveData

    private var _productosConProveedorList: List<ProductoConProveedor> = listOf()

    private val _navegarARegistroProducto = MutableLiveData<String?>()
    val navegarARegistroProducto: LiveData<String?> = _navegarARegistroProducto

    private val _updateProductUiState = MutableStateFlow<UpdateProductUiState>(UpdateProductUiState.Idle)
    val updateProductUiState: StateFlow<UpdateProductUiState> = _updateProductUiState.asStateFlow()

    fun iniciarObservacionInventario(categoriaId: String?, proveedorId: String?) {
        viewModelScope.launch {
            try {
                repository.getAllProductos(categoriaId, proveedorId).collect { productos ->
                    val productosConProveedor = coroutineScope {
                        productos.map { producto ->
                            async {
                                val proveedor = repository.getProveedorById(producto.id_proveedor)
                                ProductoConProveedor(producto, proveedor?.nombre)
                            }
                        }.map { it.await() }
                    }
                    _productosConProveedorList = productosConProveedor
                    _productosConProveedorLiveData.postValue(productosConProveedor)
                }
            } catch (e: Exception) {
                _updateProductUiState.value = UpdateProductUiState.Error("Error al obtener inventario: ${e.message}")
            }
        }
    }

    fun filtrarProductos(query: String) {
        val filteredList = if (query.isBlank()) {
            _productosConProveedorList
        } else {
            _productosConProveedorList.filter {
                it.producto.nombre_producto.contains(query, ignoreCase = true)
            }
        }
        _productosConProveedorLiveData.postValue(filteredList)
    }

    fun actualizarStock(idProducto: String, nuevoStock: Int) {
        viewModelScope.launch {
            _updateProductUiState.value = UpdateProductUiState.Loading
            try {
                val exito: Boolean = repository.actualizarStock(idProducto, nuevoStock)

                if (exito) {
                    _updateProductUiState.value = UpdateProductUiState.Success("Stock actualizado con éxito.")
                } else {
                    _updateProductUiState.value = UpdateProductUiState.Error("No se pudo encontrar el producto para actualizar el stock.")
                }
            } catch (e: Exception) {
                _updateProductUiState.value = UpdateProductUiState.Error("Error de conexión o inesperado: ${e.message}")
            }
        }
    }

    fun resetUiState() {
        _updateProductUiState.value = UpdateProductUiState.Idle
    }

    fun buscarProductoPorCodigo(barcode: String) {
        viewModelScope.launch {
            _updateProductUiState.value = UpdateProductUiState.Loading // Indica que la búsqueda está en curso
            try {
                val producto = repository.getProductoByBarcode(barcode)

                if (producto != null) {
                    _updateProductUiState.value = UpdateProductUiState.Success("Producto encontrado: ${producto.nombre_producto}")
                } else {
                    _navegarARegistroProducto.value = barcode
                }
            } catch (e: Exception) {
                _updateProductUiState.value = UpdateProductUiState.Error("Error al buscar el producto: ${e.message}")
            }
        }
    }

    fun onNavegacionARegistroCompleta() {
        _navegarARegistroProducto.value = null
    }
}
package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.Producto
import kotlinx.coroutines.launch

class InventarioViewModel(private val repository: InventarioRepository) : ViewModel() {

    private val _productosLiveData = MutableLiveData<List<Producto>>()
    val productosLiveData: LiveData<List<Producto>> get() = _productosLiveData

    /**
     * Inicia la observación en tiempo real del inventario.
     * Si se provee una categoriaId, filtra los productos por esa categoría.
     */
    fun iniciarObservacionInventario(categoriaId: String?) {
        viewModelScope.launch {
            try {
                // 🔹 LÍNEA CORREGIDA: Usamos .collect para escuchar el flujo de forma continua
                // y actualizar LiveData con cada nueva lista de productos.
                repository.getAllProductos(categoriaId).collect { productos ->
                    _productosLiveData.postValue(productos)
                }
            } catch (e: Exception) {
                // TODO: Manejar el error, por ejemplo, mostrando un mensaje al usuario.
            }
        }
    }

    /**
     * Actualiza el stock de un producto en la base de datos.
     * @param idProducto El ID del producto a actualizar.
     * @param nuevoStock La nueva cantidad de stock.
     */
    fun actualizarStock(idProducto: String, nuevoStock: Int) {
        viewModelScope.launch {
            repository.actualizarStock(idProducto, nuevoStock)
        }
    }
}

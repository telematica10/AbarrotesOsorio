package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajo.abarrotesOsorio.data.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.Producto

class InventarioViewModel(private val repository: InventarioRepository) : ViewModel() {

    private val _productosLiveData = MutableLiveData<List<Producto>>()
    val productosLiveData: LiveData<List<Producto>> get() = _productosLiveData

    fun iniciarEscuchaInventarioPorCambiosFiltrado(
        categoriaId: String?,
        onProductoChange: (tipo: String, producto: Producto) -> Unit
    ) {
        repository.escucharInventarioEnTiempoRealFiltrado(categoriaId, onProductoChange)
    }

    fun actualizarStock(productoId: String, nuevoStock: Int) {
        repository.actualizarStock(productoId, nuevoStock)
    }

}

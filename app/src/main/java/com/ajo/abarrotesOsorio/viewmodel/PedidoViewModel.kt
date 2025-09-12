package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.model.PedidoItem
import com.ajo.abarrotesOsorio.data.repository.PedidoRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PedidoViewModel(private val repository: PedidoRepository) : ViewModel() {

    private val _pedidoList = MutableLiveData<List<PedidoItem>>()
    val pedidoList: LiveData<List<PedidoItem>> get() = _pedidoList

    private val _total = MutableLiveData<Double>()
    val total: LiveData<Double> get() = _total

    private val _totalProductos = MutableLiveData<Int>()
    val totalProductos: LiveData<Int> get() = _totalProductos

    private val _pedidoGuardado = MutableLiveData<Boolean>()
    val pedidoGuardado: LiveData<Boolean> get() = _pedidoGuardado

    private val _proveedorNombre = MutableLiveData<String>()
    val proveedorNombre: LiveData<String> get() = _proveedorNombre

    fun getProductosParaPedido(proveedorId: String) {
        viewModelScope.launch {
            repository.getProductosParaPedido()
                .catch {
                    // Manejar errores
                }
                .collectLatest { allPedidos ->
                    val filteredList = allPedidos.filter { it.idProveedor == proveedorId }
                        .map { it.copy(recibido = true) }
                        .sortedBy { it.nombreProducto }
                    _pedidoList.value = filteredList
                    updateTotal()
                    updateTotalProductos()
                }
        }
    }

    fun getProveedorNombre(proveedorId: String) {
        viewModelScope.launch {
            repository.getProveedorNombre(
                proveedorId,
                onSuccess = { nombre ->
                    _proveedorNombre.value = nombre
                },
                onFailure = {
                    // Manejar errores
                    _proveedorNombre.value = "Proveedor Desconocido"
                }
            )
        }
    }

    fun updateTotal() {
        val currentList = _pedidoList.value
        if (currentList != null) {
            val total = currentList.sumOf { if (it.recibido) it.subtotal else 0.0 }
            _total.value = total
        }
    }

    fun updateTotalProductos() {
        val currentList = _pedidoList.value
        if (currentList != null) {
            val totalCantidad = currentList.sumOf { if (it.recibido) it.cantidadAPedir else 0 }
            _totalProductos.value = totalCantidad
        }
    }

    fun finalizarPedido(pedido: List<PedidoItem>) {
        viewModelScope.launch {
            repository.finalizarPedido(
                pedido,
                onSuccess = {
                    _pedidoGuardado.value = true
                },
                onFailure = {
                    _pedidoGuardado.value = false
                }
            )
        }
    }
}

class PedidoViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PedidoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repository = PedidoRepository(FirestoreHelper.firestoreInstance)
            return PedidoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.repository.InventarioRepository
import com.ajo.abarrotesOsorio.data.repository.VentasRepository
import com.ajo.abarrotesOsorio.data.model.Venta
import com.ajo.abarrotesOsorio.data.model.VentaItem
import kotlinx.coroutines.launch

class VentaViewModel(
    private val inventarioRepository: InventarioRepository,
    private val ventasRepository: VentasRepository
) : ViewModel() {
    val carrito = MutableLiveData<MutableList<VentaItem>>(mutableListOf())

    val ventaGuardadaEvent = MutableLiveData<Venta?>(null)

    fun agregarAlCarrito(item: VentaItem) {
        val listaActual = carrito.value ?: mutableListOf()
        val productoExistente = listaActual.find { it.nombre == item.nombre && it.precio == item.precio }

        if (productoExistente != null) {
            productoExistente.cantidad += 1
            productoExistente.subtotal = productoExistente.cantidad * productoExistente.precio
        } else {
            listaActual.add(item)
        }
        carrito.value = listaActual.toMutableList()
    }

    fun buscarYAgregarProducto(codigoBarras: String) {
        viewModelScope.launch {
            val producto = inventarioRepository.getProductoByBarcode(codigoBarras)
            if (producto != null) {
                val ventaItem = VentaItem(producto.nombre_producto, producto.precio_de_venta, 1)
                ventaItem.subtotal = producto.precio_de_venta * 1
                agregarAlCarrito(ventaItem)
            } else {

            }
        }
    }

    fun guardarVenta() {
        val listaVenta = carrito.value ?: return
        viewModelScope.launch {
            val ventaGuardada = ventasRepository.guardarVenta(listaVenta)
            if (ventaGuardada != null) {
                ventaGuardadaEvent.value = ventaGuardada
                carrito.value = mutableListOf()
            }
        }
    }

    fun onVentaGuardadaEventConsumed() {
        ventaGuardadaEvent.value = null
    }

}
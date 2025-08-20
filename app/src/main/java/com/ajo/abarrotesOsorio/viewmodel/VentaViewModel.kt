package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.InventarioRepository
import com.ajo.abarrotesOsorio.data.VentasRepository
import com.ajo.abarrotesOsorio.data.model.Venta
import com.ajo.abarrotesOsorio.data.model.VentaItem
import kotlinx.coroutines.launch

class VentaViewModel(
    private val inventarioRepository: InventarioRepository,
    private val ventasRepository: VentasRepository
) : ViewModel() {
    val carrito = MutableLiveData<MutableList<VentaItem>>(mutableListOf())

    val ventaGuardadaEvent = MutableLiveData<Venta?>(null)

    /**
     * Agrega un nuevo producto al carrito. Si el producto ya existe,
     * se actualiza la cantidad en lugar de agregar una nueva entrada.
     * @param item El VentaItem que se va a agregar.
     */
    fun agregarAlCarrito(item: VentaItem) {
        val listaActual = carrito.value ?: mutableListOf()
        val productoExistente = listaActual.find { it.nombre == item.nombre && it.precio == item.precio }

        if (productoExistente != null) {
            productoExistente.cantidad += 1
            productoExistente.subtotal = productoExistente.cantidad * productoExistente.precio
        } else {
            listaActual.add(item)
        }
        // Crea una nueva lista para forzar la actualización del LiveData y notificar al Fragment.
        carrito.value = listaActual.toMutableList()
    }

    /**
     * Busca un producto en el repositorio y lo agrega al carrito si se encuentra.
     * @param codigoBarras El código de barras a buscar.
     */
    fun buscarYAgregarProducto(codigoBarras: String) {
        viewModelScope.launch {
            val producto = inventarioRepository.getProductoByBarcode(codigoBarras)
            if (producto != null) {
                // Si el producto se encuentra, creamos un VentaItem y lo agregamos al carrito.
                val ventaItem = VentaItem(producto.nombre_producto, producto.precio_de_venta, 1)
                // Se agrega el subtotal
                ventaItem.subtotal = producto.precio_de_venta * 1
                agregarAlCarrito(ventaItem)
            } else {
                // Aquí puedes manejar el caso en que el producto no se encuentra.
                // Por ejemplo, mostrar un Toast o un Snackbar.
                // Log.d("VentaViewModel", "Producto no encontrado.")
            }
        }
    }

    /**
     * Guarda la venta actual usando el VentasRepository.
     */
    fun guardarVenta() {
        val listaVenta = carrito.value ?: return // No hagas nada si el carrito está vacío
        viewModelScope.launch {
            val ventaGuardada = ventasRepository.guardarVenta(listaVenta)
            if (ventaGuardada != null) {
                ventaGuardadaEvent.value = ventaGuardada
                carrito.value = mutableListOf()
            }
        }
    }

    /**
     * Resetea el evento de venta guardada.
     * Debe llamarse desde el Fragment después de haber manejado el evento.
     */
    fun onVentaGuardadaEventConsumed() {
        ventaGuardadaEvent.value = null
    }

}
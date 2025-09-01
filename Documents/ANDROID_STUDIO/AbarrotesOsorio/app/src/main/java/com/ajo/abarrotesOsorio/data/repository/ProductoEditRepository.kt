package com.ajo.abarrotesOsorio.data.repository

import android.util.Log
import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoEditRepository(private val firestore: FirebaseFirestore){
    private val productoCollection = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION)

    suspend fun actualizarProductoConPermisos(producto: Producto, esPropietario: Boolean) {
        val productoRef = productoCollection.document(producto.codigo_de_barras_sku)

        try {
            val updates = mutableMapOf<String, Any>()

            updates["nombre_producto"] = producto.nombre_producto
            updates["proveedor"] = producto.proveedor
            updates["stock_actual"] = producto.stock_actual
            updates["notas_observaciones"] = producto.notas_observaciones
            updates["fecha_de_caducidad"] = producto.fecha_de_caducidad
            updates["proveedor_preferente"] = producto.proveedor_preferente

            if (esPropietario) {
                updates["precio_de_venta"] = producto.precio_de_venta
                updates["cantidad"] = producto.cantidad
                updates["stock_minimo"] = producto.stock_minimo
            }

            productoRef.update(updates).await()
            Log.d("InventarioRepository", "Producto ${producto.codigo_de_barras_sku} actualizado con éxito.")
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al actualizar el producto ${producto.codigo_de_barras_sku}", e)
            throw e
        }
    }

}
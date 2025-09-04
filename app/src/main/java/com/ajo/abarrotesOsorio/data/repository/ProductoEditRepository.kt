package com.ajo.abarrotesOsorio.data.repository

import android.util.Log
import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoEditRepository(private val firestore: FirebaseFirestore){
    private val productoCollection = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION)

    suspend fun actualizarProductoConPermisos(producto: Producto, esPropietario: Boolean): Boolean  {
        val productoRef = productoCollection.document(producto.id)

        return try {
            val updates = mutableMapOf<String, Any>()

            updates["codigo_de_barras_sku"] = producto.codigo_de_barras_sku
            updates["nombre_producto"] = producto.nombre_producto
            updates["id_proveedor"] = producto.id_proveedor
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
            Log.d("InventarioRepository", "Producto ${producto.id} actualizado con éxito.")
            true
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al actualizar el producto ${producto.id}", e)
            false
        }
    }

}
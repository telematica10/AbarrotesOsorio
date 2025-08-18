package com.ajo.abarrotesOsorio.data.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductoEditRepository(private val firestore: FirebaseFirestore){
    private val productoCollection = firestore.collection("inventario")

    /**
     * Actualiza un producto en Firestore con base en los permisos del usuario,
     * excluyendo los campos que no deben ser editados.
     *
     * @param producto El objeto Producto que contiene los datos actualizados.
     * @param esPropietario Booleano para saber si el usuario es el dueño de la cuenta.
     */
    suspend fun actualizarProductoConPermisos(producto: Producto, esPropietario: Boolean) {
        // en su lugar se usa 'producto.codigo_de_barras_sku' que es el ID del documento en Firestore.
        val productoRef = productoCollection.document(producto.codigo_de_barras_sku)

        try {
            // Se crea un mapa con los campos que se pueden actualizar para todos los usuarios.
            val updates = mutableMapOf<String, Any>()

            // Campos básicos que todos los usuarios pueden editar
            updates["nombre_producto"] = producto.nombre_producto
            updates["proveedor"] = producto.proveedor
            updates["stock_actual"] = producto.stock_actual
            updates["notas_observaciones"] = producto.notas_observaciones
            updates["ubicacion_en_tienda_almacen"] = producto.ubicacion_en_tienda_almacen
            updates["fecha_de_caducidad"] = producto.fecha_de_caducidad
            updates["proveedor_preferente"] = producto.proveedor_preferente

            // Si el usuario es propietario, se añaden los campos financieros y de cantidad que pueden ser editados.
            if (esPropietario) {
                updates["precio_de_venta"] = producto.precio_de_venta
                updates["cantidad"] = producto.cantidad
                updates["stock_minimo"] = producto.stock_minimo
            }

            // Se usa .update() para modificar solo los campos del mapa, manteniendo los demás intactos.
            productoRef.update(updates).await()
            Log.d("InventarioRepository", "Producto ${producto.codigo_de_barras_sku} actualizado con éxito.")
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al actualizar el producto ${producto.codigo_de_barras_sku}", e)
            throw e
        }
    }

}
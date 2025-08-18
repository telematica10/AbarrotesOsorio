package com.ajo.abarrotesOsorio.data

import android.util.Log
import com.ajo.abarrotesOsorio.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class InventarioRepository(private val firestore: FirebaseFirestore) {

    private val productoCollection = firestore.collection("inventario")

    /**
     * Obtiene una lista de productos en tiempo real desde Firestore, ordenados alfabéticamente por nombre.
     * La lista se actualizará automáticamente cada vez que los productos cambien.
     *
     * @param categoriaId Un ID de categoría opcional para filtrar los productos.
     * @return Un [Flow] de [List] de [Producto] que emite actualizaciones en tiempo real.
     */
    fun getAllProductos(categoriaId: String? = null): Flow<List<Producto>> = callbackFlow {
        // 🔹 LÓGICA DE FILTRADO: Se construye la consulta con un filtro si categoriaId no es null.
        var query: Query = if (categoriaId != null) {
            productoCollection.whereEqualTo("categoria_id", categoriaId)
        } else {
            productoCollection
        }

        // 🔹 LÓGICA DE ORDENACIÓN: Se añade el filtro de ordenación por nombre.
        // Firestore ordena por defecto de forma ascendente, y los números van antes que las letras.
        query = query.orderBy("nombre_producto", Query.Direction.ASCENDING)

        // 🔹 SE INICIA EL OYENTE EN TIEMPO REAL: addSnapshotListener
        val subscription = query.addSnapshotListener { snapshot, e ->
            // Manejo de errores: Si hay un error, cerramos el flujo con la excepción.
            if (e != null) {
                Log.e("InventarioRepository", "Error al obtener productos", e)
                // Usamos close() para terminar el flujo si ocurre un error.
                close(e)
                return@addSnapshotListener
            }

            // Si el snapshot es nulo o no tiene documentos, emitimos una lista vacía.
            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            // Convertimos los documentos del snapshot a objetos Producto y los enviamos al flujo.
            val productos = snapshot.toObjects(Producto::class.java)
            trySend(productos).isSuccess

            Log.d("InventarioRepository", "Se emitieron ${productos.size} productos.")
        }

        // 🔹 GESTIÓN DEL FLUJO:
        // Esta función se ejecuta cuando el flujo deja de ser observado (ej. la pantalla se cierra).
        // Es CRUCIAL para cancelar el oyente de Firestore y evitar fugas de memoria.
        awaitClose {
            subscription.remove()
            Log.d("InventarioRepository", "Oyente de Firestore removido.")
        }
    }

    /**
     * Actualiza el stock de un producto en la base de datos de Firestore.
     *
     * @param idProducto El ID del documento del producto a actualizar.
     * @param nuevoStock La nueva cantidad de stock.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    suspend fun actualizarStock(idProducto: String, nuevoStock: Int): Boolean {
        // 🔹 LÍNEA CORREGIDA: Ahora devolvemos un Boolean
        val productoRef = productoCollection.document(idProducto)
        return try {
            // Intenta actualizar el documento
            productoRef.update("stock_actual", nuevoStock).await()
            Log.d("InventarioRepository", "Stock actualizado con éxito para el producto: $idProducto")
            true // Devuelve true en caso de éxito
        } catch (e: Exception) {
            // Captura cualquier excepción y devuelve false
            Log.e("InventarioRepository", "Error al actualizar stock para $idProducto", e)
            false // Devuelve false en caso de error
        }
    }
}

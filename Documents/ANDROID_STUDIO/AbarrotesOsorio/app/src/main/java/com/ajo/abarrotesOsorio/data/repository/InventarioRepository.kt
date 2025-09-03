package com.ajo.abarrotesOsorio.data.repository

import android.util.Log
import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class InventarioRepository(private val firestore: FirebaseFirestore) {

    private val productoCollection = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION)

    fun getAllProductos(categoriaId: String? = null, proveedorId: String? = null): Flow<List<Producto>> = callbackFlow {
        var query: Query = productoCollection // Consulta base

        // Aplicamos el filtro correspondiente
        if (categoriaId != null) {
            query = query.whereEqualTo("categoria_id", categoriaId)
        } else if (proveedorId != null) {
            query = query.whereEqualTo("id_proveedor", proveedorId)
        }

       /* var query: Query = if (categoriaId != null) {
            productoCollection.whereEqualTo("categoria_id", categoriaId)
        } else {
            productoCollection
        }*/

        query = query.orderBy("nombre_producto", Query.Direction.ASCENDING)

        val subscription = query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("InventarioRepository", "Error al obtener productos", e)
                close(e)
                return@addSnapshotListener
            }

            if (snapshot == null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val productos = snapshot.toObjects(Producto::class.java)
            trySend(productos).isSuccess

            Log.d("InventarioRepository", "Se emitieron ${productos.size} productos.")
        }

        awaitClose {
            subscription.remove()
            Log.d("InventarioRepository", "Oyente de Firestore removido.")
        }
    }

    suspend fun actualizarStock(idProducto: String, nuevoStock: Int): Boolean {
        val productoRef = productoCollection.document(idProducto)
        return try {
            productoRef.update("stock_actual", nuevoStock).await()
            Log.d("InventarioRepository", "Stock actualizado con éxito para el producto: $idProducto")
            true
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al actualizar stock para $idProducto", e)
            false
        }
    }

    suspend fun getProductoByBarcode(barcode: String): Producto? {
        return try {
            val querySnapshot = productoCollection
                .whereEqualTo("codigo_de_barras_sku", barcode)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                null
            } else {
                querySnapshot.documents.first().toObject(Producto::class.java)
            }
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al buscar producto por código de barras", e)
            null
        }
    }

    suspend fun guardarProducto(producto: Producto): Boolean {
        return try {
            firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION).add(producto).await()
            true
        } catch (e: Exception) {
            false
        }
    }

}
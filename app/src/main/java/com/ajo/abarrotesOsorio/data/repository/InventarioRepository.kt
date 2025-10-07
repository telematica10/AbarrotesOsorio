package com.ajo.abarrotesOsorio.data.repository

import android.util.Log
import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*

class InventarioRepository(private val firestore: FirebaseFirestore) {

    private val productoCollection = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION)
    private val proveedorCollection = firestore.collection(FirestoreConstants.PROVEEDORES_COLLECTION)

    // Enum para manejar los posibles resultados al guardar un producto
    sealed class GuardarProductoResult {
        data class Success(val productoId: String) : GuardarProductoResult()
        data class DuplicateProduct(val producto: Producto) : GuardarProductoResult()
    }

    fun getAllProductos(categoriaId: String? = null, proveedorId: String? = null): Flow<List<Producto>> = callbackFlow {
        var query: Query = productoCollection

        if (categoriaId != null) {
            query = query.whereEqualTo("categoria_id", categoriaId)
        } else if (proveedorId != null) {
            query = query.whereEqualTo("id_proveedor", proveedorId)
        }

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

    /**
     * Valida y guarda un nuevo producto. La validación se realiza para asegurar
     * que no exista un producto con el mismo código de barras y proveedor.
     * @param producto El objeto Producto a guardar.
     * @return Un objeto de tipo [GuardarProductoResult] que indica el resultado de la operación.
     */
    suspend fun guardarNuevoProductoConValidacion(producto: Producto): GuardarProductoResult {
        // 1. Verificar si ya existe un producto con el mismo código de barras y proveedor.
        val existingProductSnapshot = productoCollection
            .whereEqualTo("codigo_de_barras_sku", producto.codigo_de_barras_sku)
            .whereEqualTo("id_proveedor", producto.id_proveedor)
            //.whereEqualTo("status", "activo")
            .get()
            .await()

        // 2. Si ya existe un producto con esta combinación, se retorna un error.
        if (!existingProductSnapshot.isEmpty) {
            val existingProducto = existingProductSnapshot.documents.first().toObject(Producto::class.java)
            return GuardarProductoResult.DuplicateProduct(existingProducto ?: producto)
        }

        // 3. Si la combinación es única, se procede a guardar el nuevo producto.
        val documentRef = productoCollection.document()
        val nuevoProducto = producto.copy(id = documentRef.id)

        documentRef.set(nuevoProducto).await()

        return GuardarProductoResult.Success(documentRef.id)
    }

    suspend fun getProveedorById(proveedorId: String): Proveedor? {
        if (proveedorId.isBlank()) return null
        return try {
            val document = proveedorCollection.document(proveedorId).get().await()
            document.toObject(Proveedor::class.java)
        } catch (e: Exception) {
            Log.e("InventarioRepository", "Error al obtener proveedor con id $proveedorId", e)
            null
        }
    }

}
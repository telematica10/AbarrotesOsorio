package com.ajo.abarrotesOsorio.data.repository

import android.util.Log
import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProductoEditRepository(private val firestore: FirebaseFirestore){

    private val productoCollection = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION)

    suspend fun actualizarProducto(producto: Producto): Boolean {
        val productoRef = productoCollection.document(producto.id)

        return try {
            productoRef.set(producto).await()
            Log.d("ProductoRepository", "Producto ${producto.id} actualizado con éxito.")
            true
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Error al actualizar el producto ${producto.id}", e)
            false
        }
    }

    fun deleteProducto(productoId: String): Flow<Unit> = flow {
        productoCollection.document(productoId).delete().await()
        emit(Unit)
    }

}
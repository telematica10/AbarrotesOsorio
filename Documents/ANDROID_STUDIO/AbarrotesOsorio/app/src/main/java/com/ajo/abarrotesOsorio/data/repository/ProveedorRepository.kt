package com.ajo.abarrotesOsorio.data.repository

import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log

interface ProveedorRepositoryI {

    fun getProveedores(): Flow<List<Proveedor>>

    suspend fun addProveedor(proveedor: Proveedor)

    suspend fun updateProveedor(proveedor: Proveedor)

    suspend fun deleteProveedor(id: String)
}

class ProveedorRepository(private val firestore: FirebaseFirestore) : ProveedorRepositoryI {

    private val proveedoresCollection = firestore.collection(FirestoreConstants.PROVEEDORES_COLLECTION)

    override fun getProveedores(): Flow<List<Proveedor>> = callbackFlow {
        val listenerRegistration = proveedoresCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("ProveedorRepository", "Error al obtener proveedores: ${e.message}", e)
                close(e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val proveedores = snapshot.documents.mapNotNull { document ->
                    document.toObject<Proveedor>()?.copy(id = document.id)
                }
                trySend(proveedores)
            }
        }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun addProveedor(proveedor: Proveedor) {
        try {
            proveedoresCollection.add(proveedor).await()
        } catch (e: Exception) {
            Log.e("ProveedorRepository", "Error al añadir proveedor: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateProveedor(proveedor: Proveedor) {
        try {
            proveedor.id?.let { id ->
                proveedoresCollection.document(id).set(proveedor).await()
            }
        } catch (e: Exception) {
            Log.e("ProveedorRepository", "Error al actualizar proveedor: ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteProveedor(id: String) {
        try {
            proveedoresCollection.document(id).delete().await()
        } catch (e: Exception) {
            Log.e("ProveedorRepository", "Error al eliminar proveedor: ${e.message}", e)
            throw e
        }
    }
}
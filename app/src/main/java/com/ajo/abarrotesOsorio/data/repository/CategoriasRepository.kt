package com.ajo.abarrotesOsorio.data.repository

import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CategoriaRepository(private val firestore: FirebaseFirestore) {


    fun getTodasLasCategorias(): Flow<List<Categoria>> = callbackFlow {
        val categoriasRef = firestore.collection(FirestoreConstants.CATEGORIAS_COLLECTION)

        val subscription =
            categoriasRef.addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val categorias = snapshot?.toObjects<Categoria>() ?: emptyList()
                trySend(categorias).isSuccess
            }

        awaitClose {
            subscription.remove()
        }
    }

    fun getProvedores(): Flow<List<Proveedor>> = callbackFlow {
        val proveedoresRef = firestore.collection(FirestoreConstants.PROVEEDORES_COLLECTION)
        val subscription =
            proveedoresRef.addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val proveedores = snapshot?.toObjects<Proveedor>() ?: emptyList()
                trySend(proveedores).isSuccess
            }
        awaitClose {
            subscription.remove()
        }
    }

}

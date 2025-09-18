package com.ajo.abarrotesOsorio.data.repository

import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects

class CategoriaRepository(private val firestore: FirebaseFirestore) {


    fun getTodasLasCategorias(): Flow<List<Categoria>> = callbackFlow {
        val categoriasRef = firestore.collection(FirestoreConstants.CATEGORIAS_COLLECTION)

        val subscription = categoriasRef.addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
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
}

package com.ajo.abarrotesOsorio.data.repository

import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CategoriaRepository(private val firestore: FirebaseFirestore) {
    val categoriasRef = firestore.collection(FirestoreConstants.CATEGORIAS_COLLECTION)
    fun getTodasLasCategorias(): Flow<List<Categoria>> = callbackFlow {
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

    fun addCategoria(nombre: String, imagen_url: String): Flow<Unit> = flow {
        val querySnapshot = categoriasRef.get().await()
        val nextIdNumber = querySnapshot.size() + 1
        val nextId = String.format("cat_%03d", nextIdNumber)

        val nuevaCategoria = Categoria(
            id = nextId,
            nombre = nombre,
            imagen_url = imagen_url
        )

        categoriasRef.document(nextId).set(nuevaCategoria).await()
        emit(Unit)
    }

    fun deleteCategoria(categoriaId: String): Flow<Unit> = flow {
        categoriasRef.document(categoriaId).delete().await()
        emit(Unit)
    }

}

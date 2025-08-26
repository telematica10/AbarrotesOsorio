package com.ajo.abarrotesOsorio.data

import com.ajo.abarrotesOsorio.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects

class CategoriaRepository(private val firestore: FirebaseFirestore) {

    /**
     * Obtiene una lista de categorías en tiempo real utilizando un Kotlin Flow.
     * @return un Flow que emite listas de Categoria cada vez que hay un cambio en Firestore.
     */
    fun getTodasLasCategorias(): Flow<List<Categoria>> = callbackFlow {
        // Se define la referencia a la colección de categorías
        val categoriasRef = firestore.collection(FirestoreConstants.CATEGORIAS_COLLECTION)

        // Se agrega un listener de cambios en tiempo real a la colección
        val subscription = categoriasRef.addSnapshotListener { snapshot: QuerySnapshot?, error: com.google.firebase.firestore.FirebaseFirestoreException? ->
            if (error != null) {
                // Si hay un error, se envía al Flow
                close(error)
                return@addSnapshotListener
            }

            // Se mapean los documentos a objetos Categoria y se envían al Flow
            val categorias = snapshot?.toObjects<Categoria>() ?: emptyList()
            trySend(categorias).isSuccess
        }

        // El bloque awaitClose asegura que el listener se elimine cuando el Flow se cancele
        awaitClose {
            subscription.remove()
        }
    }
}

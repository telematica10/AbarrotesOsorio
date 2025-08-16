package com.ajo.abarrotesOsorio.data

import android.util.Log
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasRepository(private val db: FirebaseFirestore) {

    fun obtenerCategorias(
        onResult: (List<Categoria>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("categorias")
            .orderBy("orden")
            .get()
            .addOnSuccessListener { snapshot ->
                val categorias = snapshot.documents.map { doc ->
                    doc.toObject(Categoria::class.java)!!.apply {
                        id = doc.id
                    }
                }
                onResult(categorias)
            }
            .addOnFailureListener { e ->
                Log.e("CategoriasRepository", "Error obteniendo categorías", e)
                onError(e)
            }
    }
}

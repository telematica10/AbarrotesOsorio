package com.ajo.abarrotesOsorio.data

import android.util.Log
import com.ajo.abarrotesOsorio.data.model.Producto
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class InventarioRepository(private val db: FirebaseFirestore) {

    companion object {
        const val CHANGE_ADDED = "ADDED"
        const val CHANGE_MODIFIED = "MODIFIED"
        const val CHANGE_REMOVED = "REMOVED"
    }

    fun escucharInventarioEnTiempoRealFiltrado(
        categoriaId: String?,
        onProductoChange: (tipo: String, producto: Producto) -> Unit
    ) {
        var query = db.collection("inventario").orderBy("nombre_producto")
        if (categoriaId != null) {
            query = query.whereEqualTo("categoria_id", categoriaId)
        }
        query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("InventarioRepository", "Error escuchando cambios", e)
                return@addSnapshotListener
            }
            if (snapshots != null) {
                for (dc in snapshots.documentChanges) {
                    val producto = dc.document.toObject(Producto::class.java)?.apply {
                        id = dc.document.id
                    }
                    if (producto?.nombre_producto.isNullOrBlank()) continue

                    when (dc.type) {
                        DocumentChange.Type.ADDED -> onProductoChange(CHANGE_ADDED, producto!!)
                        DocumentChange.Type.MODIFIED -> onProductoChange(CHANGE_MODIFIED, producto!!)
                        DocumentChange.Type.REMOVED -> onProductoChange(CHANGE_REMOVED, producto!!)
                    }
                }
            }
        }
    }


    fun actualizarStock(productoId: String, nuevoStock: Int) {
        db.collection("inventario")
            .document(productoId)
            .update("stock_actual", nuevoStock)
            .addOnFailureListener {
                Log.e("InventarioRepository", "Error al actualizar stock", it)
            }
    }
}

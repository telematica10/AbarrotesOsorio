package com.ajo.abarrotesOsorio.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ajo.abarrotesOsorio.data.model.Venta
import com.ajo.abarrotesOsorio.data.model.VentaItem
import com.ajo.abarrotesOsorio.viewmodel.VentaViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class VentasRepository(private val firestore: FirebaseFirestore) {

    private val ventasCollection = firestore.collection(FirestoreConstants.VENTAS_COLLECTION)

    /**
     * Guarda una nueva venta en Firestore.
     * @param listaVenta La lista de VentaItem que componen la venta.
     * @return El objeto Venta guardado si la venta se guardó con éxito, null en caso contrario.
     */
    suspend fun guardarVenta(listaVenta: List<VentaItem>): Venta? {
        if (listaVenta.isEmpty()) {
            Log.d("VentasRepository", "La lista de venta está vacía, no se guardará nada.")
            return null
        }

        val totalVenta = listaVenta.sumOf { it.subtotal }
        val fechaVenta = Date()

        val ventaMap = hashMapOf(
            "fechaVenta" to fechaVenta,
            "productos" to listaVenta.map { item ->
                hashMapOf(
                    "nombre" to item.nombre,
                    "precio" to item.precio,
                    "cantidad" to item.cantidad,
                    "subtotal" to item.subtotal
                )
            },
            "totalVenta" to totalVenta
        )

        return try {
            val docRef = ventasCollection.add(ventaMap).await()
            Log.d("VentasRepository", "Venta guardada con éxito con ID: ${docRef.id}")
            // Devuelve el objeto Venta que se guardó
            Venta(fechaVenta, listaVenta, totalVenta)
        } catch (e: Exception) {
            Log.e("VentasRepository", "Error al guardar la venta", e)
            null
        }
    }
}

/**
 * Factory para crear instancias de VentaViewModel.
 *
 * Esta clase inyecta las dependencias del repositorio al ViewModel,
 * asegurando un acoplamiento bajo y una mejor organización del código.
 */
class VentaViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VentaViewModel::class.java)) {
            val firestoreInstance = FirestoreHelper.firestoreInstance
            val productosRepository = InventarioRepository(firestoreInstance)
            val ventasRepository = VentasRepository(firestoreInstance)

            @Suppress("UNCHECKED_CAST")
            return VentaViewModel(productosRepository, ventasRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

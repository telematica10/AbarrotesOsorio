package com.ajo.abarrotesOsorio.data.repository

import com.ajo.abarrotesOsorio.data.FirestoreConstants
import com.ajo.abarrotesOsorio.data.model.PedidoItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class PedidoRepository(private val firestore: FirebaseFirestore) {

    fun getProductosParaPedido(): Flow<List<PedidoItem>> = callbackFlow {
        val productosRef = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION)

        val subscription =
            productosRef.addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val pedidoList = mutableListOf<PedidoItem>()
                for (document in snapshot?.documents ?: emptyList()) {
                    val stock = document.getLong("stock_actual")?.toInt() ?: 0
                    val stockMinimo = document.getLong("stock_minimo")?.toInt() ?: 0

                    val cantidadAPedir = stockMinimo - stock
                    if (cantidadAPedir > 0) {
                        val precioProveedor = document.getDouble("precio_proveedor") ?: 0.0
                        val subtotal = cantidadAPedir * precioProveedor

                        val item = PedidoItem(
                            idPedido = "",
                            idProducto = document.id,
                            nombreProductoProveedor = document.getString("nombre_producto_proveedor")
                                ?: "N/A",
                            nombreProducto = document.getString("nombre_producto") ?: "N/A",
                            cantidadAPedir = cantidadAPedir,
                            precioProveedor = precioProveedor,
                            precioPorUnidadProveedor = document.getDouble("precio_por_unidad_proveedor")
                                ?: 0.0,
                            subtotal = subtotal,
                            total = subtotal,
                            idProveedor = document.getString("id_proveedor") ?: "",
                        )
                        pedidoList.add(item)
                    }
                }
                trySend(pedidoList).isSuccess
            }

        awaitClose {
            subscription.remove()
        }
    }

    fun getProveedorNombre(
        proveedorId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection(FirestoreConstants.PROVEEDORES_COLLECTION).document(proveedorId)
            .get()
            .addOnSuccessListener { document ->
                val nombre = document.getString("nombre")
                if (nombre != null) {
                    onSuccess(nombre)
                } else {
                    onFailure(Exception("Nombre de proveedor no encontrado"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    suspend fun finalizarPedido(
        pedido: List<PedidoItem>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (pedido.isEmpty()) {
            onFailure(Exception("El pedido no contiene productos."))
            return
        }

        val batch = firestore.batch()
        val pedidoDocRef = firestore.collection(FirestoreConstants.PEDIDOS_COLLECTION).document()

        val productosData = pedido.map { item ->
            mapOf(
                "idProducto" to item.idProducto,
                "nombreProductoProveedor" to item.nombreProductoProveedor,
                "nombreProducto" to item.nombreProducto,
                "cantidadAPedir" to item.cantidadAPedir,
                "precioProveedor" to item.precioProveedor,
                "precioPorUnidadProveedor" to item.precioPorUnidadProveedor,
                "subtotal" to item.subtotal
            )
        }

        val primerProducto = pedido.first()
        val proveedorDoc = firestore.collection(FirestoreConstants.PROVEEDORES_COLLECTION)
            .document(primerProducto.idProveedor)
            .get()
            .await()
        val nombreProveedor = proveedorDoc.getString("nombre") ?: "Proveedor Desconocido"

        val pedidoData = mapOf(
            "idPedido" to pedidoDocRef.id,
            "idProveedor" to primerProducto.idProveedor,
            "nombreProveedor" to nombreProveedor,
            "fechaPedido" to primerProducto.fechaPedido,
            "totalPedido" to pedido.sumOf { it.subtotal },
            "productos" to productosData
        )
        batch.set(pedidoDocRef, pedidoData)

        pedido.forEach { item ->
            val productoDocRef = firestore.collection(FirestoreConstants.PRODUCTOS_COLLECTION).document(item.idProducto)
            batch.update(
                productoDocRef,
                "stock_actual", FieldValue.increment(item.cantidadAPedir.toDouble())
            )
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
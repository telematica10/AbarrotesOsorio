package com.ajo.abarrotesOsorio.data.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

/**
 * Clase de datos que representa un producto en la base de datos de Firestore.
 *
 * @param id Identificador único del documento en Firestore.
 * Este campo se mapeará automáticamente al ID del documento, que en este
 * caso es el mismo valor que el codigo_de_barras_sku.
 */
@Parcelize
data class Producto(
    // Anotación para que Firestore asigne el ID del documento a esta propiedad.
    @DocumentId
    val id: String = "",
    val codigo_de_barras_sku: String = "",
    var nombre_producto: String = "",
    val nombre_producto_proveedor: String = "",
    var proveedor: String = "",
    val cantidad: Int = 0,
    val precio_por_unidad_proveedor: Double = 0.0,
    var categoria: String = "",
    var impuesto: Double = 0.0,
    var ganancia: Double = 0.0,
    val precio_proveedor: Double = 0.0,
    var precio_de_venta: Double = 0.0,
    var unidad_de_medida: String = "",
    var stock_actual: Int = 0,
    var stock_minimo: Int = 0,
    val fecha_de_ultima_compra: String = "",
    var fecha_de_caducidad: String = "",
    var proveedor_preferente: String = "",
    var notas_observaciones: String = "",
    val fecha_registro: String = "",
    var categoria_id: String = ""
) : Parcelable
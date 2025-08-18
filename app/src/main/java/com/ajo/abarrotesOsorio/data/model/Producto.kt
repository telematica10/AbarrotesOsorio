package com.ajo.abarrotesOsorio.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Producto(
    var id: String = "", // Este es el ID del documento en Firestore
    val codigo_de_barras_sku: String = "",
    var nombre_producto: String = "",
    val nombre_producto_proveedor: String = "",
    var proveedor: String = "",
    val cantidad: Int = 0,
    val precio_por_unidad_proveedor: Double = 0.0,
    val costo_por_unidad_compra: Double = 0.0,
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
    var ubicacion_en_tienda_almacen: String = "",
    var proveedor_preferente: String = "",
    var notas_observaciones: String = "",
    val fecha_registro: String = "",
    var categoria_id: String = ""
) : Parcelable

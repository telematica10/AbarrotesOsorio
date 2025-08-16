package com.ajo.abarrotesOsorio.data.model

data class Producto(
    var id: String = "", // ID de Firestore
    val codigo_de_barras_sku: String = "",
    val nombre_producto: String = "",
    val nombre_producto_proveedor: String = "",
    val precio: Double = 0.0,
    var stock_actual: Int = 0,
    val proveedor_preferente: String = ""
)

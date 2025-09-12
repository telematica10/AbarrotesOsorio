package com.ajo.abarrotesOsorio.data.model

data class PedidoItem(
    val idPedido: String,
    val idProducto: String,
    val nombreProductoProveedor: String,
    val nombreProducto: String,
    var cantidadAPedir: Int,
    val precioProveedor: Double,
    val precioPorUnidadProveedor: Double,
    var subtotal: Double,
    val total: Double,
    var recibido: Boolean = false,
    val idRuta: String? = null,
    val codigoCliente: String? = null,
    val nombreVendedor: String? = "",
    val idProveedor: String = "",
    val fechaPedido: String = ""
)

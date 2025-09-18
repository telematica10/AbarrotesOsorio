package com.ajo.abarrotesOsorio.data.model

data class VentaItem(
    val nombre: String,
    val precio: Double,
    var cantidad: Int,
    var subtotal: Double = precio * cantidad
)
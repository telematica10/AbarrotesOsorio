package com.ajo.abarrotesOsorio.data.model

import java.io.Serializable
import java.util.Date

data class Venta(
    val fechaVenta: Date,
    val productos: List<VentaItem>,
    val totalVenta: Double
) : Serializable

package com.ajo.abarrotesOsorio.data.model

data class Categoria(
    var id: String = "",
    var nombre: String = "",
    var imagen_url: String = "",
    var orden: Int = 0,
    var proveedor_id: String? = null
)

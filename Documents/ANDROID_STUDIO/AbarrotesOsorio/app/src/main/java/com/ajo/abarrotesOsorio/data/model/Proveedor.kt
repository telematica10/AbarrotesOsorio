package com.ajo.abarrotesOsorio.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Proveedor(
    @get:Exclude
    val id: String = "",
    val nombre: String = "",
    val imagen_url: String = "",
    val notas_adicionales: String = "",
    val nombreVendedor: String? = "",
    val telefonoVendedor: String? = null,
    val codigoCliente: String? = null,
    val idRuta: String? = null,
    val diaDeVisita: String? = null
) : Parcelable
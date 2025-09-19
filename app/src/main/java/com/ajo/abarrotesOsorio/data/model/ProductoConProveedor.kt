package com.ajo.abarrotesOsorio.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductoConProveedor(
    val producto: Producto,
    val nombreProveedor: String?
) : Parcelable

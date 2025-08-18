package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.CategoriaRepository
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.data.ProductoEditRepository
import kotlinx.coroutines.launch

class ProductoEditViewModel(private val repository: ProductoEditRepository) : ViewModel() {

    /**
     * Actualiza un producto completo en la base de datos.
     * @param producto El objeto Producto con los datos actualizados.
     */
    fun actualizarProducto(producto: Producto, esPropietario: Boolean) {
        viewModelScope.launch {
            repository.actualizarProductoConPermisos(producto,esPropietario)
        }
    }
}

class ProductoEditViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // 🔹 LÍNEA CORREGIDA: Se usa el singleton para obtener la instancia de Firestore.
            val repository = ProductoEditRepository(FirestoreHelper.firestoreInstance)
            return ProductoEditViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

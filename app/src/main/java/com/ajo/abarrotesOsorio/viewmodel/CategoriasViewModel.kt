package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.CategoriaRepository
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.model.Categoria
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * CategoriaViewModel: Gestiona la lógica de la UI y expone la lista de categorías.
 */
class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {

    // LiveData privado y mutable para la lista de categorías.
    private val _categoriasLiveData = MutableLiveData<List<Categoria>>()

    // LiveData público y solo para lectura que la UI puede observar.
    val categoriasLiveData: LiveData<List<Categoria>> = _categoriasLiveData

    init {
        // Al crear el ViewModel, se inicia la recolección del flujo de datos.
        iniciarObservacionCategorias()
    }

    /**
     * Inicia la recolección de categorías desde el repositorio y actualiza el LiveData.
     */
    fun iniciarObservacionCategorias() {
        viewModelScope.launch {
            repository.getTodasLasCategorias()
                .catch { e ->
                    // Manejo de errores: imprime la excepción en la consola.
                    println("Error al obtener categorías: ${e.message}")
                }
                .collect { categorias ->
                    // Actualiza el LiveData con la nueva lista de categorías.
                    _categoriasLiveData.value = categorias
                }
        }
    }
}

class CategoriaViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // 🔹 LÍNEA CORREGIDA: Se usa el singleton para obtener la instancia de Firestore.
            val repository = CategoriaRepository(FirestoreHelper.firestoreInstance)
            return CategoriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

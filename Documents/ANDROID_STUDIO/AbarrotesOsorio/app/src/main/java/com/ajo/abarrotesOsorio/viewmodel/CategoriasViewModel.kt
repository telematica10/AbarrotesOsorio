package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.repository.CategoriaRepository
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.model.Categoria
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {

    private val _categoriasLiveData = MutableLiveData<List<Categoria>>()

    val categoriasLiveData: LiveData<List<Categoria>> = _categoriasLiveData

    init {
        iniciarObservacionCategorias()
    }

    fun iniciarObservacionCategorias() {
        viewModelScope.launch {
            repository.getTodasLasCategorias()
                .catch { e ->
                    println("Error al obtener categorías: ${e.message}")
                }
                .collect { categorias ->
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

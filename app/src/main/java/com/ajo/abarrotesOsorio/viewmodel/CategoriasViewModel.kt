package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajo.abarrotesOsorio.data.CategoriasRepository
import com.ajo.abarrotesOsorio.data.model.Categoria

class CategoriasViewModel(private val repository: CategoriasRepository) : ViewModel() {

    private val _categorias = MutableLiveData<List<Categoria>>()
    val categorias: LiveData<List<Categoria>> get() = _categorias

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun cargarCategorias() {
        repository.obtenerCategorias(
            onResult = { _categorias.value = it },
            onError = { _error.value = it.message }
        )
    }
}



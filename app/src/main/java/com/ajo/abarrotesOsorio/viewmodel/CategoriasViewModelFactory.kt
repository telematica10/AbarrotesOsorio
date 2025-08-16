package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ajo.abarrotesOsorio.data.CategoriasRepository


class CategoriasViewModelFactory(private val repository: CategoriasRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoriasViewModel(repository) as T
    }
}
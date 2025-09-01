package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.repository.ProveedorRepository

class ProveedorViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProveedorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repository = ProveedorRepository(FirestoreHelper.firestoreInstance)
            return ProveedorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
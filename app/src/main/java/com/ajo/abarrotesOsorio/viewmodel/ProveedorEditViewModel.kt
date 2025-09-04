package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.data.repository.ProveedorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.lifecycle.map

class ProveedorEditViewModel(private val repository: ProveedorRepository) : ViewModel() {

    private val _proveedor = MutableLiveData<Proveedor?>(null)

    val nombre = MutableLiveData<String>()
    val nombreVendedor = MutableLiveData<String?>()
    val telefonoVendedor = MutableLiveData<String?>()
    val diaDeVisita = MutableLiveData<String?>()
    val codigoCliente = MutableLiveData<String?>()
    val idRuta = MutableLiveData<String?>()

    private val _eventSaveSuccess = MutableLiveData<Boolean>()
    val eventSaveSuccess: LiveData<Boolean> get() = _eventSaveSuccess

    private val _eventShowMessage = MutableLiveData<String?>()
    val eventShowMessage: LiveData<String?> get() = _eventShowMessage

    val isNewProveedor: LiveData<Boolean> = _proveedor.map { proveedor ->
        proveedor?.id.isNullOrBlank()
    }

    fun setProveedor(proveedor: Proveedor?) {
        _proveedor.value = proveedor
        if (proveedor != null) {
            nombre.value = proveedor.nombre
            nombreVendedor.value = proveedor.nombreVendedor
            telefonoVendedor.value = proveedor.telefonoVendedor
            diaDeVisita.value = proveedor.diaDeVisita
            codigoCliente.value = proveedor.codigoCliente
            idRuta.value = proveedor.idRuta
        } else {
            nombre.value = ""
            nombreVendedor.value = ""
            telefonoVendedor.value = ""
            diaDeVisita.value = ""
            codigoCliente.value = ""
            idRuta.value = ""
        }
    }

    fun saveProveedor() {
        val currentNombre = nombre.value
        if (currentNombre.isNullOrBlank()) {
            _eventShowMessage.value = "El nombre del proveedor no puede estar vacío."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newProveedor = Proveedor(
                id = _proveedor.value?.id ?: UUID.randomUUID().toString(),
                nombre = currentNombre,
                nombreVendedor = nombreVendedor.value,
                telefonoVendedor = telefonoVendedor.value,
                diaDeVisita = diaDeVisita.value,
                codigoCliente = codigoCliente.value,
                idRuta = idRuta.value
            )

            try {
                if (isNewProveedor.value == true) {
                    repository.addProveedor(newProveedor)
                } else {
                    repository.updateProveedor(newProveedor)
                }
                _eventSaveSuccess.postValue(true)
            } catch (e: Exception) {
                _eventShowMessage.postValue("Error al guardar el proveedor: ${e.message}")
                _eventSaveSuccess.postValue(false)
            }
        }
    }

    fun onMessageShown() {
        _eventShowMessage.value = null
    }
}
package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.data.repository.ProveedorRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProveedorViewModel(private val repository: ProveedorRepository) : ViewModel() {

    private val _allProveedores = MutableStateFlow<List<Proveedor>>(emptyList())

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    @OptIn(FlowPreview::class)
    val

            proveedores: StateFlow<List<Proveedor>> =
        combine(_allProveedores, _searchText.debounce(500)) { allProveedores, searchText ->
            if (searchText.isBlank()) {
                allProveedores.sortedBy { it.nombre }
            } else {
                allProveedores.filter { proveedor ->
                    proveedor.nombre.contains(searchText, ignoreCase = true) ||
                            proveedor.nombreVendedor?.contains(
                                searchText,
                                ignoreCase = true
                            ) == true ||
                            proveedor.telefonoVendedor?.contains(
                                searchText,
                                ignoreCase = true
                            ) == true ||
                            proveedor.codigoCliente?.contains(
                                searchText,
                                ignoreCase = true
                            ) == true ||
                            proveedor.idRuta?.contains(searchText, ignoreCase = true) == true ||
                            proveedor.diaDeVisita?.contains(searchText, ignoreCase = true) == true
                }.sortedBy { it.nombre }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<UiState>(UiState.List)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val isListState: StateFlow<Boolean> = _uiState.map { it is UiState.List }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = _uiState.value is UiState.List
        )

    val isFormState: StateFlow<Boolean> = _uiState.map { it is UiState.Add || it is UiState.Edit }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = _uiState.value is UiState.Add || _uiState.value is UiState.Edit
        )

    val nombre = MutableStateFlow("")
    val nombreVendedor = MutableStateFlow("")
    val telefonoVendedor = MutableStateFlow("")
    val diaDeVisita = MutableStateFlow("")
    val codigoCliente = MutableStateFlow("")
    val idRuta = MutableStateFlow("")
    val currentProveedorId = MutableStateFlow<String?>(null)

    init {
        iniciarObservacionProveedores()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    private fun iniciarObservacionProveedores() {
        viewModelScope.launch {
            try {
                repository.getProveedores().collect { proveedores ->
                    _allProveedores.value = proveedores
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al cargar los proveedores: ${e.message}")
            }
        }
    }

    fun startAddingProveedor() {
        clearForm()
        currentProveedorId.value = null
        _uiState.value = UiState.Add
    }

    fun startEditingProveedor(proveedor: Proveedor) {
        nombre.value = proveedor.nombre
        nombreVendedor.value = proveedor.nombreVendedor ?: ""
        telefonoVendedor.value = proveedor.telefonoVendedor ?: ""
        diaDeVisita.value = proveedor.diaDeVisita ?: ""
        codigoCliente.value = proveedor.codigoCliente ?: ""
        idRuta.value = proveedor.idRuta ?: ""
        currentProveedorId.value = proveedor.id
        _uiState.value = UiState.Edit
    }

    fun saveProveedor() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val proveedorToSave = Proveedor(
                    id = currentProveedorId.value ?: "",
                    nombre = nombre.value,
                    nombreVendedor = nombreVendedor.value,
                    telefonoVendedor = telefonoVendedor.value,
                    diaDeVisita = diaDeVisita.value,
                    codigoCliente = codigoCliente.value,
                    idRuta = idRuta.value
                )

                if (proveedorToSave.id?.isNotEmpty() == true) {
                    repository.updateProveedor(proveedorToSave)
                } else {
                    repository.addProveedor(proveedorToSave)
                }
                _uiState.value = UiState.Success("Proveedor guardado correctamente.")
                goBackToList()
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al guardar el proveedor: ${e.message}")
            }
        }
    }

    fun deleteProveedor(proveedorId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.deleteProveedor(proveedorId)
                _uiState.value = UiState.Success("Proveedor eliminado correctamente.")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al eliminar el proveedor: ${e.message}")
            }
        }
    }

    fun goBackToList() {
        clearForm()
        _uiState.value = UiState.List
    }

    private fun clearForm() {
        nombre.value = ""
        nombreVendedor.value = ""
        telefonoVendedor.value = ""
        diaDeVisita.value = ""
        codigoCliente.value = ""
        idRuta.value = ""
    }
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object List : UiState()
    object Add : UiState()
    object Edit : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}
package com.ajo.abarrotesOsorio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ajo.abarrotesOsorio.data.FirestoreHelper
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.data.repository.ProveedorRepository
import com.ajo.abarrotesOsorio.data.repository.ProveedorRepositoryI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class InversionReporteViewModel(
    private val proveedorRepository: ProveedorRepositoryI
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReporteUiState>(ReporteUiState.Loading)
    val uiState: StateFlow<ReporteUiState> = _uiState.asStateFlow()

    fun cargarReporte(proveedorId: String) {
        viewModelScope.launch {
            proveedorRepository.getProductosPorProveedor(proveedorId)
                .zip(proveedorRepository.getProveedores().map { it.find { p -> p.id == proveedorId } }) { productos, proveedor ->
                    if (proveedor == null) {
                        throw Exception("Proveedor no encontrado")
                    }
                    val productosConStock = productos.filter { it.stock_actual > 0 }
                    val totalInversion = productosConStock.sumOf { it.precio_proveedor * it.stock_actual }
                    val totalProductos = productosConStock.sumOf {  it.stock_actual}
                    ReporteUiState.Success(
                        productosConStock,
                        totalInversion,
                        totalProductos,
                        proveedor.nombre ?: ""
                    )
                }
                .onStart { _uiState.value = ReporteUiState.Loading }
                .catch { e ->
                    _uiState.value = ReporteUiState.Error(e.localizedMessage ?: "Error desconocido")
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    sealed class ReporteUiState {
        data object Loading : ReporteUiState()
        data class Success(
            val productos: List<Producto>,
            val totalInversion: Double,
            val totalProductos: Int,
            val proveedorNombre: String
        ) : ReporteUiState()
        data class Error(val message: String) : ReporteUiState()
    }
}

class InversionReporteViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InversionReporteViewModel::class.java)) {
            val repository = ProveedorRepository(FirestoreHelper.firestoreInstance)
            return InversionReporteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.databinding.FragmentInversionReporteBinding
import com.ajo.abarrotesOsorio.utils.Utilities.Companion.getNumberFormat
import com.ajo.abarrotesOsorio.view.ui.ProductoReporteAdapter
import com.ajo.abarrotesOsorio.viewmodel.InversionReporteViewModel
import com.ajo.abarrotesOsorio.viewmodel.InversionReporteViewModelFactory
import kotlinx.coroutines.launch

class InversionReporteFragment : Fragment() {

    private lateinit var binding: FragmentInversionReporteBinding
    private val viewModel: InversionReporteViewModel by viewModels {
        InversionReporteViewModelFactory()
    }
    private lateinit var productoReporteAdapter: ProductoReporteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInversionReporteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productoReporteAdapter = ProductoReporteAdapter()
        binding.rvProductos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productoReporteAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is InversionReporteViewModel.ReporteUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.reporteContent.visibility = View.GONE
                        binding.tvErrorMessage.visibility = View.GONE
                    }

                    is InversionReporteViewModel.ReporteUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.reporteContent.visibility = View.VISIBLE
                        binding.tvErrorMessage.visibility = View.GONE

                        binding.tvProveedorName.text = uiState.proveedorNombre
                        binding.tvTotalInversion.text = getNumberFormat(uiState.totalInversion)
                        binding.tvTotalProductos.text = uiState.totalProductos.toString()
                        productoReporteAdapter.submitList(uiState.productos)
                    }

                    is InversionReporteViewModel.ReporteUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.reporteContent.visibility = View.GONE
                        binding.tvErrorMessage.visibility = View.VISIBLE
                        binding.tvErrorMessage.text = uiState.message
                    }
                }
            }
        }

        val proveedorId = arguments?.getString("proveedorId")
        if (proveedorId != null) {
            viewModel.cargarReporte(proveedorId)
        } else {
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.tvErrorMessage.text = "Error: ID del proveedor no encontrado."
            binding.progressBar.visibility = View.GONE
            binding.reporteContent.visibility = View.GONE
        }
    }
}
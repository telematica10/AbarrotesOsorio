package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.databinding.FragmentInventarioBinding
import com.ajo.abarrotesOsorio.view.ui.InventarioAdapter
import com.ajo.abarrotesOsorio.view.ui.UpdateProductUiState
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModel
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InventarioFragment : Fragment() {

    private var _binding: FragmentInventarioBinding? = null
    private val binding get() = _binding!!

    private val args: InventarioFragmentArgs by navArgs()
    private val viewModel: InventarioViewModel by viewModels {
        InventarioViewModelFactory()
    }

    private lateinit var adapter: InventarioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriaId = args.categoriaId
        Log.d("InventarioFragment", "Argumento categoriaId recibido: $categoriaId")

        adapter = InventarioAdapter(
            onStockChange = { productId, newStock ->
                viewModel.actualizarStock(productId, newStock)
            },
            onNavigateToEdit = { producto ->
                val action = InventarioFragmentDirections.actionInventarioFragmentToProductoEditFragment(producto)
                findNavController().navigate(action)
            }
        )

        binding.scanButton.setOnClickListener {
            findNavController().navigate(InventarioFragmentDirections.actionInventarioFragmentToScanFragment())
        }

        setFragmentResultListener("codigo_barras_key") { key, bundle ->
            val barcode = bundle.getString("barcode_data")
            if (!barcode.isNullOrEmpty()) {
                viewModel.buscarProductoPorCodigo(barcode)
            } else {
                Snackbar.make(binding.root, "Escaneo cancelado o fallido.", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.navegarARegistroProducto.observe(viewLifecycleOwner) { barcode ->
            barcode?.let {
                val action = InventarioFragmentDirections.actionInventarioFragmentToRegistroProductoFragment(barcode)
                findNavController().navigate(action)
                viewModel.onNavegacionARegistroCompleta()
            }
        }

        binding.rvInventario.layoutManager = LinearLayoutManager(context)
        binding.rvInventario.adapter = adapter

        viewModel.iniciarObservacionInventario(categoriaId)
        viewModel.productosLiveData.observe(viewLifecycleOwner) { productos ->
            Log.d("InventarioFragment", "Lista de productos actualizada. Total: ${productos.size}")
            adapter.submitList(productos)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateProductUiState.collectLatest { uiState ->
                when (uiState) {
                    is UpdateProductUiState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }

                    is UpdateProductUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is UpdateProductUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetUiState()
                    }

                    is UpdateProductUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), uiState.errorMessage, Toast.LENGTH_SHORT)
                            .show()
                        viewModel.resetUiState()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
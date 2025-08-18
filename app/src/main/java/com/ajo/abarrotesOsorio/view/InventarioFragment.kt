package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragmento que muestra el inventario de productos.
 * Observa el ViewModel para actualizar la lista de productos y el estado de la UI.
 */
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

        // Inicializamos el adaptador con los callbacks de stock y navegación.
        adapter = InventarioAdapter(
            onStockChange = { productId, newStock ->
                // Este callback ahora es más robusto y se llama solo al perder el foco
                viewModel.actualizarStock(productId, newStock)
            },
            onNavigateToEdit = { producto ->
                val action = InventarioFragmentDirections.actionInventarioFragmentToProductoEditFragment(producto)
                findNavController().navigate(action)
            }
        )

        binding.rvInventario.layoutManager = LinearLayoutManager(context)
        binding.rvInventario.adapter = adapter

        // 🔹 Observamos la lista de productos
        viewModel.iniciarObservacionInventario(categoriaId)
        viewModel.productosLiveData.observe(viewLifecycleOwner) { productos ->
            Log.d("InventarioFragment", "Lista de productos actualizada. Total: ${productos.size}")
            adapter.submitList(productos)
        }

        // 🔹 OBSERVACIÓN DEL ESTADO DE LA UI
        // Usamos collectLatest para cancelar colecciones anteriores si hay una nueva.
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateProductUiState.collectLatest { uiState ->
                when (uiState) {
                    is UpdateProductUiState.Idle -> {
                        // Ocultar la barra de progreso
                        binding.progressBar.visibility = View.GONE
                    }

                    is UpdateProductUiState.Loading -> {
                        // Mostrar la barra de progreso
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is UpdateProductUiState.Success -> {
                        // Ocultar la barra de progreso y mostrar un mensaje de éxito
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), uiState.message, Toast.LENGTH_SHORT).show()
                        // Reseteamos el estado para evitar que el mensaje se muestre de nuevo
                        viewModel.resetUiState()
                    }

                    is UpdateProductUiState.Error -> {
                        // Ocultar la barra de progreso y mostrar un mensaje de error
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), uiState.errorMessage, Toast.LENGTH_SHORT)
                            .show()
                        // Reseteamos el estado para evitar que el mensaje se muestre de nuevo
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
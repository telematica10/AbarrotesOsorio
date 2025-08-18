package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.databinding.FragmentInventarioBinding
import com.ajo.abarrotesOsorio.view.ui.InventarioAdapter
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModel
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModelFactory

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

        binding.rvInventario.layoutManager = LinearLayoutManager(context)
        binding.rvInventario.adapter = adapter

        viewModel.iniciarObservacionInventario(categoriaId)

        viewModel.productosLiveData.observe(viewLifecycleOwner) { productos ->
            Log.d("InventarioFragment", "Lista de productos actualizada. Total: ${productos.size}")
            adapter.submitList(productos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
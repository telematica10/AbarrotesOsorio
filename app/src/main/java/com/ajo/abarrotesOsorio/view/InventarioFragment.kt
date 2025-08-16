package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.data.InventarioRepository
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.FragmentInventarioBinding
import com.ajo.abarrotesOsorio.view.ui.InventarioAdapter
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModel
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class InventarioFragment : Fragment() {

    private var _binding: FragmentInventarioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventarioViewModel by viewModels {
        InventarioViewModelFactory(InventarioRepository(FirebaseFirestore.getInstance()))
    }

    private lateinit var adapter: InventarioAdapter
    private var listaCompleta: List<Producto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventarioBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriaId = arguments?.getString("categoriaId")

        // Escuchar cambios, filtrando por categoría si aplica
        viewModel.iniciarEscuchaInventarioPorCambiosFiltrado(categoriaId) { tipo, producto ->
            when (tipo) {
                "ADDED" -> {
                    listaCompleta = (listaCompleta + producto)
                        .sortedBy { it.nombre_producto.lowercase() }
                    val nuevaPos = listaCompleta.indexOfFirst { it.id == producto.id }
                    adapter.agregarProductoEnPosicion(producto, nuevaPos)
                }

                "MODIFIED" -> {
                    listaCompleta = listaCompleta
                        .map { if (it.id == producto.id) producto else it }
                        .sortedBy { it.nombre_producto.lowercase() }
                    val nuevaPos = listaCompleta.indexOfFirst { it.id == producto.id }
                    adapter.actualizarProductoEnPosicion(producto, adapterPosicion(producto.id), nuevaPos)
                }

                "REMOVED" -> {
                    val index = adapterPosicion(producto.id)
                    if (index != -1) {
                        listaCompleta = listaCompleta.filter { it.id != producto.id }
                        adapter.eliminarProductoEnPosicion(index)
                    }
                }
            }
        }
    }

    private fun adapterPosicion(id: String): Int {
        return listaCompleta.indexOfFirst { it.id == id }
    }


    private fun setupRecyclerView() {
        adapter = InventarioAdapter(mutableListOf()) { productoId, nuevoStock ->
            viewModel.actualizarStock(productoId, nuevoStock)

            // Confirmación visual
            Snackbar.make(binding.root, "Stock actualizado a $nuevoStock", Snackbar.LENGTH_SHORT)
                .setAction("Deshacer") {
                    // Acción opcional: restaurar stock anterior
                    viewModel.actualizarStock(productoId, nuevoStock - 1)
                }
                .show()
        }

        binding.rvInventario.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventario.adapter = adapter
    }


    private fun setupSearchView() {
        binding.searchViewInventario.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val texto = newText?.lowercase()?.trim() ?: ""
                val filtrados = if (texto.isEmpty()) {
                    listaCompleta
                } else {
                    listaCompleta.filter {
                        it.nombre_producto.lowercase().contains(texto)
                    }
                }.sortedBy { it.nombre_producto.lowercase() } // Siempre ordenado

                adapter.actualizarLista(filtrados)
                return true
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
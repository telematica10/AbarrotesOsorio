package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ajo.abarrotesOsorio.data.CategoriasRepository
import com.ajo.abarrotesOsorio.databinding.FragmentCategoriasBinding
import com.ajo.abarrotesOsorio.view.ui.CategoriasAdapter
import com.ajo.abarrotesOsorio.viewmodel.CategoriasViewModel
import com.ajo.abarrotesOsorio.viewmodel.CategoriasViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class CategoriasFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CategoriasAdapter

    private val viewModel: CategoriasViewModel by viewModels {
        CategoriasViewModelFactory(CategoriasRepository(FirebaseFirestore.getInstance()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.categorias.observe(viewLifecycleOwner) {
            adapter.actualizarLista(it)
            binding.rvCategorias.scheduleLayoutAnimation() // animación de entrada
        }

        viewModel.error.observe(viewLifecycleOwner) {
            // Mostrar Snackbar con error si quieres
        }

        viewModel.cargarCategorias()
    }

    private fun setupRecyclerView() {
        adapter = CategoriasAdapter(emptyList()) { categoria ->
            val fragment = InventarioFragment()
            fragment.arguments = Bundle().apply {
                putString("categoriaId", categoria.id)
            }
            parentFragmentManager.beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvCategorias.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategorias.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

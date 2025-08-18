package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ajo.abarrotesOsorio.data.CategoriaRepository
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.ajo.abarrotesOsorio.databinding.FragmentCategoriasBinding
import com.ajo.abarrotesOsorio.view.ui.CategoriaAdapter
import com.ajo.abarrotesOsorio.viewmodel.CategoriaViewModel
import com.ajo.abarrotesOsorio.viewmodel.CategoriaViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore

class CategoriaFragment : Fragment() {

    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    // 🔹 LÍNEA CORREGIDA: Se usa el ViewModelFactory para crear el ViewModel.
    private val viewModel: CategoriaViewModel by viewModels {
        CategoriaViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CategoriaAdapter { categoria ->
            navigateToProductos(categoria)
        }

        binding.rvCategorias.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategorias.adapter = adapter

        viewModel.categoriasLiveData.observe(viewLifecycleOwner) { categorias ->
            adapter.submitList(categorias)
        }
    }

    private fun navigateToProductos(categoria: Categoria) {
        val action = CategoriaFragmentDirections.actionCategoriasFragmentToProductoFragment(categoria.id)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

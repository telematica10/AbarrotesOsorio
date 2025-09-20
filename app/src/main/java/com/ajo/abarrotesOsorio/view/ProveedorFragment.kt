package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.MainActivity
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.databinding.FragmentProveedorBinding
import com.ajo.abarrotesOsorio.view.ui.ProveedorAdapter
import com.ajo.abarrotesOsorio.view.ui.SearchListener
import com.ajo.abarrotesOsorio.viewmodel.ProveedorViewModel
import com.ajo.abarrotesOsorio.viewmodel.ProveedorViewModelFactory
import com.ajo.abarrotesOsorio.viewmodel.UiState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProveedorFragment : Fragment(), SearchListener {

    private lateinit var binding: FragmentProveedorBinding
    private val viewModel: ProveedorViewModel by viewModels {
        ProveedorViewModelFactory()
    }
    private lateinit var proveedorAdapter: ProveedorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_proveedor, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeProveedores()
        observeUiStateForMessages()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onSearchTextChange("")
        (requireActivity() as? MainActivity)?.invalidateOptionsMenu()
    }

    override fun onSearchQuery(query: String) {
        viewModel.onSearchTextChange(query)
    }

    private fun setupRecyclerView() {
        proveedorAdapter = ProveedorAdapter(
            onItemClicked = { proveedor ->
                navigateToInventory(proveedor)
            },
            onEditClicked = { proveedor ->
                val action = ProveedorFragmentDirections.actionProveedoresFragmentToProveedorEditFragment(proveedor)
                findNavController().navigate(action)
            },
            onDeleteClicked = { proveedor ->
                viewModel.deleteProveedor(proveedor.id)
            },
            onLongItemClicked = { proveedor ->
                val action = ProveedorFragmentDirections.actionProveedoresFragmentToInversionReporteFragment(proveedorId = proveedor.id)
                findNavController().navigate(action)
            }
        )
        binding.proveedoresRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = proveedorAdapter
        }
    }

    private fun navigateToInventory(proveedor: Proveedor) {
        val action = ProveedorFragmentDirections.actionProveedoresFragmentToInventarioFragment(categoriaId = null, proveedorId = proveedor.id)
        findNavController().navigate(action)
    }

    private fun setupListeners() {
        binding.fabAddProveedor.setOnClickListener {
            val action = ProveedorFragmentDirections.actionProveedoresFragmentToProveedorEditFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun observeProveedores() {
        viewModel.proveedores.onEach { proveedoresList ->
            proveedorAdapter.submitList(proveedoresList)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeUiStateForMessages() {
        viewModel.uiState.onEach { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    Snackbar.make(binding.root, uiState.message, Snackbar.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    Snackbar.make(binding.root, uiState.message, Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
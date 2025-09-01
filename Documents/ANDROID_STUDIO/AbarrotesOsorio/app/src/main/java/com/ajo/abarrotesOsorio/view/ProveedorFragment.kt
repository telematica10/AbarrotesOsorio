package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.databinding.FragmentProveedorBinding
import com.ajo.abarrotesOsorio.view.ui.ProveedorAdapter
import com.ajo.abarrotesOsorio.viewmodel.ProveedorViewModel
import com.ajo.abarrotesOsorio.viewmodel.ProveedorViewModelFactory
import com.ajo.abarrotesOsorio.viewmodel.UiState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProveedorFragment : Fragment() {

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
        setupListeners(Proveedor())
        observeProveedores()
        observeUiStateForMessages()
        setupSearchView()
    }

    private fun setupSearchView() {
        val searchPlate = binding.searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate?.background = null

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val isSearching = !newText.isNullOrEmpty()
                updateUIForSearchState(isSearching)
                viewModel.onSearchTextChange(newText ?: "")
                return true
            }
        })
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
            }
        )
        binding.proveedoresRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = proveedorAdapter
        }
    }

    private fun navigateToInventory(proveedor: Proveedor) {
            val action = ProveedorFragmentDirections.actionProveedoresFragmentToInventarioFragment(proveedor.id)
            findNavController().navigate(action)
    }

    private fun setupListeners(proveedor: Proveedor) {
        binding.fabAddProveedor.setOnClickListener {
            val action = ProveedorFragmentDirections.actionProveedoresFragmentToProveedorEditFragment(proveedor)
            findNavController().navigate(action)
        }
    }

    private fun updateUIForSearchState(isSearching: Boolean) {
        val bottomNavView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if (isSearching) {
            bottomNavView?.visibility = View.GONE
            binding.fabAddProveedor.visibility = View.GONE
        } else {
            bottomNavView?.visibility = View.VISIBLE
            binding.fabAddProveedor.visibility = View.VISIBLE
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
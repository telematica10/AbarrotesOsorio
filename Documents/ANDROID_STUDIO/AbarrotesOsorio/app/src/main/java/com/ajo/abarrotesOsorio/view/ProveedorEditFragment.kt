package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.databinding.FragmentProveedorEditBinding
import com.ajo.abarrotesOsorio.viewmodel.ProveedorEditViewModel
import com.ajo.abarrotesOsorio.viewmodel.ProveedorEditViewModelFactory
import com.google.android.material.snackbar.Snackbar

class ProveedorEditFragment : Fragment() {

    private val viewModel: ProveedorEditViewModel by viewModels {
        ProveedorEditViewModelFactory()
    }
    private lateinit var binding: FragmentProveedorEditBinding
    private val args: ProveedorEditFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_proveedor_edit, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.proveedor?.let {
            viewModel.setProveedor(it)
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.saveProveedor()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.eventSaveSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Snackbar.make(binding.root, "Proveedor guardado exitosamente.", Snackbar.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        viewModel.eventShowMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.onMessageShown()
            }
        }
    }
}
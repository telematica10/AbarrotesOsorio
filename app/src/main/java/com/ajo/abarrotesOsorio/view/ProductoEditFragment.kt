package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.FragmentProductoEditBinding
import com.ajo.abarrotesOsorio.viewmodel.ProductoEditViewModel
import com.ajo.abarrotesOsorio.viewmodel.ProductoEditViewModelFactory
import com.ajo.abarrotesOsorio.viewmodel.SaveState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProductoEditFragment : Fragment() {

    private val esPropietario = true

    private var _binding: FragmentProductoEditBinding? = null
    private val binding get() = _binding!!

    private val args: ProductoEditFragmentArgs by navArgs()

    private val viewModel: ProductoEditViewModel by viewModels {
        ProductoEditViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductoEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val producto = args.producto
        Log.d("ProductoEditFragment", "Producto recibido para editar: ${producto.id_proveedor}")

        populateUI(producto)

        if (!esPropietario) {
            binding.tilPrecioVenta.visibility = View.GONE
            binding.tilStockMinimo.visibility = View.GONE
        }

        binding.btnGuardar.setOnClickListener {
            saveChanges(producto)
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }

        setFragmentResultListener("codigo_barras_key") { key, bundle ->
            val barcode = bundle.getString("barcode_data")
            barcode?.let {
                binding.etCodigoBarras.setText(it)
                Snackbar.make(binding.root, "Código de barras asignado", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        binding.tilCodigoBarras.setEndIconOnClickListener {
            val action = ProductoEditFragmentDirections.actionProductoEditFragmentToScanFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saveState.collect { state ->
                    when (state) {
                        is SaveState.Loading -> {
                            binding.btnGuardar.isEnabled = false
                        }

                        is SaveState.Success -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                            viewModel.resetSaveState()
                        }

                        is SaveState.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            binding.btnGuardar.isEnabled = true
                            viewModel.resetSaveState()
                        }

                        is SaveState.Idle -> {
                            binding.btnGuardar.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun populateUI(producto: Producto) {
        binding.apply {
            etCodigoBarras.setText(producto.codigo_de_barras_sku)
            etNombreProducto.setText(producto.nombre_producto)
            etNombreProductoP.setText(producto.nombre_producto_proveedor)
            etPrecioProveedor.setText(producto.precio_proveedor.toString())
            etCantidad.setText(producto.cantidad.toString())
            etStockActual.setText(producto.stock_actual.toString())
            etFechaCaducidad.setText(producto.fecha_de_caducidad)
            etNotas.setText(producto.notas_observaciones)

            if (esPropietario) {
                etPrecioVenta.setText(producto.precio_de_venta.toString())
                etStockMinimo.setText(producto.stock_minimo.toString())
            }
        }
    }

    private fun saveChanges(productoOriginal: Producto) {
        val barcode = binding.etCodigoBarras.text.toString().trim()
        val productName = binding.etNombreProducto.text.toString().trim()
        val productNameSupplier = binding.etNombreProductoP.text.toString().trim()
        val price = binding.etPrecioVenta.text.toString().trim().toDoubleOrNull() ?: 0.0
        val pricePerUnitSupplier = binding.etPrecioProveedor.text.toString().toDoubleOrNull() ?: 0.0
        val quantity = binding.etCantidad.text.toString().toIntOrNull() ?: 0
        val stock = binding.etStockActual.text.toString().toIntOrNull() ?: 0
        val minStock = binding.etStockMinimo.text.toString().toIntOrNull() ?: 0
        val expirationDate = binding.etFechaCaducidad.text.toString().trim()
        val notes = binding.etNotas.text.toString().trim()

        val productoActualizado = productoOriginal.copy(
            codigo_de_barras_sku = barcode,
            nombre_producto = productName,
            nombre_producto_proveedor = productNameSupplier,
            precio_proveedor = pricePerUnitSupplier,
            precio_de_venta = price,
            cantidad = quantity,
            stock_actual = stock,
            stock_minimo = minStock,
            fecha_de_caducidad = expirationDate,
            notas_observaciones = notes
        )

        // if (esPropietario)

        viewModel.actualizarProducto(productoActualizado, esPropietario)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
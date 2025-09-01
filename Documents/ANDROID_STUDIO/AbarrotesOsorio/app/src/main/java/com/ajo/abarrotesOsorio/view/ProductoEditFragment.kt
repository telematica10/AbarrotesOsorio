package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.FragmentProductoEditBinding
import com.ajo.abarrotesOsorio.viewmodel.InventarioViewModelFactory
import com.ajo.abarrotesOsorio.viewmodel.ProductoEditViewModel
import com.ajo.abarrotesOsorio.viewmodel.ProductoEditViewModelFactory
import com.google.android.material.snackbar.Snackbar

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
        Log.d("ProductoEditFragment", "Producto recibido para editar: ${producto.nombre_producto}")

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
    }

    private fun populateUI(producto: Producto) {
        binding.apply {
            etNombreProducto.setText(producto.nombre_producto)
            etNombreProductoP.setText(producto.nombre_producto_proveedor)
            etCantidad.setText(producto.cantidad.toString())
            etProveedorPreferente.setText(producto.proveedor_preferente)
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
        val nombreProducto = binding.etNombreProducto.text.toString().trim()
        val proveedorPreferente = binding.etProveedorPreferente.text.toString().trim()
        val stockActual = binding.etStockActual.text.toString().toIntOrNull() ?: 0
        val fechaCaducidad = binding.etFechaCaducidad.text.toString().trim()
        val notas = binding.etNotas.text.toString().trim()

        val productoActualizado = productoOriginal.copy(
            nombre_producto = nombreProducto,
            proveedor_preferente = proveedorPreferente,
            stock_actual = stockActual,
            fecha_de_caducidad = fechaCaducidad,
            notas_observaciones = notas
        )

        if (esPropietario) {
            val precioVenta = binding.etPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
            val stockMinimo = binding.etStockMinimo.text.toString().toIntOrNull() ?: 0

            productoActualizado.precio_de_venta = precioVenta
            productoActualizado.stock_minimo = stockMinimo
        }

        viewModel.actualizarProducto(productoActualizado, esPropietario)

        Snackbar.make(binding.root, "Producto actualizado", Snackbar.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
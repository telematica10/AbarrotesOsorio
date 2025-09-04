package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.databinding.FragmentRegistroProductoBinding
import com.ajo.abarrotesOsorio.utils.Utilities
import com.ajo.abarrotesOsorio.view.ui.RegistroProductoUiState
import com.ajo.abarrotesOsorio.viewmodel.CategoriaViewModel
import com.ajo.abarrotesOsorio.viewmodel.CategoriaViewModelFactory
import com.ajo.abarrotesOsorio.viewmodel.RegistroProductoViewModel
import com.ajo.abarrotesOsorio.viewmodel.RegistroProductoViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Date

class RegistroProductoFragment : Fragment() {

    private var _binding: FragmentRegistroProductoBinding? = null
    private val binding get() = _binding!!
    private val args: RegistroProductoFragmentArgs by navArgs()
    private val viewModel: RegistroProductoViewModel by viewModels {
        RegistroProductoViewModelFactory()
    }
    private val categoriaViewModel: CategoriaViewModel by viewModels {
        CategoriaViewModelFactory()
    }

    private var selectedCategoryId = ""
    private var selectedProveedorId = ""
    private var profit = 0.00
    private var pricePerUnitSupplier  = 0.00

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextCodigoBarras.setText(args.barcode)
        binding.tvFechaRegistro.text = Utilities.getFormattedDateRegister(Date())
        val calculationTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateUnitSupplierPrice()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        val profitTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateProfit()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.editTextPrecioProveedor.addTextChangedListener(calculationTextWatcher)
        binding.editTextCantidad.addTextChangedListener(calculationTextWatcher)
        binding.editTextPrecioVenta.addTextChangedListener(profitTextWatcher)

        categoriaViewModel.categoriasLiveData.observe(viewLifecycleOwner) { categorias ->
            val nombresCategorias = categorias.map { it.nombre }
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_item,
                nombresCategorias
            )
            binding.autoCompleteTextViewCategoria.setAdapter(adapter)
            binding.autoCompleteTextViewCategoria.setOnItemClickListener { parent, _, position, _ ->
                val categoriaSeleccionada = categorias[position]
                selectedCategoryId = categoriaSeleccionada.id
                binding.tvCategoriaID.text = "Categoria ID:"+categoriaSeleccionada.id
            }
        }

        val unitMeasurement = resources.getStringArray(R.array.unitMeasurement_array)
        val adapterM = ArrayAdapter(requireContext(), R.layout.list_location_product, unitMeasurement)
        binding.autoCompleteTextViewUnitMeasurement.setAdapter(adapterM)

        binding.saveButton.setOnClickListener {
            if (validateFields()) {
                val barcode = binding.editTextCodigoBarras.text.toString()
                val productName = binding.editTextNombreProducto.text.toString()
                val productNameSupplier = binding.editTextNombreProductoP.text.toString()
                val providerPrice =
                    binding.editTextPrecioProveedor.text.toString().toDoubleOrNull() ?: 0.0
                val quantity = binding.editTextCantidad.text.toString().toIntOrNull() ?: 0
                val pricePerUnitSupplier = pricePerUnitSupplier
                val price = binding.editTextPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
                val profit = profit
                val tax = binding.editTextImpuesto.text.toString().toDoubleOrNull() ?: 0.0
                val expirationDate = binding.tvFechaCaducidad.text.toString()
                val unitMeasurement = binding.autoCompleteTextViewUnitMeasurement.text.toString()
                val stock = binding.editTextStock.text.toString().toIntOrNull() ?: 1
                val minStock = binding.editTextStockMinimo.text.toString().toIntOrNull() ?: 1
                val idCategory = selectedCategoryId
                val category = binding.autoCompleteTextViewCategoria.text.toString()
                val registerDate = binding.tvFechaRegistro.text.toString()
                val note = binding.editTextNotas.text.toString()

                val nuevoProducto = Producto(
                    codigo_de_barras_sku = barcode,
                    nombre_producto = productName,
                    nombre_producto_proveedor = productNameSupplier,
                    precio_proveedor = providerPrice,
                    id_proveedor = selectedProveedorId,
                    precio_por_unidad_proveedor = pricePerUnitSupplier,
                    precio_de_venta = price,
                    cantidad = quantity,
                    ganancia = profit,
                    impuesto = tax,
                    fecha_de_caducidad = expirationDate,
                    unidad_de_medida = unitMeasurement,
                    stock_actual = stock,
                    stock_minimo = minStock,
                    categoria_id = idCategory,
                    categoria = category,
                    fecha_registro = registerDate,
                    notas_observaciones = note
                )
                viewModel.guardarProducto(nuevoProducto)
            }
        }
        viewModel.guardarProductoUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegistroProductoUiState.Loading -> {
                    binding.saveButton.isEnabled = false
                }

                is RegistroProductoUiState.Success -> {
                    Snackbar.make(
                        binding.root,
                        "Producto guardado con éxito.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.saveButton.isEnabled = true
                    findNavController().popBackStack()
                    viewModel.resetUiState()
                }

                is RegistroProductoUiState.Error -> {
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG)
                        .show()
                    binding.saveButton.isEnabled = true
                    viewModel.resetUiState()
                }

                is RegistroProductoUiState.Idle -> {
                    binding.saveButton.isEnabled = true
                }
            }
        }
        setupProveedorAutoComplete()
    }

    private fun setupProveedorAutoComplete() {
        // Creamos una variable para guardar el mapa de Nombres a Proveedores
        var proveedoresMap = mapOf<String, Proveedor>()

        // 1. Observamos la lista de proveedores desde el ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.proveedores.collect { proveedoresList ->
                if (proveedoresList.isNotEmpty()) {
                    // Mapeamos los nombres de los proveedores a sus objetos
                    val proveedorNombres = proveedoresList.map { it.nombre }

                    // 2. Creamos y configuramos el adaptador
                    val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu_item, proveedorNombres)
                    binding.actvProveedor.setAdapter(adapter)

                }
            }
        }

        // 4. Manejamos el clic en un elemento de la lista
        binding.actvProveedor.setOnItemClickListener { parent, view, position, id ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedProveedor = proveedoresMap[selectedName]

            selectedProveedorId = selectedProveedor?.id ?: ""

        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        binding.editTextCodigoBarras.error = null
        binding.editTextNombreProducto.error = null
        binding.editTextNombreProductoP.error = null
        binding.editTextPrecioProveedor.error = null
        binding.editTextCantidad.error = null
        binding.editTextPrecioVenta.error = null
        binding.editTextStock.error = null
        binding.editTextStockMinimo.error = null
        binding.tvFechaCaducidad.error = null

        if (binding.editTextCodigoBarras.text.isNullOrBlank()) {
            binding.editTextCodigoBarras.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextNombreProducto.text.isNullOrBlank()) {
            binding.editTextNombreProducto.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextNombreProductoP.text.isNullOrBlank()) {
            binding.editTextNombreProductoP.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextPrecioProveedor.text.isNullOrBlank()) {
            binding.editTextPrecioProveedor.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextCantidad.text.isNullOrBlank()) {
            binding.editTextCantidad.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextPrecioVenta.text.isNullOrBlank()) {
            binding.editTextPrecioVenta.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextStock.text.isNullOrBlank()) {
            binding.editTextStock.error = "Este campo es obligatorio"
            isValid = false
        }
        if (binding.editTextStockMinimo.text.isNullOrBlank()) {
            binding.editTextStockMinimo.error = "Este campo es obligatorio"
            isValid = false
        }
        /*if (binding.tvFechaCaducidad.text.isNullOrBlank()) {
            Snackbar.make(binding.root,"Este campo es obligatorio", Snackbar.LENGTH_LONG).show()
            isValid = false
        }*/

        if (binding.autoCompleteTextViewCategoria.text.isNullOrBlank()) {
            Snackbar.make(binding.root, "La categoría es obligatoria", Snackbar.LENGTH_LONG).show()
            isValid = false
        }
        if (selectedCategoryId.isNullOrBlank()) {
            Snackbar.make(binding.root, "El ID de la categoría es obligatorio", Snackbar.LENGTH_LONG).show()
            isValid = false
        }
        if (binding.autoCompleteTextViewUnitMeasurement.text.isNullOrBlank()) {
            Snackbar.make(binding.root, "La unidad de medida es obligatoria", Snackbar.LENGTH_LONG).show()
            isValid = false
        }

        val unitSupplierPrice = binding.tvUnitSupplierPrice.text.toString()
            .replace("Precio por unidad Proveedor:$ ", "")
            .toDoubleOrNull()
        if (unitSupplierPrice == null || unitSupplierPrice <= 0) {
            Snackbar.make(binding.root, "El precio por unidad del proveedor debe ser válido y mayor que cero", Snackbar.LENGTH_LONG).show()
            isValid = false
        }

        val profit = binding.tvProfit.text.toString()
            .replace(" Ganancia : $", "")
            .toDoubleOrNull()
        if (profit == null) {
            Snackbar.make(binding.root, "La ganancia debe ser un valor válido", Snackbar.LENGTH_LONG).show()
            isValid = false
        }
        return isValid
    }

    private fun calculateUnitSupplierPrice() {
        val providerPrice = binding.editTextPrecioProveedor.text.toString().toDoubleOrNull() ?: 0.0
        val quantity = binding.editTextCantidad.text.toString().toIntOrNull() ?: 0

        val pricePerUnit = if (quantity > 0) {
            providerPrice / quantity.toDouble()
        } else {
            0.0
        }

        pricePerUnitSupplier = pricePerUnit
        binding.tvUnitSupplierPrice.text = String.format("Precio por unidad Proveedor:$ %.2f", pricePerUnit)
        calculateProfit()
    }

    private fun calculateProfit() {
        val salePrice = binding.editTextPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
        val unitSupplierPrice = binding.tvUnitSupplierPrice.text.toString()
            .replace("Precio por unidad Proveedor:$ ", "")
            .toDoubleOrNull() ?: 0.0

        val profitCalculated = salePrice - unitSupplierPrice

        profit = profitCalculated
        binding.tvProfit.text = String.format(" Ganancia : $%.2f", profitCalculated)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
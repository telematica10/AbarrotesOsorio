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

/**
 * Fragmento para la pantalla de edición de un producto.
 * Muestra los detalles de un producto y permite al usuario modificar sus datos.
 */
class ProductoEditFragment : Fragment() {

    // Variable booleana para simular el rol del usuario.
    // TODO: En una app real, este valor se obtendría del sistema de autenticación (Firebase Auth).
    private val esPropietario = true

    // Binding para acceder a las vistas del layout de forma segura.
    private var _binding: FragmentProductoEditBinding? = null
    private val binding get() = _binding!!

    // Usamos Safe Args para recibir el objeto Producto directamente.
    private val args: ProductoEditFragmentArgs by navArgs()

    // ViewModel para la lógica de negocio, inyectado con una Factory.
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

        // 🔹 Paso 1: Obtener el producto de los argumentos de navegación.
        val producto = args.producto
        Log.d("ProductoEditFragment", "Producto recibido para editar: ${producto.nombre_producto}")

        // 🔹 Paso 2: Rellenar los campos con la información del producto.
        populateUI(producto)

        // 🔹 Nuevo paso: Configurar el menú desplegable para la ubicación.
        val ubicaciones = resources.getStringArray(R.array.ubicaciones_array)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_location_product, ubicaciones)
        // El EditText ahora funciona como un AutoCompleteTextView para el dropdown.
        binding.etUbicacion.setAdapter(adapter)

        // 🔹 Paso 3: Configurar la visibilidad de los campos para propietarios.
        // Oculta los campos si el usuario no es propietario.
        if (!esPropietario) {
            binding.tilPrecioVenta.visibility = View.GONE
            binding.tilStockMinimo.visibility = View.GONE
        }

        // 🔹 Paso 4: Configurar los listeners para los botones.
        binding.btnGuardar.setOnClickListener {
            saveChanges(producto)
        }

        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    /**
     * Llena los campos de la UI con la información del producto.
     * @param producto El objeto Producto con los datos a mostrar.
     */
    private fun populateUI(producto: Producto) {
        binding.apply {
            etNombreProducto.setText(producto.nombre_producto)
            etNombreProductoP.setText(producto.nombre_producto_proveedor)
            etCantidad.setText(producto.cantidad.toString())
            etProveedorPreferente.setText(producto.proveedor_preferente)
            etStockActual.setText(producto.stock_actual.toString())
            etUbicacion.setText(producto.ubicacion_en_tienda_almacen, false)
            etFechaCaducidad.setText(producto.fecha_de_caducidad)
            etNotas.setText(producto.notas_observaciones)

            // Campos solo para propietarios
            if (esPropietario) {
                etPrecioVenta.setText(producto.precio_de_venta.toString())
                etStockMinimo.setText(producto.stock_minimo.toString())
            }
        }
    }

    /**
     * Recoge los datos del formulario, crea un objeto Producto actualizado y lo envía al ViewModel.
     * @param productoOriginal El objeto Producto original recibido de los argumentos.
     */
    private fun saveChanges(productoOriginal: Producto) {
        // Recopila los datos de los campos editables.
        val nombreProducto = binding.etNombreProducto.text.toString().trim()
        val proveedorPreferente = binding.etProveedorPreferente.text.toString().trim()
        val stockActual = binding.etStockActual.text.toString().toIntOrNull() ?: 0
        val ubicacion = binding.etUbicacion.text.toString().trim()
        val fechaCaducidad = binding.etFechaCaducidad.text.toString().trim()
        val notas = binding.etNotas.text.toString().trim()

        // Crea una copia del producto con los datos actualizados.
        val productoActualizado = productoOriginal.copy(
            nombre_producto = nombreProducto,
            proveedor_preferente = proveedorPreferente,
            stock_actual = stockActual,
            ubicacion_en_tienda_almacen = ubicacion,
            fecha_de_caducidad = fechaCaducidad,
            notas_observaciones = notas
        )

        // Si el usuario es propietario, actualiza también los campos financieros y de stock mínimo.
        if (esPropietario) {
            val precioVenta = binding.etPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
            val stockMinimo = binding.etStockMinimo.text.toString().toIntOrNull() ?: 0

            productoActualizado.precio_de_venta = precioVenta
            productoActualizado.stock_minimo = stockMinimo
        }

        // Llama al ViewModel para guardar los cambios en la base de datos.
        viewModel.actualizarProducto(productoActualizado, esPropietario)

        // Muestra un mensaje al usuario y navega de regreso.
        Snackbar.make(binding.root, "Producto actualizado", Snackbar.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
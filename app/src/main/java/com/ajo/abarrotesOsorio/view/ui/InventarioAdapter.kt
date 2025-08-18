package com.ajo.abarrotesOsorio.view.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.ItemInventarioBinding

/**
 * Adaptador para mostrar la lista de productos en el inventario.
 * Utiliza un ListAdapter para manejar eficientemente los cambios en la lista.
 *
 * @param onStockChange Callback que se activa cuando el stock de un producto es actualizado.
 * @param onNavigateToEdit Callback para navegar a la pantalla de edición de un producto.
 */
class InventarioAdapter(
    private val onStockChange: (productId: String, newStock: Int) -> Unit,
    private val onNavigateToEdit: (producto: Producto) -> Unit
) : ListAdapter<Producto, InventarioAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding =
            ItemInventarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = getItem(position)
        holder.bind(producto)
    }

    inner class ProductoViewHolder(private val binding: ItemInventarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Listener para el EditText del stock. Se almacena para poder removerlo.
        private var stockTextWatcher: TextWatcher? = null

        fun bind(producto: Producto) {
            // Muestra la información del producto
            binding.tvNombreProducto.text = producto.nombre_producto
            binding.tvNombreProductoP.text = producto.nombre_producto_proveedor
            binding.tvLastProvider.text =
                "Último proveedor: ${producto.proveedor_preferente ?: "N/A"}"

            // 🔹 Configuración inicial del stock
            val stockActual = producto.stock_actual.toString()
            binding.etStock.setText(stockActual)

            // 🔹 Implementación de los botones y el campo de texto
            setupListeners(producto)
        }

        private fun setupListeners(producto: Producto) {
            // Asegurarse de que no haya múltiples listeners
            stockTextWatcher?.let { binding.etStock.removeTextChangedListener(it) }

            // Listener para el botón de incrementar (+)
            binding.btnAdd.setOnClickListener {
                val currentStock = binding.etStock.text.toString().toInt()
                val newStock = currentStock + 1
                binding.etStock.setText(newStock.toString())
                onStockChange(producto.codigo_de_barras_sku, newStock)
            }

            // Listener para el botón de decrementar (-)
            binding.btnRemove.setOnClickListener {
                val currentStock = binding.etStock.text.toString().toInt()
                if (currentStock > 0) {
                    val newStock = currentStock - 1
                    binding.etStock.setText(newStock.toString())
                    onStockChange(producto.codigo_de_barras_sku, newStock)
                }
            }

            // Listener para el cambio de texto manual en el EditText
            stockTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val newStock = s.toString().toInt()
                    onStockChange(producto.codigo_de_barras_sku, newStock)
                }
            }
            binding.etStock.addTextChangedListener(stockTextWatcher)

            // Listener para la navegación a la pantalla de edición
            binding.root.setOnClickListener {
                onNavigateToEdit(producto)
            }
        }
    }

    /**
     * Clase para comparar las diferencias entre los productos de forma eficiente.
     */
    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}

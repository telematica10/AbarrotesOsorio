package com.ajo.abarrotesOsorio.view.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.ItemInventarioBinding

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

        fun bind(producto: Producto) {
            binding.tvNombreProducto.text = producto.nombre_producto
            binding.tvNombreProductoP.text = producto.nombre_producto_proveedor
            binding.tvLastProvider.text =
                "Último proveedor: ${producto.proveedor_preferente ?: "N/A"}"

            val stockActual = producto.stock_actual?.toString() ?: "0"
            binding.etStock.setText(stockActual)

            setupListeners(producto)
        }

        private fun setupListeners(producto: Producto) {
            binding.btnAdd.setOnClickListener {
                val currentStock = binding.etStock.text.toString().toIntOrNull() ?: 0
                val newStock = currentStock + 1
                binding.etStock.setText(newStock.toString())
                onStockChange(producto.id, newStock)
            }

            binding.btnRemove.setOnClickListener {
                val currentStock = binding.etStock.text.toString().toIntOrNull() ?: 0
                if (currentStock > 0) {
                    val newStock = currentStock - 1
                    binding.etStock.setText(newStock.toString())
                    onStockChange(producto.id, newStock)
                }
            }

            binding.etStock.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val nuevoStock = binding.etStock.text.toString().toIntOrNull()
                    if (nuevoStock != null) {
                        onStockChange(producto.id, nuevoStock)
                        Log.d("InventarioAdapter", "Stock actualizado por cambio de foco: ${producto.nombre_producto} -> $nuevoStock")
                    }
                }
            }

            binding.root.setOnClickListener {
                onNavigateToEdit(producto)
            }
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
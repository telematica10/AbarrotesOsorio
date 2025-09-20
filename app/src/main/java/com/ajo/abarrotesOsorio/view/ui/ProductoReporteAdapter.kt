package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.ListItemReporteBinding

class ProductoReporteAdapter : ListAdapter<Producto, ProductoReporteAdapter.ProductoReporteViewHolder>(ProductoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoReporteViewHolder {
        val binding = ListItemReporteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoReporteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoReporteViewHolder, position: Int) {
        val producto = getItem(position)
        holder.bind(producto)
    }

    inner class ProductoReporteViewHolder(private val binding: ListItemReporteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            binding.tvProductoNombre.text = producto.nombre_producto
            binding.tvProductoStock.text = "Stock: ${producto.stock_actual}"
            val precioTexto = String.format("Precio de Proveedor: $%.2f", producto.precio_proveedor)
            binding.tvProductoPrecio.text = precioTexto
        }
    }

    private object ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
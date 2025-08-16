package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Producto
import com.ajo.abarrotesOsorio.databinding.ItemInventarioBinding

class InventarioAdapter(
    private var productos: MutableList<Producto>,
    private val onStockChange: (String, Int) -> Unit
) : RecyclerView.Adapter<InventarioAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemInventarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto, position: Int) {
            binding.tvNombreProducto.text = producto.nombre_producto
            binding.etStock.setText(producto.stock_actual.toString())

            // Colores
            val defaultColor = ContextCompat.getColor(binding.root.context, android.R.color.black)
            val primaryColor = ContextCompat.getColor(binding.root.context, R.color.colorPrimary)

            // Color inicial según stock
            binding.etStock.setTextColor(
                if (producto.stock_actual > 0) primaryColor else defaultColor
            )

            // Animación al cambiar stock
            fun animarCambioStock() {
                binding.etStock.setTextColor(primaryColor)
                binding.etStock.postDelayed({
                    binding.etStock.setTextColor(
                        if (producto.stock_actual > 0) primaryColor else defaultColor
                    )
                }, 500)
            }

            // Botón +
            binding.btnAdd.setOnClickListener {
                val nuevoStock = producto.stock_actual + 1
                producto.stock_actual = nuevoStock
                onStockChange(producto.id, nuevoStock)
                notifyItemChanged(position)
                animarCambioStock()
            }

            // Botón -
            binding.btnRemove.setOnClickListener {
                if (producto.stock_actual > 0) {
                    val nuevoStock = producto.stock_actual - 1
                    producto.stock_actual = nuevoStock
                    onStockChange(producto.id, nuevoStock)
                    notifyItemChanged(position)
                    animarCambioStock()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productos[position], position)
    }

    override fun getItemCount(): Int = productos.size

    // 🔹 Métodos para cambios individuales de Firestore
    fun agregarProductoEnPosicion(producto: Producto, posicion: Int) {
        productos.add(posicion, producto)
        notifyItemInserted(posicion)
    }

    fun actualizarProductoEnPosicion(producto: Producto, posAnterior: Int, posNueva: Int) {
        if (posAnterior == posNueva) {
            productos[posAnterior] = producto
            notifyItemChanged(posAnterior)
        } else {
            productos.removeAt(posAnterior)
            productos.add(posNueva, producto)
            notifyItemMoved(posAnterior, posNueva)
            notifyItemChanged(posNueva)
        }
    }

    fun eliminarProductoEnPosicion(posicion: Int) {
        productos.removeAt(posicion)
        notifyItemRemoved(posicion)
    }


    // Método opcional si necesitas refrescar todo (por búsqueda, etc.)
    fun actualizarLista(nuevaLista: List<Producto>) {
        productos = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }
}

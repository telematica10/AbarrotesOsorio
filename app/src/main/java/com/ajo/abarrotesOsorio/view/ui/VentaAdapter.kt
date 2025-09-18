package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.VentaItem
import com.ajo.abarrotesOsorio.databinding.ItemVentaBinding

class VentaAdapter(private var carrito: MutableList<VentaItem>) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    class VentaViewHolder(val binding: ItemVentaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val binding = ItemVentaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VentaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        val item = carrito[position]
        holder.binding.txtNombre.text = item.nombre
        holder.binding.txtCantidad.text = "x${item.cantidad}"
        holder.binding.txtPrecio.text = String.format("%.2f", item.precio)
        holder.binding.txtSubtotal.text = String.format("%.2f", item.subtotal)
    }

    override fun getItemCount(): Int = carrito.size

    fun actualizarLista(nuevaLista: MutableList<VentaItem>) {
        this.carrito = nuevaLista
        notifyDataSetChanged()
    }
}
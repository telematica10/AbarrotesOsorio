package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.VentaItem
import com.ajo.abarrotesOsorio.databinding.ItemVentaTicketBinding
import java.text.NumberFormat
import java.util.Locale

class VentaTicketAdapter(private val listaProductos: List<VentaItem>) : RecyclerView.Adapter<VentaTicketAdapter.VentaTicketViewHolder>() {

    class VentaTicketViewHolder(private val binding: ItemVentaTicketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VentaItem) {
            val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            binding.txtNombre.text = item.nombre
            binding.txtCantidad.text = "x${item.cantidad}"
            binding.txtPrecio.text = formatter.format(item.precio)
            binding.txtSubtotal.text = formatter.format(item.subtotal)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaTicketViewHolder {
        val binding = ItemVentaTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VentaTicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VentaTicketViewHolder, position: Int) {
        holder.bind(listaProductos[position])
    }

    override fun getItemCount(): Int = listaProductos.size
}
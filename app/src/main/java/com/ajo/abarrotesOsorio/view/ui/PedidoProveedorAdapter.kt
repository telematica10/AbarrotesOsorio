package com.ajo.abarrotesOsorio.view.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.PedidoItem
import com.ajo.abarrotesOsorio.databinding.ItemPedidoProveedorBinding

class PedidoProveedorAdapter(
    val pedidoList: MutableList<PedidoItem>,
    private val onTotalChanged: () -> Unit
) : RecyclerView.Adapter<PedidoProveedorAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(private val binding: ItemPedidoProveedorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var quantityTextWatcher: TextWatcher? = null

        fun bind(item: PedidoItem) {
            binding.apply {
                quantityTextWatcher?.let { cantidadEditText.removeTextChangedListener(it) }

                productoProveedorNombreTv.text = item.nombreProductoProveedor
                nombreProductoTv.text = item.nombreProducto
                precioProveedorTv.text = "Precio/u: $${"%.2f".format(item.precioProveedor)}"
                subtotalTextView.text = "Subtotal: $${"%.2f".format(item.subtotal)}"
                recibidoCheckbox.isChecked = item.recibido

                cantidadEditText.setText(item.cantidadAPedir.toString())

                recibidoCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    item.recibido = isChecked
                    onTotalChanged()
                }

                quantityTextWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        val newQuantity = s.toString().toIntOrNull() ?: 0
                        item.cantidadAPedir = newQuantity
                        item.subtotal = newQuantity * item.precioProveedor
                        subtotalTextView.text = "Subtotal: $${"%.2f".format(item.subtotal)}"
                        onTotalChanged()
                    }
                }
                cantidadEditText.addTextChangedListener(quantityTextWatcher)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoProveedorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(pedidoList[position])
    }

    override fun getItemCount() = pedidoList.size

    fun getUncheckedItems(): List<PedidoItem> {
        return pedidoList.filter { !it.recibido }
    }

    fun updateList(newList: MutableList<PedidoItem>) {
        pedidoList.clear()
        pedidoList.addAll(newList)
        notifyDataSetChanged()
    }
}
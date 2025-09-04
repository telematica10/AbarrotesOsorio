package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.Proveedor
import com.ajo.abarrotesOsorio.databinding.ItemProveedorBinding

class ProveedorAdapter(
    private val onItemClicked: (Proveedor) -> Unit,
    private val onDeleteClicked: (Proveedor) -> Unit,
    private val onEditClicked: (Proveedor) -> Unit
) : ListAdapter<Proveedor, ProveedorAdapter.ProveedorViewHolder>(ProveedorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorViewHolder {
        val binding = ItemProveedorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProveedorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProveedorViewHolder, position: Int) {
        val proveedor = getItem(position)
        holder.bind(proveedor, onItemClicked, onDeleteClicked, onEditClicked)
    }

    class ProveedorViewHolder(private val binding: ItemProveedorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            proveedor: Proveedor,
            onItemClicked: (Proveedor) -> Unit,
            onDeleteClicked: (Proveedor) -> Unit,
            onEditClicked: (Proveedor) -> Unit
        ) {

            binding.proveedor = proveedor

            if (proveedor.nombreVendedor.isNullOrEmpty()) {
                binding.proveedorContactoVendedor.visibility = View.GONE
                binding.proveedorTelefonoVendedor.visibility = View.GONE
                binding.proveedorDiaVisita.visibility = View.GONE
                binding.editButton.visibility = View.GONE
            } else {
                binding.proveedorContactoVendedor.visibility = View.VISIBLE
                binding.proveedorTelefonoVendedor.visibility = View.VISIBLE
                binding.proveedorDiaVisita.visibility = View.VISIBLE
                binding.editButton.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener { onItemClicked(proveedor) }
            binding.deleteButton.setOnClickListener { onDeleteClicked(proveedor) }
            binding.editButton.setOnClickListener { onEditClicked(proveedor) }

            binding.executePendingBindings()

        }
    }
}

class ProveedorDiffCallback : DiffUtil.ItemCallback<Proveedor>() {
    override fun areItemsTheSame(oldItem: Proveedor, newItem: Proveedor): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Proveedor, newItem: Proveedor): Boolean {
        return oldItem == newItem
    }
}
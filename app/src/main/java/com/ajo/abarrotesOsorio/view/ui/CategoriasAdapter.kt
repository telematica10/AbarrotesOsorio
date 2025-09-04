package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.ajo.abarrotesOsorio.databinding.ItemCategoriaBinding
import com.bumptech.glide.Glide

class CategoriaAdapter(
    private val onCategoriaClick: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriaAdapter.ViewHolder>(CategoriaDiffCallback()) {

    inner class ViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria) {
            binding.tvNombreCategoria.text = categoria.nombre

            Glide.with(binding.ivCategoria.context)
                .load(categoria.imagen_url)
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_broken_image)
                .into(binding.ivCategoria)

            binding.root.setOnClickListener {
                onCategoriaClick(categoria)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CategoriaDiffCallback : DiffUtil.ItemCallback<Categoria>() {
    override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
        return oldItem == newItem
    }
}

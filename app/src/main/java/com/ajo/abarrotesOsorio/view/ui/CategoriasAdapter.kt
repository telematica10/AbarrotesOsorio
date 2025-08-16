package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.ajo.abarrotesOsorio.databinding.ItemCategoriaBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class CategoriasAdapter(
    private var categorias: List<Categoria>,
    private val onClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriasAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoria: Categoria) {
            binding.tvNombreCategoria.text = categoria.nombre

            Glide.with(binding.root.context)
                .load(categoria.imagen_url)
                .placeholder(com.ajo.abarrotesOsorio.R.drawable.ic_placeholder) // ícono genérico
                .error(com.ajo.abarrotesOsorio.R.drawable.ic_broken_image) // ícono de error
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade()) // animación suave
                .into(binding.ivCategoria)

            binding.root.setOnClickListener { onClick(categoria) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = categorias.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categorias[position])
    }

    fun actualizarLista(nuevaLista: List<Categoria>) {
        categorias = nuevaLista
        notifyDataSetChanged()
    }
}

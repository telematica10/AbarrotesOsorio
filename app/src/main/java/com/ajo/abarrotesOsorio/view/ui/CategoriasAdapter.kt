package com.ajo.abarrotesOsorio.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.model.Categoria
import com.ajo.abarrotesOsorio.databinding.ItemCategoriaBinding
import com.bumptech.glide.Glide // 🔹 IMPORTANTE: Importar Glide

// 🔹 Heredamos de ListAdapter
class CategoriaAdapter(
    private val onCategoriaClick: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriaAdapter.ViewHolder>(CategoriaDiffCallback()) {

    inner class ViewHolder(private val binding: ItemCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: Categoria) {
            binding.tvNombreCategoria.text = categoria.nombre

            // 🔹 Implementamos la carga de la imagen con Glide
            // La URL de la imagen está en la propiedad 'imagen_url' de tu modelo Categoria.
            // Glide se encarga de descargar y mostrar la imagen en la ImageView.
            Glide.with(binding.ivCategoria.context)
                .load(categoria.imagen_url)
                .centerCrop() // Opcional, pero recomendado para mantener la proporción de la imagen
                .placeholder(R.drawable.ic_placeholder) // Placeholder mientras se carga
                .error(R.drawable.ic_broken_image) // Imagen si hay un error de carga
                .into(binding.ivCategoria) // `ivCategoria` es la ImageView en tu layout

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

    // 🔹 onBindViewHolder se simplifica
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// 🔹 Clase DiffUtil.ItemCallback para comparar las categorías
class CategoriaDiffCallback : DiffUtil.ItemCallback<Categoria>() {
    override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
        return oldItem == newItem
    }
}

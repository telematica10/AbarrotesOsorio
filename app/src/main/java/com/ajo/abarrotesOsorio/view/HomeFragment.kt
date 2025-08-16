package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.ajo.abarrotesOsorio.R
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<MaterialButton>(R.id.btnVenta).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, VentaFragment())
                addToBackStack(null)
            }
        }

        view.findViewById<MaterialButton>(R.id.btnInventario).setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, CategoriasFragment()) // 🔹 Cambiado
                addToBackStack(null)
            }
        }

        return view
    }
}

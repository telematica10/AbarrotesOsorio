package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ajo.abarrotesOsorio.R

class VentaFragment : Fragment() {

    private lateinit var btnScan: Button
    private lateinit var recyclerVenta: RecyclerView
    private lateinit var txtTotal: TextView
    private lateinit var btnGuardar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_venta, container, false)

        btnScan = view.findViewById(R.id.btnScanProducto)
        recyclerVenta = view.findViewById(R.id.recyclerVenta)
        txtTotal = view.findViewById(R.id.txtTotalVenta)
        btnGuardar = view.findViewById(R.id.btnGuardarVenta)

        // Acción escanear (luego conectaremos con ML Kit)
        btnScan.setOnClickListener {
            Toast.makeText(requireContext(), "Escanear producto", Toast.LENGTH_SHORT).show()
        }

        // Acción guardar venta
        btnGuardar.setOnClickListener {
            Toast.makeText(requireContext(), "Venta guardada", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}


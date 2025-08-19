package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.databinding.FragmentVentaBinding
import com.google.firebase.auth.FirebaseAuth

class VentaFragment : Fragment() {

    private var _binding: FragmentVentaBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout usando Data Binding
        _binding = FragmentVentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnScanProducto.setOnClickListener {
            Toast.makeText(requireContext(), "Escanear producto", Toast.LENGTH_SHORT).show()
        }

        binding.btnGuardarVenta.setOnClickListener {
            Toast.makeText(requireContext(), "Venta guardada", Toast.LENGTH_SHORT).show()
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            if (isAdded) {
                findNavController().navigate(R.id.action_ventasFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

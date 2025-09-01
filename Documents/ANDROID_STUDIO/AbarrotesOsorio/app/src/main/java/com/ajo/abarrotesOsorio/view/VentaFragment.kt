package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.data.repository.VentaViewModelFactory
import com.ajo.abarrotesOsorio.data.model.VentaItem
import com.ajo.abarrotesOsorio.databinding.FragmentVentaBinding
import com.ajo.abarrotesOsorio.view.ui.VentaAdapter
import com.ajo.abarrotesOsorio.viewmodel.VentaViewModel
import java.text.NumberFormat
import java.util.Locale

class VentaFragment : Fragment() {

    private var _binding: FragmentVentaBinding? = null
    private val binding get() = _binding!!
    private lateinit var ventaAdapter: VentaAdapter
    private lateinit var ventaViewModel: VentaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ventaViewModel = ViewModelProvider(this, VentaViewModelFactory()).get(VentaViewModel::class.java)

        setFragmentResultListener("codigo_barras_key") { _, bundle ->
            val barcode = bundle.getString("barcode_data")
            if (barcode != null) {
                ventaViewModel.buscarYAgregarProducto(barcode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVentaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerVenta.layoutManager = LinearLayoutManager(context)

        ventaAdapter = VentaAdapter(ventaViewModel.carrito.value ?: mutableListOf())
        binding.recyclerVenta.adapter = ventaAdapter

        ventaViewModel.carrito.observe(viewLifecycleOwner) { nuevaLista ->
            ventaAdapter.actualizarLista(nuevaLista)
            actualizarTotal(nuevaLista)
        }

        ventaViewModel.ventaGuardadaEvent.observe(viewLifecycleOwner) { ventaGuardada ->
            ventaGuardada?.let {
                VentaTicketDialogFragment.newInstance(it).show(
                    childFragmentManager, VentaTicketDialogFragment.TAG
                )
                ventaViewModel.onVentaGuardadaEventConsumed()
            }
        }

        binding.btnScanProducto.setOnClickListener {
            findNavController().navigate(R.id.action_ventasFragment_to_scanFragment)
        }

        binding.btnGuardarVenta.setOnClickListener {
            if (ventaViewModel.carrito.value?.isEmpty() == true) {
                Toast.makeText(context, "El carrito está vacío. Agrega productos para guardar la venta.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ventaViewModel.guardarVenta()
        }
    }

    private fun actualizarTotal(lista: List<VentaItem>) {
        val total = lista.sumOf { it.subtotal }
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        binding.txtTotalVenta.text = formatter.format(total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
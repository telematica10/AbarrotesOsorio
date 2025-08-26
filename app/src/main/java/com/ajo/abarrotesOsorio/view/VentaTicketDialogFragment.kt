package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.data.model.Venta
import com.ajo.abarrotesOsorio.databinding.FragmentVentaTicketBinding
import com.ajo.abarrotesOsorio.utils.Utilities
import com.ajo.abarrotesOsorio.view.ui.VentaTicketAdapter
import java.text.NumberFormat
import java.util.Locale

class VentaTicketDialogFragment : DialogFragment() {

    private var _binding: FragmentVentaTicketBinding? = null
    private val binding get() = _binding!!
    private lateinit var venta: Venta

    companion object {
        const val TAG = "VentaTicketDialogFragment"
        private const val ARG_VENTA = "venta_arg"

        fun newInstance(venta: Venta): VentaTicketDialogFragment {
            val fragment = VentaTicketDialogFragment()
            val args = Bundle().apply {
                putSerializable(ARG_VENTA, venta)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        venta = arguments?.getSerializable(ARG_VENTA) as? Venta ?: throw IllegalArgumentException("Se requiere un objeto Venta")
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVentaTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerTicketProductos.layoutManager = LinearLayoutManager(context)
        val adapter = VentaTicketAdapter(venta.productos)
        binding.recyclerTicketProductos.adapter = adapter

        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))

        binding.txtFechaTicket.text = Utilities.getFormattedDate(venta.fechaVenta)
        binding.txtTotalTicket.text = formatter.format(venta.totalVenta)

        binding.btnAceptar.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
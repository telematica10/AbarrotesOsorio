package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajo.abarrotesOsorio.MainActivity
import com.ajo.abarrotesOsorio.databinding.FragmentPedidoProveedorBinding
import com.ajo.abarrotesOsorio.utils.Utilities
import com.ajo.abarrotesOsorio.viewmodel.PedidoViewModel
import com.ajo.abarrotesOsorio.viewmodel.PedidoViewModelFactory
import com.ajo.abarrotesOsorio.view.ui.PedidoProveedorAdapter
import com.ajo.abarrotesOsorio.view.ui.SearchListener

class PedidoProveedorFragment : Fragment(), SearchListener {

    private var _binding: FragmentPedidoProveedorBinding? = null
    private val binding get() = _binding!!

    private val args: PedidoProveedorFragmentArgs by navArgs()

    private lateinit var pedidoViewModel: PedidoViewModel
    private lateinit var pedidoAdapter: PedidoProveedorAdapter

    private lateinit var proveedorId: String

    private var keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedidoProveedorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        proveedorId = args.proveedorId

        setupViewModel()
        setupUI()
        setupObservers()
        setupListeners()

        keyboardLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (_binding == null) return

                val rect = android.graphics.Rect()
                binding.root.getWindowVisibleDisplayFrame(rect)
                val screenHeight = binding.root.rootView.height
                val keypadHeight = screenHeight - rect.bottom
                val isKeyboardShowing = keypadHeight > screenHeight * 0.15

                if (isKeyboardShowing) {
                    binding.lyTotal.visibility = View.GONE
                    binding.finalizarPedidoButton.visibility = View.GONE
                } else {
                    binding.lyTotal.visibility = View.VISIBLE
                    binding.finalizarPedidoButton.visibility = View.VISIBLE
                }
            }
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as? MainActivity)?.searchListener = this
        (requireActivity() as? MainActivity)?.invalidateOptionsMenu()
    }

    private fun setupViewModel() {
        val viewModelFactory = PedidoViewModelFactory()
        pedidoViewModel = ViewModelProvider(this, viewModelFactory)[PedidoViewModel::class.java]
    }

    private fun setupUI() {
        pedidoAdapter = PedidoProveedorAdapter(mutableListOf()) {
            pedidoViewModel.updateTotal()
            pedidoViewModel.updateTotalProductos()
        }
        binding.pedidoRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pedidoAdapter
        }
        binding.fechaPedidoTextView.text = Utilities.getDate()
    }

    private fun setupObservers() {
        pedidoViewModel.pedidoList.observe(viewLifecycleOwner) { list ->
            pedidoAdapter.updateList(list.toMutableList())
        }
        pedidoViewModel.total.observe(viewLifecycleOwner) { total ->
            binding.totalTextView.text = "Total: $${"%.2f".format(total)}"
        }
        pedidoViewModel.totalProductos.observe(viewLifecycleOwner) { totalProductos ->
            binding.totalPTextView.text = "Total Productos: $totalProductos"
        }
        pedidoViewModel.proveedorNombre.observe(viewLifecycleOwner) { nombre ->
            binding.proveedorTituloTextView.text = "Pedido para: $nombre"
        }
        pedidoViewModel.pedidoGuardado.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                Toast.makeText(requireContext(), "Pedido guardado exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al guardar el pedido", Toast.LENGTH_SHORT).show()
            }
        }

        pedidoViewModel.getProveedorNombre(proveedorId)
        pedidoViewModel.getProductosParaPedido(proveedorId)
    }

    private fun setupListeners(){
        binding.finalizarPedidoButton.setOnClickListener {
            val pedidoConFecha = pedidoAdapter.pedidoList.map { it.copy(fechaPedido = binding.fechaPedidoTextView.text.toString()) }
            pedidoViewModel.finalizarPedido(pedidoConFecha)
        }
    }

    override fun onSearchQuery(query: String) {
        pedidoViewModel.filtrarProductos(query)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardLayoutListener?.let {
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        (requireActivity() as? MainActivity)?.searchListener = null
        _binding = null
    }
}
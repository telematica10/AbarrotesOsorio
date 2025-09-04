package com.ajo.abarrotesOsorio.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            iniciarEscaneo()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    private fun iniciarEscaneo() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("Escanea un código de barras")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents == null) {
            Log.d("ScanFragment", "Escaneo cancelado")
        } else {
            val barcode = result.contents
            Log.d("ScanFragment", "Código de barras escaneado: $barcode")
            val bundle = Bundle()
            bundle.putString("barcode_data", barcode)
            setFragmentResult("codigo_barras_key", bundle)
        }

        parentFragmentManager.popBackStack()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
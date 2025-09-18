package com.ajo.abarrotesOsorio.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ajo.abarrotesOsorio.R
import com.ajo.abarrotesOsorio.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())

    private var authListener: AuthStateListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navigateRunnable = Runnable {
            authListener = AuthStateListener { firebaseAuth ->
                if (isAdded) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        findNavController().navigate(R.id.action_splashFragment_to_ventasFragment)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }
                authListener?.let {
                    FirebaseAuth.getInstance().removeAuthStateListener(it)
                }
            }
            FirebaseAuth.getInstance().addAuthStateListener(authListener!!)
        }

        handler.postDelayed(navigateRunnable, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        authListener?.let {
            FirebaseAuth.getInstance().removeAuthStateListener(it)
        }
        _binding = null
    }
}
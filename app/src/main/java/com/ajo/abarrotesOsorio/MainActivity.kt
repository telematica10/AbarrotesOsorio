package com.ajo.abarrotesOsorio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.ajo.abarrotesOsorio.view.CategoriasFragment
import com.ajo.abarrotesOsorio.view.VentaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Fragment por defecto
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, VentaFragment())
            }
        }

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_venta -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, VentaFragment())
                    }
                    true
                }
                R.id.nav_inventario -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, CategoriasFragment()) // 🔹 Cambiado
                    }
                    true
                }
                else -> false
            }
        }
    }
}

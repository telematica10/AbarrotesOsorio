package com.ajo.abarrotesOsorio

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.ajo.abarrotesOsorio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var isSearchVisible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define los destinos de nivel superior donde se mostrará el logo
        // Los fragmentos de registro y edición NO están aquí porque queremos
        // un comportamiento de navegación diferente (flecha de regreso).
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.ventasFragment,
                R.id.categoriasFragment,
                R.id.proveedoresFragment,
                R.id.perfilFragment,
                R.id.loginFragment,
                R.id.signUpFragment
            )
        )

        setSupportActionBar(binding.topAppBar)
        // Esto configura laActionBar para que use el NavController y la AppBarConfiguration
        // Esto maneja automáticamente la flecha de regreso (up arrow) para destinos no top-level.
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.topAppBar.title = destination.label

            // Determina si el bottomNavigationView debe ser visible
            val showBottomNav = when (destination.id) {
                R.id.loginFragment, R.id.signUpFragment, R.id.splashFragment,
                R.id.registroProductoFragment, R.id.productoEditFragment, R.id.scanFragment -> false
                // Para todos los demás fragmentos, lo mostramos
                else -> true
            }
            binding.bottomNavigationView.visibility = if (showBottomNav) View.VISIBLE else View.GONE

            // Controla la visibilidad y el ícono de la topAppBar
            val showSearch = when (destination.id) {
                R.id.ventasFragment, R.id.productoEditFragment, R.id.perfilFragment,
                R.id.loginFragment,
                R.id.signUpFragment -> false

                else -> true
            }
            // Actualiza la variable de estado y notifica a la Activity que el menú necesita ser reconstruido
            if (isSearchVisible != showSearch) {
                isSearchVisible = showSearch
                invalidateOptionsMenu() // Esto llama a onCreateOptionsMenu
            }

            // Maneja la visibilidad y el ícono de la topAppBar
            when (destination.id) {
                // Para estos fragmentos específicos, aseguramos que la topAppBar se muestre
                // y que NavigationUI maneje la flecha de regreso si es necesario.
                // La 'flecha blanca de navegación' se mostrará automáticamente
                // por setupActionBarWithNavController si el destino NO es un top-level.
                R.id.registroProductoFragment, R.id.productoEditFragment -> {
                    binding.topAppBar.visibility =
                        View.VISIBLE// Asegura que la TopAppBar sea visible
                    // automáticamente cuando el destino NO esté en appBarConfiguration.topLevelDestinations.
                    // Si quisieras un ícono PERSONALIZADO para estos, lo harías aquí.
                }

                R.id.splashFragment, R.id.scanFragment -> {
                    binding.topAppBar.visibility = View.GONE
                }
                else -> {
                    // Para todos los demás destinos, aplicamos la configuración estándar.
                    binding.topAppBar.visibility =
                        View.VISIBLE // Asegura que la TopAppBar sea visible
                    // Si el destino actual es uno de los destinos de nivel superior, muestra el logo.
                    if (appBarConfiguration.topLevelDestinations.contains(destination.id)) {
                        binding.topAppBar.setNavigationIcon(R.drawable.ic_store_logo)
                    } else {
                        // Si NO es un destino de nivel superior (y no es uno de los casos especiales manejados arriba),
                        // NavigationUI.setupActionBarWithNavController ya se encarga de mostrar la flecha de regreso.
                        // No necesitamos hacer nada explícito aquí para la flecha de regreso.
                    }
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        // Configura la visibilidad del ítem de búsqueda
        searchItem.isVisible = isSearchVisible

        // Si el ítem de búsqueda está visible, configura su comportamiento
        if (searchItem.isVisible) {
            val searchView = searchItem.actionView as SearchView

            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    binding.bottomNavigationView.visibility = View.GONE
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    val currentDestinationId = navController.currentDestination?.id ?: -1
                    val isVisible = when (currentDestinationId) {
                        R.id.loginFragment,
                        R.id.signUpFragment, R.id.splashFragment,
                        R.id.registroProductoFragment, R.id.productoEditFragment, R.id.scanFragment -> false

                        else -> true
                    }
                    binding.bottomNavigationView.visibility =
                        if (isVisible) View.VISIBLE else View.GONE
                    return true
                }
            })
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // TODO: Llamar al ViewModel compartido para actualizar la búsqueda
                    Log.d("Search", "Query: $newText")
                    return true
                }
            })
        }


        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }
}
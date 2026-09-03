package com.example.booksy

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.booksy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si no hay estado guardado, cargamos Inicio por defecto
        if (savedInstanceState == null) {
            actualizarSeleccion(binding.tvInicio, InicioFragment())
        }

        // Listeners de la barra de navegación
        binding.navInicio.setOnClickListener {
            actualizarSeleccion(binding.tvInicio, InicioFragment())
        }

        binding.navExplorar.setOnClickListener {
            actualizarSeleccion(binding.tvExplorar, ExplorarFragment())
        }

        binding.navBiblioteca.setOnClickListener {
            actualizarSeleccion(binding.tvBiblioteca, BibliotecaFragment())
        }
    }

    // Función pública para navegar a Explorar desde otros Fragmentos
    fun irAExplorar() {
        actualizarSeleccion(binding.tvExplorar, ExplorarFragment())
    }

    // Función pública para ir a Búsqueda (Se deselecciona todo porque SearchFragment no pertenece a la barra inferior)
    fun irABuscar() {
        // Pasamos null para que ningún botón quede resaltado
        actualizarSeleccion(null, SearchFragment())
    }

    // AHORA ACEPTA NULL para poder deseleccionar todos los botones
    private fun actualizarSeleccion(textViewSeleccionado: TextView?, fragment: Fragment) {
        cargarFragmento(fragment)

        // Colores traídos directamente de TU colors.xml
        val colorGris = ContextCompat.getColor(this, R.color.texto_gris)
        val colorActivo = ContextCompat.getColor(this, R.color.exterior_oscuro)

        val textViews = listOf(binding.tvInicio, binding.tvExplorar, binding.tvBiblioteca)

        for (tv in textViews) {
            if (tv == textViewSeleccionado) {
                // Resaltar el botón activo con tu verde oscuro
                tv.setTextColor(colorActivo)
                tv.setTypeface(null, Typeface.BOLD)
            } else {
                // Quitar resaltado a los demás con tu gris
                tv.setTextColor(colorGris)
                tv.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun cargarFragmento(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
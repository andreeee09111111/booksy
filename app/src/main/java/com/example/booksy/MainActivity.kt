package com.example.booksy

import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.booksy.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter


        resaltarBoton(0)


        binding.navInicio.setOnClickListener { irAPagina(0) }
        binding.navExplorar.setOnClickListener { irAPagina(1) }
        binding.navBiblioteca.setOnClickListener { irAPagina(2) }


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                resaltarBoton(position)
            }
        })
    }

    private fun irAPagina(position: Int) {
        ocultarBusqueda()
        binding.viewPager.setCurrentItem(position, true) // true = con animación de deslizamiento
        resaltarBoton(position)
    }

    // Función pública para navegar a Explorar desde otros Fragmentos
    fun irAExplorar() {
        irAPagina(1)
    }

    // Función pública para ir a Búsqueda (fuera del ViewPager, deselecciona todo)
    fun irABuscar() {
        binding.searchContainer.visibility = android.view.View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.searchContainer, SearchFragment())
            .commit()
        deseleccionarTodo()
    }

    private fun ocultarBusqueda() {
        binding.searchContainer.visibility = android.view.View.GONE
    }

    private fun resaltarBoton(position: Int) {
        ocultarBusqueda()
        val colorGris = ContextCompat.getColor(this, R.color.texto_gris)
        val colorActivo = ContextCompat.getColor(this, R.color.exterior_oscuro)
        val textViews = listOf(binding.tvInicio, binding.tvExplorar, binding.tvBiblioteca)

        textViews.forEachIndexed { index, tv ->
            if (index == position) {
                tv.setTextColor(colorActivo)
                tv.setTypeface(null, Typeface.BOLD)
            } else {
                tv.setTextColor(colorGris)
                tv.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun deseleccionarTodo() {
        val colorGris = ContextCompat.getColor(this, R.color.texto_gris)
        listOf(binding.tvInicio, binding.tvExplorar, binding.tvBiblioteca).forEach {
            it.setTextColor(colorGris)
            it.setTypeface(null, Typeface.NORMAL)
        }
    }
}
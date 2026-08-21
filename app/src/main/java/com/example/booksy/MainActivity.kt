package com.example.booksy

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.booksy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            actualizarSeleccion(binding.tvInicio, InicioFragment())
        }

        binding.navInicio.setOnClickListener {
            actualizarSeleccion(binding.tvInicio, InicioFragment())
        }

        binding.navExplorar.setOnClickListener {
            irAExplorar()
        }

        binding.navBiblioteca.setOnClickListener {
            actualizarSeleccion(binding.tvBiblioteca, BibliotecaFragment())
        }
    }

    fun irAExplorar() {
        actualizarSeleccion(binding.tvExplorar, ExplorarFragment())
    }

    fun irABuscar() {
        cargarFragmento(SearchFragment())

        val textViews = listOf(binding.tvInicio, binding.tvExplorar, binding.tvBiblioteca)
        for (tv in textViews) {
            tv.setTextColor(Color.parseColor("#666666"))
            tv.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun actualizarSeleccion(textViewSeleccionado: TextView, fragment: Fragment) {
        cargarFragmento(fragment)

        val textViews = listOf(binding.tvInicio, binding.tvExplorar, binding.tvBiblioteca)
        for (tv in textViews) {
            tv.setTextColor(Color.parseColor("#666666"))
            tv.setTypeface(null, Typeface.NORMAL)
        }

        textViewSeleccionado.setTextColor(Color.parseColor("#008080"))
        textViewSeleccionado.setTypeface(null, Typeface.BOLD)
    }

    private fun cargarFragmento(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
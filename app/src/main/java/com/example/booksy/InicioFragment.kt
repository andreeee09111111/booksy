package com.example.booksy

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksy.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class InicioFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recomendadosAdapter: LibrosAdapter
    private lateinit var viewModel: LibrosViewModel

    private var todosLosLibros: List<Book> = emptyList()
    private var filtroActual: String = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivSearch.setOnClickListener {
            val mainActivity = activity as? MainActivity
            mainActivity?.irABuscar()
        }

        val database = AppDatabase.getInstance(requireContext())
        val repository = LibrosRepository(database.librosDao())
        val factory = LibrosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LibrosViewModel::class.java]

        setupRecomendados()
        observeBooks()
    }

    private fun setupRecomendados() {
        recomendadosAdapter = LibrosAdapter(
            onFavoriteClick = { book -> viewModel.toggleFavorite(book) }
        )
        binding.rvRecomendados.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recomendadosAdapter
        }
    }

    private fun observeBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allBooks.collectLatest { books ->
                todosLosLibros = books
                mostrarDestacado(books)
                generarFiltros(books)
                aplicarFiltro()
            }
        }
    }

    // ===== Destacado del mes: un solo libro =====
    private fun mostrarDestacado(books: List<Book>) {
        if (books.isEmpty()) return

        val destacado = books.firstOrNull { it.esFavorito } ?: books.maxByOrNull { it.calificacion }!!

        val card = binding.incluirDestacado
        card.tvCategoriaLibro.text = destacado.categoria.uppercase()
        card.tvTituloLibro.text = destacado.titulo
        card.tvAutorLibro.text = destacado.autor
        card.tvCalificacionLibro.text = destacado.calificacion.toString()
        card.ratingBar.rating = destacado.calificacion

        cargarPortadaDestacado(destacado)

        val favColor = if (destacado.esFavorito) Color.parseColor("#E63946") else Color.parseColor("#B0B0B0")
        card.ivFavoritoLibro.setColorFilter(favColor)
        card.ivFavoritoLibro.setOnClickListener {
            viewModel.toggleFavorite(destacado)
        }
    }

    private fun cargarPortadaDestacado(book: Book) {
        val iv = binding.incluirDestacado.ivPortadaLibro
        if (book.portada.isNullOrEmpty()) {
            iv.setImageResource(R.drawable.portada_destacado)
            return
        }
        try {
            val uri = android.net.Uri.parse(book.portada)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            if (bitmap != null) {
                iv.setImageBitmap(bitmap)
            } else {
                iv.setImageResource(R.drawable.portada_destacado)
            }
        } catch (e: Exception) {
            iv.setImageResource(R.drawable.portada_destacado)
        }
    }

    // ===== Botones de filtro por género =====
    private fun generarFiltros(books: List<Book>) {
        binding.containerFiltros.removeAllViews()

        val generos = listOf("Todos") + books.map { it.categoria }.distinct().sorted()

        generos.forEach { genero ->
            val boton = Button(requireContext()).apply {
                text = genero
                textSize = 12f
                isAllCaps = false
                setPadding(32, 16, 32, 16)
                setTextColor(if (genero == filtroActual) Color.WHITE else Color.parseColor("#1E2B25"))
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    if (genero == filtroActual) Color.parseColor("#1E7A6F") else Color.parseColor("#E8E2DC")
                )
                val params = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 16 }
                layoutParams = params

                setOnClickListener {
                    filtroActual = genero
                    generarFiltros(todosLosLibros) // repinta para resaltar el botón activo
                    aplicarFiltro()
                }
            }
            binding.containerFiltros.addView(boton)
        }
    }

    private fun aplicarFiltro() {
        val filtrados = if (filtroActual == "Todos") {
            todosLosLibros
        } else {
            todosLosLibros.filter { it.categoria == filtroActual }
        }
        recomendadosAdapter.updateBooks(filtrados)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
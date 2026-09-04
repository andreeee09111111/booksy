package com.example.booksy

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.booksy.databinding.FragmentExplorarBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExplorarFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!
    private lateinit var librosAdapter: LibrosAdapter
    private lateinit var viewModel: LibrosViewModel

    // Portada elegida en el diálogo que está abierto actualmente
    private var portadaSeleccionada: Uri? = null
    private var ivPreviewActual: ImageView? = null

    // Selector de imagen con permiso persistente (para que la URI siga siendo válida después)
    private val seleccionarImagenLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    // Algunos proveedores no soportan permiso persistente; seguimos igual
                }
                portadaSeleccionada = uri
                ivPreviewActual?.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getInstance(requireContext())
        val repository = LibrosRepository(database.librosDao())
        val factory = LibrosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LibrosViewModel::class.java]

        setupRecyclerView()
        observeBooks()
        setupSearch()
        setupAgregarLibro()
    }

    private fun setupRecyclerView() {
        librosAdapter = LibrosAdapter(
            onFavoriteClick = { book -> viewModel.toggleFavorite(book) }
        )
        binding.rvBooks.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = librosAdapter
        }
    }

    private fun observeBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allBooks.collectLatest { books ->
                librosAdapter.updateBooks(books)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearchExplorar.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etSearchExplorar, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupAgregarLibro() {
        binding.btnAgregarLibro.setOnClickListener {
            mostrarDialogoAgregarLibro()
        }
    }

    private fun mostrarDialogoAgregarLibro() {
        portadaSeleccionada = null

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.agregarlibro, null)

        val etTitulo = dialogView.findViewById<EditText>(R.id.etTitulo)
        val etAutor = dialogView.findViewById<EditText>(R.id.etAutor)
        val etCategoria = dialogView.findViewById<EditText>(R.id.etCategoria)
        val etCalificacion = dialogView.findViewById<EditText>(R.id.etCalificacion)
        val ivPreview = dialogView.findViewById<ImageView>(R.id.ivPortadaPreview)
        val btnSeleccionarPortada = dialogView.findViewById<View>(R.id.btnSeleccionarPortada)

        ivPreviewActual = ivPreview

        btnSeleccionarPortada.setOnClickListener {
            seleccionarImagenLauncher.launch(arrayOf("image/*"))
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Agregar libro")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val titulo = etTitulo.text.toString().trim()
                val autor = etAutor.text.toString().trim()
                val categoria = etCategoria.text.toString().trim()
                val calificacion = etCalificacion.text.toString().toFloatOrNull() ?: 0f

                if (titulo.isEmpty() || autor.isEmpty()) {
                    Toast.makeText(requireContext(), "Título y autor son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val nuevoLibro = Book(
                    titulo = titulo,
                    autor = autor,
                    categoria = if (categoria.isEmpty()) "General" else categoria,
                    calificacion = calificacion.coerceIn(0f, 5f),
                    esFavorito = false,
                    portada = portadaSeleccionada?.toString() // null si no eligió imagen
                )

                viewModel.insert(nuevoLibro)
                Toast.makeText(requireContext(), "Libro agregado", Toast.LENGTH_SHORT).show()
                ivPreviewActual = null
            }
            .setNegativeButton("Cancelar") { _, _ ->
                ivPreviewActual = null
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
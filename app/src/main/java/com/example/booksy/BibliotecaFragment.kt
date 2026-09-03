package com.example.booksy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksy.databinding.FragmentBibliotecaBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BibliotecaFragment : Fragment() {

    private var _binding: FragmentBibliotecaBinding? = null
    private val binding get() = _binding!!
    private lateinit var librosAdapter: LibrosAdapter
    private lateinit var viewModel: LibrosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBibliotecaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        val database = AppDatabase.getInstance(requireContext())
        val repository = LibrosRepository(database.librosDao())
        val factory = LibrosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LibrosViewModel::class.java]

        setupRecyclerView()
        observeFavoriteBooks()
    }

    private fun setupRecyclerView() {
        librosAdapter = LibrosAdapter(
            onFavoriteClick = { book ->
                viewModel.toggleFavorite(book) // Cambiar favorito desde biblioteca también
            }
        )

        binding.rvBiblioteca.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = librosAdapter
        }
    }

    private fun observeFavoriteBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteBooks.collectLatest { books ->
                // CAMBIO AQUÍ: En lugar de updateBooks, usamos submitList
                librosAdapter.submitList(books)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
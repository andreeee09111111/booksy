package com.example.booksy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.booksy.databinding.FragmentBibliotecaBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BibliotecaFragment : Fragment() {

    private var _binding: FragmentBibliotecaBinding? = null
    private val binding get() = _binding!!
    private lateinit var librosAdapter: LibrosAdapter
    private val viewModel: LibrosViewModel by viewModels()

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

        setupRecyclerView()
        setupSwipeToDelete()
        observeFavoriteBooks()
    }

    private fun setupRecyclerView() {
        librosAdapter = LibrosAdapter(
            onFavoriteClick = { book ->
                viewModel.toggleFavorite(book)
            }
        )

        binding.rvBiblioteca.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = librosAdapter
        }
    }

    private fun setupSwipeToDelete() {
        val callback = SwipeToDeleteCallback { position ->
            val book = librosAdapter.currentList.getOrNull(position)
            if (book != null) {
                viewModel.toggleFavorite(book)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.rvBiblioteca)
    }

    private fun observeFavoriteBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteBooks.collectLatest { books ->
                librosAdapter.submitList(books)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
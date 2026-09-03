package com.example.booksy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksy.databinding.FragmentExplorarBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibrosFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!
    private lateinit var librosAdapter: LibrosAdapter
    private lateinit var viewModel: LibrosViewModel

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

        // Inicializar ViewModel con Room
        val database = AppDatabase.getInstance(requireContext())
        val repository = LibrosRepository(database.librosDao())
        val factory = LibrosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LibrosViewModel::class.java]

        setupRecyclerView()
        observeBooks()
    }

    private fun setupRecyclerView() {
        librosAdapter = LibrosAdapter()
        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = librosAdapter
        }
    }

    private fun observeBooks() {
        // Usar lifecycleScope para observar Flow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allBooks.collectLatest { books ->
                librosAdapter.updateBooks(books)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
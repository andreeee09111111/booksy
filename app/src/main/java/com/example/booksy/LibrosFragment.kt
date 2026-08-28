package com.example.booksy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksy.databinding.FragmentExplorarBinding

class LibrosFragment : Fragment() {

    private var _binding: FragmentExplorarBinding? = null
    private val binding get() = _binding!!
    private lateinit var librosAdapter: LibrosAdapter

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

        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        librosAdapter = LibrosAdapter()
        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = librosAdapter
        }
    }

    private fun loadDummyData() {
        val sampleBooks = listOf(
            Book(id = 1, autor = "Antoine de Saint-Exupéry", esFavorito = "Sí"),
            Book(id = 2, autor = "Gabriel García Márquez", esFavorito = "No"),
            Book(id = 3, autor = "George Orwell", esFavorito = "Sí")
        )
        librosAdapter.updateBooks(sampleBooks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
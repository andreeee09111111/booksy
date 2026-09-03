package com.example.booksy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LibrosViewModel(private val repository: LibrosRepository) : ViewModel() {

    val allBooks: Flow<List<Book>> = repository.allLibros

    // ===== NUEVO: Obtener favoritos del repositorio =====
    val favoriteBooks: Flow<List<Book>> = repository.favoriteBooks

    fun insert(book: Book) = viewModelScope.launch {
        repository.insert(book)
    }

    fun update(book: Book) = viewModelScope.launch {
        repository.update(book)
    }

    fun delete(book: Book) = viewModelScope.launch {
        repository.delete(book)
    }

    // ===== FUNCIÓN PARA FAVORITOS =====
    fun toggleFavorite(book: Book) = viewModelScope.launch {
        val updatedBook = book.copy(esFavorito = !book.esFavorito)
        repository.update(updatedBook)
    }
}

class LibrosViewModelFactory(private val repository: LibrosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibrosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibrosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
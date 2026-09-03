package com.example.booksy

import kotlinx.coroutines.flow.Flow

class LibrosRepository(private val librosDao: LibrosDao) {
    val allLibros: Flow<List<Book>> = librosDao.getAllLibros()

    // ===== NUEVO: Obtener favoritos =====
    val favoriteBooks: Flow<List<Book>> = librosDao.getFavoriteBooks()

    suspend fun insert(book: Book) {
        librosDao.insertLibro(book)
    }

    suspend fun update(book: Book) {
        librosDao.updateLibro(book)
    }

    suspend fun delete(book: Book) {
        librosDao.deleteLibro(book)
    }
}
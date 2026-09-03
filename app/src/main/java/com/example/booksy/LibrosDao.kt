package com.example.booksy

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibrosDao {
    @Query("SELECT * FROM libros")
    fun getAllLibros(): Flow<List<Book>>

    // ===== NUEVO: Obtener solo favoritos =====
    @Query("SELECT * FROM libros WHERE esFavorito = 1")
    fun getFavoriteBooks(): Flow<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibro(book: Book)

    @Update
    suspend fun updateLibro(book: Book)

    @Delete
    suspend fun deleteLibro(book: Book)
}
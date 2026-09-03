package com.example.booksy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val autor: String,
    val esFavorito: Boolean = false,
    val categoria: String,
    val calificacion: Float,
    val portada: String? = null // URI de la imagen elegida por el usuario; null = usar portada por defecto
) {
    // Función para cambiar estado de favorito
    fun toggleFavorite(): Book {
        return this.copy(esFavorito = !this.esFavorito)
    }
}